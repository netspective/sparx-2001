@echo off

REM **************************************************************************
REM ** Setup location of Sparx distribution, application build file, and    **
REM ** the Sparx JAR file. Just set SPARX_HOME and the others will be set.  **
REM **************************************************************************

set SPARX_HOME=C:\Projects\Sparx
set SPARX_JAR=%SPARX_HOME%\lib\sparx.jar
set SPARX_REDIST_HOME=%SPARX_HOME%\lib\redist
set APP_BUILD_FILE=%SPARX_HOME%\tools\app-build.xml


REM **************************************************************************
REM ** It's important to set the Application's root directory so that the   **
REM ** build file can use APP_ROOT/Site/WEB-INF/classes to locate and       **
REM ** compile the application's java classes and to do automatic setup of  **
REM ** configuration files using the target 'setup-sparx'. If you are using **
REM ** Windows 2000, XP, or NT you do not need to set APP_ROOT, it will be  **
REM ** set to the current working directory automatically.                  **
REM **                                                                      **
REM ** NOTE: The command "for %%D in (.) do set APP_ROOT" will only work in **
REM ** Windows NT/2k/XP. If you are not running under Windows NT/2k/XP then **
REM ** you need to replace the line with with                               **
REM **    set APP_ROOT=[your path name]                                     **
REM **************************************************************************

for %%D in (.) do set APP_ROOT=%%~fD
set APP_CLASSES=%APP_ROOT%\Site\WEB-INF\classes


REM **************************************************************************
REM ** Setup location of all the Sparx prerequisites                        **
REM **   Apache Xerces 1.4 or above (http://xml.apache.org)                 **
REM **   Apache Xalan 2.1 or above (http://xml.apache.org)                  **
REM **   Jakarta ORO Matcher 2.0 or above (http://jakarta.apache.org)       **
REM **   Jakarta Log4J 1.1 or above (http://jakarta.apache.org)             **
REM **   Java Servlet API 2.2 or above (http://java.sun.com)                **
REM **   Java JDBC 2.0 Standard Extensions (http://java.sun.com)            **
REM **************************************************************************

set JAVA_HOME=C:\utils\java\jdk1.3.1

set ANT_JAR=%SPARX_REDIST_HOME%\ant.jar
set XERCES_JAR=%SPARX_REDIST_HOME%\xerces.jar
set XALAN_JAR=%SPARX_REDIST_HOME%\xalan.jar
set OROMATCHER_JAR=%SPARX_REDIST_HOME%\oro.jar
set LOG4J_JAR=%SPARX_REDIST_HOME%\log4j.jar
set BSF_JAR=%SPARX_REDIST_HOME%\bsf.jar
set BSF_JS_JAR=%SPARX_REDIST_HOME%\js.jar
set SERVLETAPI_JAR=%SPARX_REDIST_HOME%\servlet.jar
set JDBC2X_JAR=%SPARX_REDIST_HOME%\jdbc.jar

if exist "%JAVA_HOME%/lib/tools.jar" set JAVACP=%JAVA_HOME%/lib/tools.jar
if exist "%JAVA_HOME%/lib/classes.zip" set JAVACP=%CLASSPATH%;%JAVA_HOME%/lib/classes.zip

set USE_CLASS_PATH=%APP_CLASSES%;%XERCES_JAR%;%SPARX_JAR%;%OROMATCHER_JAR%;%LOG4J_JAR%;%SERVLETAPI_JAR%;%JDBC2X_JAR%;%XALAN_JAR%;%JAVACP%;%ANT_JAR%;%BSF_JAR%;%BSF_JS_JAR%


REM **************************************************************************
REM ** Now that all the variables are set, execute Ant                      **
REM **************************************************************************

java -classpath %USE_CLASS_PATH% org.apache.tools.ant.Main -Dbasedir=%APP_ROOT% -buildfile %APP_BUILD_FILE% %1 %2 %3 %4 %5
