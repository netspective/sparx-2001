@echo off
echo Building XAF...
echo.

set SAVECP=%CLASSPATH%
set SAVEANTHOME=%ANT_HOME%
set SAVEJAVAHOME=%JAVA_HOME%
set SAVERESINHOME=%RESIN_HOME%

set JAVA_HOME=C:\utils\JDK1.3
set ANT_HOME=C:\utils\jakarta-ant-1.3
set RESIN_HOME=C:\utils\resin-1.2.5

REM === This automatically adds system classes to CLASSPATH ===
if exist "%JAVA_HOME%\lib\tools.jar" set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%\lib\classes.zip" set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\classes.zip
set CLASSPATH=.;%CLASSPATH%;%ANT_HOME%\lib\ant.jar;%ANT_HOME%\lib\xerces.jar

echo Starting Ant...
%ANT_HOME%\bin\ant -buildfile build.xml %1

goto end


:end
rem Cleanup environment variables
set CLASSPATH=%SAVECP%
set SAVECP=
set ANT_HOME=%SAVEANTHOME%
set JAVA_HOME=%SAVJAVAHOME%
set RESIN_HOME=%SAVERESINHOME%