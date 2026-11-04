@ECHO OFF

title Game Server [LIVE]

set /p input=Do you want to compile the server? (y/n)
if %input%==y echo CALL build-live.cmd

set java="C:/Users/Administrator/Desktop/server-jdk/jre/bin/java"
set vmargs=-Xms1G -Xmx1G -server -XX:+HeapDumpOnOutOfMemoryError -Dvisualvm.display.name=GielinorServer
set classpath=bin-prod;lib/*
set appargs=--port 43594 --onlinemode true --host 69.197.7.147
set mainclass=com.koios.Bootstrap

%java% %vmargs% -cp %classpath%; %mainclass% %appargs%

PAUSE