@echo off

REM Copia o hibernate.cfg.xml para a pasta out
xcopy /y resources\hibernate.cfg.xml out\

REM Compila
javac -cp ".;lib/*" -d out backend\*.java frontend\*.java

REM Roda
java -cp ".;out;lib/*" frontend.frontend

pause