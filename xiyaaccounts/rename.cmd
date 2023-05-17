@echo off
set projectname=%~p0
set projectname=%projectname:\= %
for %%a in (%projectname%) do set projectname=%%a

cd target
del/q %projectname%.war
del/q %projectname%.war.original
ren %projectname%-encrypted.war %projectname%.war
cd ..
echo [INFO] Rename ok!
