@ECHO OFF

:: Production directory - not to be modified except stable versions.
set bin=bin-prod

if exist "%bin%" rd /S /Q "%bin%"
md "%bin%"

echo Compiling sources...
"C:/Users/Administrator/Desktop/server-jdk/bin/javac" -d %bin% -cp lib/*;src src\net\gielinor\Bootstrap.java
echo Finished compiling sources.

PAUSE