@echo off
echo Building XAF...
echo.

set SAVECP=%CLASSPATH%
set SAVEANTHOME=%ANT_HOME%
set SAVEJAVAHOME=%JAVA_HOME%
set SAVERESINHOME=%RESIN_HOME%

if "%POOLMAN_HOME%" == "" set POOLMAN_HOME=D:\Utils\Poolman-2.0.3
if "%JAVA_HOME%" == "" set JAVA_HOME=D:\jdk1.3.1
if "%ANT_HOME%" == "" set ANT_HOME=D:\jakarta-ant-1.3
if "%RESIN_HOME%" == "" set RESIN_HOME=D:\resin-1.2.5
if "%OROMATCHER_HOME%" == "" set OROMATCHER_HOME=D:\Utils\jakarta-oro-2.0.3

REM === This automatically adds system classes to CLASSPATH ===
if exist "%JAVA_HOME%\lib\tools.jar" set CLASSPATH=%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%\lib\classes.zip" set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\classes.zip
set CLASSPATH=.;%CLASSPATH%;%ANT_HOME%\lib\ant.jar;%ANT_HOME%\lib\xerces.jar

echo Starting Ant...
%ANT_HOME%\bin\ant -buildfile build.xml %1

goto end


:end
rem Cleanup environment variables
set CLASSPATH=%SAVECP%
echo classpath set to %CLASSPATH%
set SAVECP=
set ANT_HOME=%SAVEANTHOME%
set JAVA_HOME=%SAVJAVAHOME%
set RESIN_HOME=%SAVERESINHOME%