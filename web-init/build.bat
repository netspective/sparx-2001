@echo off

set SPARX_HOME=c:/java-library/Sparx
set APP_ROOT=c:/web-application/web-init
set APP_CLASSES=%APP_ROOT%/Site/WEB-INF/classes

set BUILD_FILE=%SPARX_HOME%/tools/app-build.xml

rem set JAVA_HOME=C:/utils/java/jdk1.3.1
set ANT_HOME=C:/java-library/jakarta-ant-1.4.1
set XERCES_JAR=C:/java-library/xerces-1_4_4/xerces.jar
set XALAN_JAR=C:/java-library/xalan-j_2_1_0/bin/xalan.jar
set OROMATCHER_JAR=C:/java-library/jakarta-oro-2.0.4/jakarta-oro-2.0.4.jar
set LOG4J_JAR=C:/java-library/jakarta-log4j-1.1.3/dist/lib/log4j.jar
set SERVLETAPI_JAR=C:/app-server/tomcat-4.0.3/common/lib/servlet.jar
set JDBC2X_JAR=C:/app-server/tomcat-4.0.3/common/lib/jdbc2_0-stdext.jar
set SPARX_JAR=%SPARX_HOME%/lib/sparx.jar

if exist "%JAVA_HOME%/lib/tools.jar" set JAVACP=%JAVA_HOME%/lib/tools.jar
if exist "%JAVA_HOME%/lib/classes.zip" set JAVACP=%CLASSPATH%;%JAVA_HOME%/lib/classes.zip

set USE_CLASS_PATH=%APP_CLASSES%;%XERCES_JAR%;%SPARX_JAR%;%OROMATCHER_JAR%;%LOG4J_JAR%;%SERVLETAPI_JAR%;%JDBC2X_JAR%;%XALAN_JAR%;%JAVACP%

java -Dant.home=%ANT_HOME% -classpath %USE_CLASS_PATH%;%ANT_HOME%/lib/ant.jar org.apache.tools.ant.Main -Dbasedir=%APP_ROOT% -buildfile %BUILD_FILE% %1 %2 %3 %4 %5
