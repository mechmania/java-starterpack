@echo off
cd /d "%~dp0\.."
if not exist build mkdir build
javac -cp "deps/jna.jar" -d build src/com/bot/*.java src/com/bot/core/*.java src/com/bot/strategy/*.java
jar cfm bot.jar manifest.txt -C build .
