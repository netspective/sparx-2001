@echo off

REM $Id: build.bat,v 1.1 2002-09-30 03:45:21 shahid.shah Exp $

if "%JAVA_HOME%" == "" echo Error: JAVA_HOME environment variable is not set. && goto end
if "%SPARX_HOME%" == "" set SPARX_HOME=..\..\Sparx

if "%JAVACMD%" == "" set JAVACMD=%JAVA_HOME%\bin\java
if not exist "%JAVACMD%.exe" echo Error: "%JAVACMD%.exe" not found - check JAVA_HOME && goto end

if exist "%JAVA_HOME%/lib/tools.jar" set JAVACP=%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%/lib/classes.zip" set JAVACP=%JAVACP%;%JAVA_HOME%\lib\classes.zip

%JAVACMD% -classpath lib/ant.jar;lib/xerces.jar;%JAVACP% org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

:end