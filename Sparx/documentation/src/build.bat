@echo off

REM $Id: build.bat,v 1.3 2002-08-19 16:53:01 shahid.shah Exp $

REM **************************************************************************
REM ** This script should be be run from the SPARX_HOME\documentation\src   **
REM ** directory. It is basically a "launcher" for Ant and the actual work  **
REM ** is done in the build.xml file.                                       **
REM **************************************************************************

REM **************************************************************************
REM ** NOTE: The command "for %%D in (.) do set BASEDIR" will only work in  **
REM ** Windows NT/2k/XP. If you are not running under Windows NT/2k/XP then **
REM ** you need to replace the line with with                               **
REM **    set BASEDIR=[your path name]                                      **
REM ** The SPARX_HOME, if not set, is assumed to be two levels above this   **
REM ** directory                                                            **
REM **************************************************************************

for %%D in (.) do set BASEDIR=%%~fD

if "%JAVA_HOME%" == "" set JAVA_HOME=C:\utils\java\jdk1.3.1
if "%SPARX_HOME%" == "" set SPARX_HOME=%BASEDIR%\..\..

echo JAVA_HOME is %JAVA_HOME%
echo SPARX_HOME is %SPARX_HOME%

set BUILD_FILE=build.xml
set SPARX_REDIST_HOME=%SPARX_HOME%\lib\redist

set ANT_JAR=%SPARX_REDIST_HOME%\ant.jar;%SPARX_REDIST_HOME%\ant-optional.jar
set XERCES_JAR=%SPARX_REDIST_HOME%\xerces.jar
set XALAN_JAR=%SPARX_REDIST_HOME%\xalan.jar
set OROMATCHER_JAR=%SPARX_REDIST_HOME%\oro.jar
set LOG4J_JAR=%SPARX_REDIST_HOME%\log4j.jar
set SERVLETAPI_JAR=%SPARX_REDIST_HOME%\servlet.jar
set JDBC2X_JAR=%SPARX_REDIST_HOME%\jdbc.jar

set SPARX_JAR=%SPARX_HOME%\lib\sparx.jar

if exist "%JAVA_HOME%\lib\tools.jar" set JAVACP=%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%\lib\classes.zip" set JAVACP=%CLASSPATH%;%JAVA_HOME%\lib\classes.zip

set USE_CLASS_PATH=%APP_CLASSES%;%XERCES_JAR%;%SPARX_JAR%;%OROMATCHER_JAR%;%LOG4J_JAR%;%SERVLETAPI_JAR%;%JDBC2X_JAR%;%XALAN_JAR%;%JAVACP%

java -classpath %USE_CLASS_PATH%;%ANT_JAR% org.apache.tools.ant.Main -Dbasedir=%BASEDIR% -buildfile %BUILD_FILE% %1 %2 %3 %4 %5
