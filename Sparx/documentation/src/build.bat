@echo off

REM $Id: build.bat,v 1.4 2002-09-16 02:07:20 shahid.shah Exp $

REM **************************************************************************
REM ** This script should be be run from the SPARX_HOME\java directory.     **
REM ** It is basically a "launcher" for Ant and the actual work is done in  **
REM ** the build.xml file.                                                  **
REM **************************************************************************

if "%JAVA_HOME%" == "" echo Error: JAVA_HOME environment variable is not set. && goto end
if "%SPARX_HOME%" == "" set SPARX_HOME=..\..
set SPARX_REDIST_LIB=%SPARX_HOME%\lib\redist

if "%JAVACMD%" == "" set JAVACMD=%JAVA_HOME%\bin\java
if not exist "%JAVACMD%.exe" echo Error: "%JAVACMD%.exe" not found - check JAVA_HOME && goto end

if exist "%JAVA_HOME%/lib/tools.jar" set JAVACP=%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%/lib/classes.zip" set JAVACP=%JAVACP%;%JAVA_HOME%\lib\classes.zip

%JAVACMD% -classpath .;%SPARX_HOME%\lib\sparx.jar;%SPARX_REDIST_LIB%\oro.jar;%SPARX_REDIST_LIB%\syntax.jar;%SPARX_REDIST_LIB%\ant.jar;%SPARX_REDIST_LIB%\ant-optional.jar;%SPARX_REDIST_LIB%\xerces.jar;%JAVACP% org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

:end