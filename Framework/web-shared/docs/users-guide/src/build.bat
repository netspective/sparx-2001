@echo off

set SAVECP=%CLASSPATH%
echo classpath at start is '%CLASSPATH%'

set XML_SOURCE_FILE=documentation.xml

set DOCBOOK_XSL_HOME=E:\Utils\docbook-xsl-1.45
set DOCBOOK_XSL_MAIN=%DOCBOOK_XSL_HOME%\html\chunk.xsl

set JAVA_HOME=E:\utils\jdk1.3.1
set XERCES_JAR=E:\utils\xerces-1_4_3\xerces.jar
set XALAN_JAR=E:\utils\xalan-j_2_2_D11\bin\xalan.jar
set FOP_HOME=E:\Utils\Fop-0.20.1

set CLASSPATH=%XERCES_JAR%;%XALAN_JAR%;%FOP_HOME%\build\fop.jar;%FOP_HOME%\lib\batik.jar;%FOP_HOME%\lib\jimi-1.0.jar;%JFOR_HOME%\jfor-0.5.jar
echo classpath is now '%CLASSPATH%'

rem echo Building Documentation (PDF)...
rem java org.apache.xalan.xslt.Process -IN %XML_SOURCE_FILE% -XSL %DOCBOOK_XSL_HOME%\fo\docbook.xsl -OUT ..\sparx.fo
rem java org.apache.fop.apps.Fop -fo ..\sparx.fo -pdf ..\sparx.pdf
rem %XEP_HOME%\run ..\sparx.fo

rem echo Building Documentation (RTF)...
rem java org.apache.xalan.xslt.Process -IN %XML_SOURCE_FILE% -XSL %DOCBOOK_XSL_HOME%\fo\docbook.xsl -OUT ..\sparx.fo
rem java ch.codeconsult.jfor.main.CmdLineConverter ..\sparx.fo ..\sparx.rtf

rem echo Building Documentation (single HTML file)...
rem java org.apache.xalan.xslt.Process -IN %XML_SOURCE_FILE% -XSL %DOCBOOK_XSL_HOME%\html\docbook.xsl -OUT ..\sparx.html

echo Building Documentation (chunked HTML files)...
java org.apache.xalan.xslt.Process -IN %XML_SOURCE_FILE% -XSL %DOCBOOK_XSL_HOME%\html\chunk.xsl
move *.html ..

goto end

:end
set CLASSPATH=%SAVECP%
echo classpath reset to '%CLASSPATH%'
