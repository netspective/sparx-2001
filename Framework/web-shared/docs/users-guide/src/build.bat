@echo off

set SAVECP=%CLASSPATH%
echo classpath at start is '%CLASSPATH%'

set XML_SOURCE_FILE=documentation.xml
set DOCBOOK_XSL_HOME=c:\Utils\docbook-xsl-1.44
set DOCBOOK_XSL_MAIN=%DOCBOOK_XSL_HOME%\html\chunk.xsl
set JAVA_HOME=C:\utils\java\jdk1.3.1
set XERCES_JAR=C:\utils\java\xerces-1_4_1\xerces.jar
set XALAN_JAR=C:\utils\java\xalan-j_2_1_0\bin\xalan.jar
set FOP_HOME=C:\Utils\java\Fop-0.20.1

set CLASSPATH=%XERCES_JAR%;%XALAN_JAR%;%FOP_HOME%\build\fop.jar;%FOP_HOME%\lib\batik.jar;%FOP_HOME%\lib\jimi-1.0.jar
echo classpath is now '%CLASSPATH%'

rem echo Building Documentation (PDF)...
rem java org.apache.xalan.xslt.Process -IN %XML_SOURCE_FILE% -XSL %DOCBOOK_XSL_HOME%\fo\docbook.xsl -OUT ..\sparx.fo
rem java org.apache.fop.apps.Fop -fo ..\sparx.fo -pdf ..\sparx.pdf

rem echo Building Documentation (single HTML file)...
rem java org.apache.xalan.xslt.Process -IN %XML_SOURCE_FILE% -XSL %DOCBOOK_XSL_HOME%\html\docbook.xsl -OUT ..\sparx.html

echo Building Documentation (chunked HTML files)...
java org.apache.xalan.xslt.Process -IN %XML_SOURCE_FILE% -XSL %DOCBOOK_XSL_HOME%\html\chunk.xsl
move *.html ..

goto end

:end
set CLASSPATH=%SAVECP%
echo classpath reset to '%CLASSPATH%'
