@ECHO OFF

set bin=bin-test

if exist "%bin%" rd /S /Q "%bin%"
md "%bin%"

echo Compiling sources...
"C:/Users/Administrator/Desktop/server-jdk/bin/javac" -d %bin% -cp lib/*;src src\net\gielinor\Bootstrap.java
echo Finished compiling sources.

PAUSE