@echo off
chcp 866 > nul
java -Dfile.encoding=CP866 -cp bin com.example.dungeon.Main
pause