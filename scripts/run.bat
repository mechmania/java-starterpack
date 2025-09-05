@echo off
cd /d "%~dp0\.."
java --enable-native-access=ALL-UNNAMED -cp "bot.jar;deps/jna.jar" com.bot.Main %*
