@echo off

REM ************************************************************
REM ** This script is run before doing a CVS Update to help 
REM ** Prevent build errors associated with local build files
REM ************************************************************

erase java\build.number
erase java\com\netspective\sparx\BuildConfiguration.java
erase web-shared\resources\sparx.build.properties

echo Build files cleaned up, you may now do a CVS update.