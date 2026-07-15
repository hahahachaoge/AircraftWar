@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set SRC_DIR=src
set OUT_DIR=out
set LIB_DIR=lib
set MAIN_CLASS=cn.edu.scnu.application.Main

echo ========================================
echo   AircraftWar - 飞机大战启动脚本
echo ========================================
echo.

:: 清理旧的编译输出
if exist "%OUT_DIR%" (
    echo [清理] 删除旧的编译缓存...
    rmdir /s /q "%OUT_DIR%" 2>nul
)

:: 收集 Java 源文件
set JAVA_FILES=
for /r "%SRC_DIR%" %%f in (*.java) do (
    set JAVA_FILES=!JAVA_FILES! "%%f"
)

echo [编译] 正在使用 UTF-8 编码编译...
javac -encoding UTF-8 -d "%OUT_DIR%" -cp "%LIB_DIR%\*" !JAVA_FILES!

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 编译失败！请检查上面的错误信息。
    pause
    exit /b 1
)

echo [编译] 编译成功！
echo.
echo [运行] 启动游戏...
java -Dfile.encoding=UTF-8 -cp "%OUT_DIR%;%LIB_DIR%\*" %MAIN_CLASS%

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 运行失败！
    pause
    exit /b 1
)

pause
