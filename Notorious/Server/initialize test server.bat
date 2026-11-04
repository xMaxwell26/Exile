@ECHO OFF

title Game Server [DEVELOPMENT]

set /p input=Do you want to compile the server? (y/n)
if %input%==y CALL build-test.cmd

set java="C:/Users/Administrator/Desktop/server-jdk/bin/java"
set vmargs=-Xms1G -Xmx1G -server -XX:+HeapDumpOnOutOfMemoryError -Dvisualvm.display.name=GielinorServer
set classpath=bin-test;lib/*
set appargs=--port 43594 --onlinemode false --host 72.20.54.106
set mainclass=com.koios.Bootstrap

%java% %vmargs% -cp %classpath%; %mainclass% %appargs%

PAUSE