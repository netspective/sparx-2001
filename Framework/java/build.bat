@echo off
echo Building XAF...
echo.

set SAVECP=%CLASSPATH%
echo classpath at start is '%CLASSPATH%'

set JAVA_HOME=C:\utils\java\jdk1.3.1
set ANT_HOME=C:\utils\java\jakarta-ant-1.3
set XERCES_JAR=C:\utils\java\xerces-1_4_1\xerces.jar
set XALAN_JAR=C:\utils\java\xalan-j_2_1_0\bin\xalan.jar
set OROMATCHER_JAR=C:\utils\java\jakarta-oro-2.0.3\jakarta-oro-2.0.3.jar
set LOG4J_JAR=C:\utils\java\jakarta-log4j-1.1.2\dist\lib\log4j.jar
set SERVLETAPI_JAR=C:\utils\app-servers\resin-2.0.2\lib\jsdk23.jar
set JDBC2X_JAR=C:\utils\app-servers\resin-2.0.2\lib\jdbc2_0-stdext.jar

REM === Automatically add system classes to CLASSPATH ===
if exist "%JAVA_HOME%\lib\tools.jar" set CLASSPATH=%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%\lib\classes.zip" set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\classes.zip

set CLASSPATH=%CLASSPATH%;%ANT_HOME%\lib\ant.jar;%XERCES_JAR%;%XALAN_JAR%;%OROMATCHER_JAR%;%LOG4J_JAR%;%SERVLETAPI_JAR%;%JDBC2X_JAR%
echo classpath is now '%CLASSPATH%'

echo Starting Ant...
%ANT_HOME%\bin\ant -buildfile build.xml %1 %2 %3 %4 %5 %6 %7 %8 %9

goto end

:end
set CLASSPATH=%SAVECP%
echo classpath reset to '%CLASSPATH%'
