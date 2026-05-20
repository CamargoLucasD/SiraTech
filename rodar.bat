@echo off
set DB_PATH=%USERPROFILE%\Desktop\Windows\Programas\siratech\siratech.db
java -Xms64m -Xmx256m -DDB_PATH="%DB_PATH%" -cp "target\classes;target\dependency\*" frontend.frontend
pause
