@echo off
setlocal

echo ============================================
echo   Starting Full Project (Backend + Frontend)
echo ============================================
echo.

REM ---- CONFIG ----
set BACKEND_DIR=backend
set FRONTEND_DIR=frontend
set BACKEND_PORT=8080
set FRONTEND_PORT=5173

REM ---- CHECK BACKEND DIRECTORY ----
if not exist "%BACKEND_DIR%" (
    echo [ERROR] Backend directory "%BACKEND_DIR%" not found.
    pause
    exit /b 1
)

REM ---- CHECK FRONTEND DIRECTORY ----
if not exist "%FRONTEND_DIR%" (
    echo [ERROR] Frontend directory "%FRONTEND_DIR%" not found.
    pause
    exit /b 1
)

REM ---- START BACKEND ----
echo [1/3] Starting backend...
start "Backend" cmd /k "cd %BACKEND_DIR% && mvn spring-boot:run"
echo Backend launching on port %BACKEND_PORT%.
echo Waiting 8 seconds for backend to initialize...
timeout /t 8 >nul

REM ---- START FRONTEND ----
echo.
echo [2/3] Starting frontend...
start "Frontend" cmd /k "cd %FRONTEND_DIR% && npm start"
echo Frontend launching on port %FRONTEND_PORT%.
echo Waiting 5 seconds for frontend to initialize...
timeout /t 5 >nul

REM ---- OPEN BROWSER ----
echo.
echo [3/3] Opening browser...
start http://localhost:%FRONTEND_PORT%

echo.
echo ============================================
echo   All systems started successfully!
echo   Backend:  http://localhost:%BACKEND_PORT%
echo   Frontend: http://localhost:%FRONTEND_PORT%
echo ============================================
echo.

pause