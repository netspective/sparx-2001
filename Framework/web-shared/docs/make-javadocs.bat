@echo off

set RESIN_HOME=c:\utils\resin-1.2.5
set RESIN_LIB=%RESIN_HOME%\lib
set CLASSPATH=%CLASSPATH%;%RESIN_LIB%\dom.jar;%RESIN_LIB%\jaxp.jar;%RESIN_LIB%\jdbc2_0-stdext.jar;%RESIN_LIB%\jdk12.jar;%RESIN_LIB%\jndi.jar;%RESIN_LIB%\jsdk22.jar;%RESIN_LIB%\jta-spec1_0_1.jar;%RESIN_LIB%\sax.jar;

rmdir javadoc /s /q
mkdir javadoc
cd javadoc
javadoc com.xaf.ace com.xaf.db com.xaf.db.generate com.xaf.form com.xaf.form.field com.xaf.form.taglib com.xaf.navigate com.xaf.navigate.taglib com.xaf.report com.xaf.report.calc com.xaf.report.column com.xaf.skin com.xaf.sql com.xaf.sql.query com.xaf.sql.query.comparison com.xaf.sql.taglib com.xaf.transform com.xaf.value com.xaf.xml
cd ..
