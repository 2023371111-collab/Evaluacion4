$ErrorActionPreference = "Stop"
docker compose up -d --build
docker compose ps
Write-Host "Frontend: http://localhost:8083"
Write-Host "Eureka:   http://localhost:8761"
Write-Host "Gateway:  http://localhost:8080"
Write-Host "Auth:     http://localhost:9000"
