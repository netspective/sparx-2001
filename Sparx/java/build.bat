@echo off

set BUILD_FILE=build.xml

set JAVA_HOME=C:\utils\java\jdk1.3.1
set SPARX_REDIST_HOME=S:\utils\java\lib\sparx-redist

set ANT_JAR=%SPARX_REDIST_HOME%\ant.jar
set XERCES_JAR=%SPARX_REDIST_HOME%\xerces.jar
set XALAN_JAR=%SPARX_REDIST_HOME%\xalan.jar
set OROMATCHER_JAR=%SPARX_REDIST_HOME%\oro.jar
set LOG4J_JAR=%SPARX_REDIST_HOME%\log4j.jar
set SERVLETAPI_JAR=%SPARX_REDIST_HOME%\servlet.jar
set JDBC2X_JAR=%SPARX_REDIST_HOME%\jdbc.jar

if exist "%JAVA_HOME%\lib\tools.jar" set JAVACP=%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%\lib\classes.zip" set JAVACP=%CLASSPATH%;%JAVA_HOME%\lib\classes.zip

set USE_CLASS_PATH=%XERCES_JAR%;%SPARX_JAR%;%OROMATCHER_JAR%;%LOG4J_JAR%;%SERVLETAPI_JAR%;%JDBC2X_JAR%;%XALAN_JAR%;%JAVACP%

java -classpath %USE_CLASS_PATH%;%ANT_JAR% org.apache.tools.ant.Main -buildfile %BUILD_FILE% %1 %2 %3 %4 %5
