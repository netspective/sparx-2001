@echo off

REM $Id: build.bat,v 1.2 2002-08-09 15:58:53 shahid.shah Exp $

REM **************************************************************************
REM ** This script should be be run from the APP_ROOT\WEB-INF directory.    **
REM ** It is basically a "launcher" for Ant and the actual work is done in  **
REM ** the build.xml file.                                                  **
REM **************************************************************************

if "%JAVA_HOME%" == "" set JAVA_HOME=C:\utils\java\jdk1.3.1


REM **************************************************************************
REM ** NOTE: The command "for %%D in (.) do set BASEDIR" will only work in  **
REM ** Windows NT/2k/XP. If you are not running under Windows NT/2k/XP then **
REM ** you need to replace the line with with                               **
REM **    set BASEDIR=[your path name]                                      **
REM **************************************************************************

for %%D in (.) do set BASEDIR=%%~fD

REM The SPARX_HOME, if not set, is assumed to be at the same level as apps.
REM For example: if application hello is at C:\some\where\hello and this script
REM              is executing from C:\some\where\hello\WEB-INF then the Sparx
REM              home directory is assumed be C:\some\where\Sparx.

if "%SPARX_HOME%" == "" set SPARX_HOME=%BASEDIR%\..\..\Sparx

set APP_CLASSES=%BASEDIR%\classes
set APP_LIB=%BASEDIR%\lib
set APP_BUILD_FILE=%BASEDIR%\build.xml
set SPARX_REDIST_HOME=%SPARX_HOME%\lib\redist

REM **************************************************************************
REM ** Setup location of all the Sparx prerequisites                        **
REM **   Apache Xerces 1.4 or above (http://xml.apache.org)                 **
REM **   Apache Xalan 2.1 or above (http://xml.apache.org)                  **
REM **   Jakarta ORO Matcher 2.0 or above (http://jakarta.apache.org)       **
REM **   Jakarta Log4J 1.1 or above (http://jakarta.apache.org)             **
REM **   Java Servlet API 2.2 or above (http://java.sun.com)                **
REM **   Java JDBC 2.0 Standard Extensions (http://java.sun.com)            **
REM **************************************************************************

set SPARX_JAR=%APP_LIB%\sparx.jar
set ANT_JAR=%APP_LIB%\ant.jar
set XERCES_JAR=%APP_LIB%\xerces.jar
set XALAN_JAR=%APP_LIB%\xalan.jar
set OROMATCHER_JAR=%APP_LIB%\oro.jar
set LOG4J_JAR=%APP_LIB%\log4j.jar
set BSF_JAR=%APP_LIB%\bsf.jar
set BSF_JS_JAR=%APP_LIB%\js.jar

set SERVLETAPI_JAR=%SPARX_REDIST_HOME%\servlet.jar
set JDBC2X_JAR=%SPARX_REDIST_HOME%\jdbc.jar

if exist "%JAVA_HOME%/lib/tools.jar" set JAVACP=%JAVA_HOME%/lib/tools.jar
if exist "%JAVA_HOME%/lib/classes.zip" set JAVACP=%CLASSPATH%;%JAVA_HOME%/lib/classes.zip

set USE_CLASS_PATH=%APP_CLASSES%;%XERCES_JAR%;%SPARX_JAR%;%OROMATCHER_JAR%;%LOG4J_JAR%;%SERVLETAPI_JAR%;%JDBC2X_JAR%;%XALAN_JAR%;%JAVACP%;%ANT_JAR%;%BSF_JAR%;%BSF_JS_JAR%

REM **************************************************************************
REM ** Now that all the variables are set, execute Ant                      **
REM **************************************************************************

java -classpath %USE_CLASS_PATH% org.apache.tools.ant.Main -Dbasedir=%BASEDIR% -buildfile %APP_BUILD_FILE% %1 %2 %3 %4 %5
