@echo off

REM $Id: new-sparx-app.bat,v 1.1 2002-08-22 01:34:14 shahid.shah Exp $

REM **************************************************************************
REM ** This must be be run from the SPARX_HOME/tools directory.             **
REM ** Usage: new-sparx-app <directory-of-new-app> <remove-old>             **
REM ** Examples:                                                            **
REM **   new-sparx-app C:\Projects\new-app                                  **
REM **   new-sparx-app C:\Projects\new-app Y           [ remove if exists ] **
REM **************************************************************************

if "%1%" == "" goto USAGE

set APP_ROOT=%1%
set REMOVE_OLD=%2%

if exist "%APP_ROOT%" goto ALREADYEXISTS

:START

set APP_WEBINF_ROOT=%APP_ROOT%\WEB-INF
set APP_WEBINF_LIB_ROOT=%APP_ROOT%\WEB-INF\lib

mkdir %APP_ROOT%
mkdir %APP_WEBINF_ROOT%
mkdir %APP_WEBINF_LIB_ROOT%
copy app-build.bat %APP_WEBINF_ROOT%\build.bat
copy app-build.sh %APP_WEBINF_ROOT%\build.sh
copy app-build.xml %APP_WEBINF_ROOT%\build.xml
copy ..\lib\redist\ant.jar %APP_WEBINF_LIB_ROOT%
copy ..\lib\redist\xml-apis.jar %APP_WEBINF_LIB_ROOT%
copy ..\lib\redist\xerces.jar %APP_WEBINF_LIB_ROOT%
copy ..\lib\sparx.jar %APP_WEBINF_LIB_ROOT%

REM **************************************************************************
REM ** Now that we've bootstrapped the required libraries, run the build    **
REM ** target in the Jakarta Ant build.xml file.                            **
REM **************************************************************************

cd %APP_WEBINF_ROOT%
build start-sparx-app

goto END

:REMOVE_OLD_APP_ROOT
echo Removing %APP_ROOT%...
rd %APP_ROOT% /s /q
goto START

:ALREADYEXISTS
if "%REMOVE_OLD%" == "Y" goto REMOVE_OLD_APP_ROOT

echo %APP_ROOT% already exists. Use "new-sparx-app %APP_ROOT% Y" to force delete and recreate it.
goto END

:USAGE
echo USAGE: new-sparx-app [directory-of-new-app] [remove-old]
goto END

:END