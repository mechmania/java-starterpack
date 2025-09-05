@echo off
cd /d "%~dp0\.."
java -cp "bot.jar;deps/jna.jar" com.bot.Main %*
