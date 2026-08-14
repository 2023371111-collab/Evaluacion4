param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$AuthUrl = "http://localhost:9000",
    [int]$Attempts = 10
)

$ErrorActionPreference = "Stop"

function Wait-Http([string]$Uri, [int]$TimeoutSeconds = 180) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        try {
            $response = Invoke-WebRequest -Uri $Uri -UseBasicParsing -TimeoutSec 5
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) { return }
        } catch { }
        Start-Sleep -Seconds 3
    } while ((Get-Date) -lt $deadline)
    throw "Tiempo agotado esperando $Uri"
}

Wait-Http "$AuthUrl/actuator/health"
Wait-Http "$BaseUrl/actuator/health"

$unauthorizedStatus = 0
try {
    $unauthorizedResponse = Invoke-WebRequest -Uri "$BaseUrl/api/entity-a" -UseBasicParsing -TimeoutSec 10
    $unauthorizedStatus = [int]$unauthorizedResponse.StatusCode
} catch {
    if ($_.Exception.Response) {
        $unauthorizedStatus = [int]$_.Exception.Response.StatusCode
    } else {
        throw
    }
}
if ($unauthorizedStatus -ne 401) {
    throw "Se esperaba 401 sin token y se obtuvo $unauthorizedStatus"
}

$clientId = if ($env:OAUTH_CLIENT_ID) { $env:OAUTH_CLIENT_ID } else { "frontend-client" }
$clientSecret = if ($env:OAUTH_CLIENT_SECRET) { $env:OAUTH_CLIENT_SECRET } else { "secret123" }
$basic = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("${clientId}:${clientSecret}"))
$tokenResponse = Invoke-RestMethod -Method Post -Uri "$AuthUrl/oauth2/token" -Headers @{ Authorization = "Basic $basic" } -ContentType "application/x-www-form-urlencoded" -Body "grant_type=client_credentials&scope=read%20write" -TimeoutSec 15
$headers = @{ Authorization = "Bearer $($tokenResponse.access_token)" }

$instancesA = @()
$instancesB = @()
$lastA = $null
$lastB = $null
1..$Attempts | ForEach-Object {
    $responseA = Invoke-WebRequest -Uri "$BaseUrl/api/entity-a" -Headers $headers -UseBasicParsing -TimeoutSec 15
    $responseB = Invoke-WebRequest -Uri "$BaseUrl/api/entity-b" -Headers $headers -UseBasicParsing -TimeoutSec 15
    $instancesA += [string]$responseA.Headers["X-Service-Instance"]
    $instancesB += [string]$responseB.Headers["X-Service-Instance"]
    $lastA = $responseA.Content | ConvertFrom-Json
    $lastB = $responseB.Content | ConvertFrom-Json
}

$uniqueA = @($instancesA | Where-Object { $_ } | Sort-Object -Unique)
$uniqueB = @($instancesB | Where-Object { $_ } | Sort-Object -Unique)
if ($uniqueA.Count -lt 2) { throw "No se comprobo balanceo en A. Instancias observadas: $($uniqueA -join ', ')" }
if ($uniqueB.Count -lt 2) { throw "No se comprobo balanceo en B. Instancias observadas: $($uniqueB -join ', ')" }
if (-not ($lastB | Where-Object { $_.nombreA })) { throw "B no devolvio datos combinados desde A mediante Feign/Eureka" }

$eureka = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Headers @{ Accept = "application/json" } -TimeoutSec 15
$registered = @($eureka.applications.application | ForEach-Object {
    [PSCustomObject]@{ name = $_.name; instances = @($_.instance).Count }
})

$result = [ordered]@{
    timestamp = (Get-Date).ToString("o")
    unauthorized_without_token = $unauthorizedStatus
    token_type = $tokenResponse.token_type
    microservice_a_instances = $uniqueA
    microservice_b_instances = $uniqueB
    eureka_registrations = $registered
    entity_a_records = @($lastA).Count
    entity_b_records = @($lastB).Count
    feign_join_verified = [bool]($lastB | Where-Object { $_.nombreA })
    status = "PASS"
}

$result | ConvertTo-Json -Depth 6
