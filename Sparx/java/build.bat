@echo off

REM $Id: build.bat,v 1.5 2002-08-23 16:51:20 shahid.shah Exp $

REM **************************************************************************
REM ** This script should be be run from the SPARX_HOME\java directory.     **
REM ** It is basically a "launcher" for Ant and the actual work is done in  **
REM ** the build.xml file.                                                  **
REM **************************************************************************

REM **************************************************************************
REM ** NOTE: The command "for %%D in (.) do set BASEDIR" will only work in  **
REM ** Windows NT/2k/XP. If you are not running under Windows NT/2k/XP then **
REM ** you need to replace the line with with                               **
REM **    set BASEDIR=[your path name]                                      **
REM ** The SPARX_HOME, if not set, is assumed to be one level above this    **
REM ** directory                                                            **
REM **************************************************************************

for %%D in (.) do set BASEDIR=%%~fD

if "%JAVA_HOME%" == "" set JAVA_HOME=C:\utils\java\jdk1.3.1
if "%SPARX_HOME%" == "" set SPARX_HOME=%BASEDIR%\..

echo JAVA_HOME is %JAVA_HOME%
echo SPARX_HOME is %SPARX_HOME%

REM **************************************************************************
REM ** Setup location of all the Sparx prerequisites                        **
REM **   Apache Xerces 1.4 or above (http://xml.apache.org)                 **
REM **   Apache Xalan 2.1 or above (http://xml.apache.org)                  **
REM **   Jakarta ORO Matcher 2.0 or above (http://jakarta.apache.org)       **
REM **   Jakarta Log4J 1.1 or above (http://jakarta.apache.org)             **
REM **   Java Servlet API 2.2 or above (http://java.sun.com)                **
REM **   Java JDBC 2.0 Standard Extensions (http://java.sun.com)            **
REM **************************************************************************

set SPARX_REDIST_LIB=%SPARX_HOME%\lib\redist

set ANT_JAR=%SPARX_REDIST_LIB%\ant.jar;%SPARX_REDIST_LIB%\ant-optional.jar
set XMLAPIS_JAR=%SPARX_REDIST_LIB%\xml-apis.jar
set XERCES_JAR=%SPARX_REDIST_LIB%\xerces.jar
set XALAN_JAR=%SPARX_REDIST_LIB%\xalan.jar
set OROMATCHER_JAR=%SPARX_REDIST_LIB%\oro.jar
set LOG4J_JAR=%SPARX_REDIST_LIB%\log4j.jar
set BSF_JAR=%SPARX_REDIST_LIB%\bsf.jar
set BSF_JS_JAR=%SPARX_REDIST_LIB%\js.jar

set SERVLETAPI_JAR=%SPARX_REDIST_LIB%\servlet.jar
set JDBC2X_JAR=%SPARX_REDIST_LIB%\jdbc.jar

if exist "%JAVA_HOME%/lib/tools.jar" set JAVACP=%JAVA_HOME%/lib/tools.jar
if exist "%JAVA_HOME%/lib/classes.zip" set JAVACP=%CLASSPATH%;%JAVA_HOME%/lib/classes.zip

set USE_CLASS_PATH=%XMLAPIS_JAR%;%XERCES_JAR%;%OROMATCHER_JAR%;%LOG4J_JAR%;%SERVLETAPI_JAR%;%JDBC2X_JAR%;%XALAN_JAR%;%JAVACP%;%ANT_JAR%;%BSF_JAR%;%BSF_JS_JAR%

REM **************************************************************************
REM ** Now that all the variables are set, execute Ant                      **
REM **************************************************************************

java -classpath %USE_CLASS_PATH% org.apache.tools.ant.Main -Dbasedir=%BASEDIR% -buildfile build.xml %1 %2 %3 %4 %5
