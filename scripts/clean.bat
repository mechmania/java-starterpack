@echo off
cd /d "%~dp0\.."
if exist build rmdir /s /q build
if exist bot.jar del bot.jar
