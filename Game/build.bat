@echo off
chcp 866 > nul
javac -encoding UTF-8 -d bin src/com/example/dungeon/*.java src/com/example/dungeon/core/*.java src/com/example/dungeon/model/*.java
if %errorlevel% equ 0 (
    echo Сборка завершена успешно!
) else (
    echo Ошибка сборки!
    pause
)