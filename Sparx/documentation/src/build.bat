@echo off

set SPARX_HOME=C:\Projects\Sparx
set BUILD_FILE=build.xml
set BASE_DIR=%SPARX_HOME%\documentation\src
set SPARX_REDIST_HOME=S:\utils\java\lib\sparx-redist

set JAVA_HOME=C:\utils\java\jdk1.3.1
set ANT_HOME=S:\utils\java\lib\jakarta-ant-1.4.1

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

java -Dant.home=%ANT_HOME% -classpath %USE_CLASS_PATH%;%ANT_HOME%\lib\ant.jar;%ANT_HOME%\lib\ant-optional.jar org.apache.tools.ant.Main -Dbasedir=%BASE_DIR% -buildfile %BUILD_FILE% %1 %2 %3 %4 %5
