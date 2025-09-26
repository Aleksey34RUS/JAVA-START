@echo off
echo Настройка кодировки консоли для русской версии Windows...
reg add "HKCU\Console" /v CodePage /t REG_DWORD /d 65001 /f
echo Кодировка установлена на UTF-8. Перезапустите консоль.
pause