@echo off

set BUILD_FILE=build.xml

set JAVA_HOME=C:\utils\java\jdk1.3.1
set ANT_HOME=C:\utils\java\jakarta-ant-1.3
set XERCES_JAR=C:\utils\java\xerces-1_4_1\xerces.jar
set XALAN_JAR=C:\utils\java\xalan-j_2_1_0\bin\xalan.jar
set OROMATCHER_JAR=C:\utils\java\jakarta-oro-2.0.3\jakarta-oro-2.0.3.jar
set LOG4J_JAR=C:\utils\java\jakarta-log4j-1.1.2\dist\lib\log4j.jar
set SERVLETAPI_JAR=C:\utils\app-servers\resin-2.0.4\lib\jsdk23.jar
set JDBC2X_JAR=C:\utils\app-servers\resin-2.0.4\lib\jdbc2_0-stdext.jar

if exist "%JAVA_HOME%\lib\tools.jar" set JAVACP=%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%\lib\classes.zip" set JAVACP=%CLASSPATH%;%JAVA_HOME%\lib\classes.zip

set USE_CLASS_PATH=%XERCES_JAR%;%SPARX_JAR%;%OROMATCHER_JAR%;%LOG4J_JAR%;%SERVLETAPI_JAR%;%JDBC2X_JAR%;%XALAN_JAR%;%JAVACP%

java -Dant.home=%ANT_HOME% -classpath %USE_CLASS_PATH%;%ANT_HOME%\lib\ant.jar org.apache.tools.ant.Main -buildfile %BUILD_FILE% %1 %2 %3 %4 %5
