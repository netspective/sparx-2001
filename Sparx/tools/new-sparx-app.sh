#!/bin/sh
# $Id: new-sparx-app.sh,v 1.1 2003-03-01 19:24:18 shahbaz.javeed Exp $

# **************************************************************************
# ** This must be be run from the SPARX_HOME/tools directory.             **
# ** Usage: new-sparx-app <directory-of-new-app> <remove-old>             **
# ** Examples:                                                            **
# **   new-sparx-app ~/app/new-app                                        **
# **   new-sparx-app ~/app/new-app Y                 [ remove if exists ] **
# **************************************************************************

if [ -z "$1" ]; then
	# No commandline arguments passed to the script...
	# Show Usage
	echo "USAGE: ./new-sparx-app.sh [directory-of-new-app] [remove-old]"
	echo "Example 1: ./new-sparx-app.sh ~/web-application/new-app"
	echo "Example 2: ./new-sparx-app.sh ~/web-application/new-app Y"
	exit
fi

APP_ROOT=$1;
REMOVE_OLD=$2;

if [ -d "$APP_ROOT" ]; then
	# If this directory exists...
	
	if [ -n "$REMOVE_OLD" ]; then
		if [ "$REMOVE_OLD" == "y" ] || [ "$REMOVE_OLD" == "Y" ]; then
			# ... and the user wants it removed ...
			# remove it...
			rm -rf $APP_ROOT
			mkdir $APP_ROOT
		fi
	fi
fi

APP_WEBINF_ROOT="$APP_ROOT/WEB-INF"
APP_WEBINF_LIB_ROOT="$APP_WEBINF_ROOT/lib"

mkdir -p $APP_WEBINF_LIB_ROOT
cp app-build.bat $APP_WEBINF_ROOT/build.bat
cp app-build.sh $APP_WEBINF_ROOT/build.sh
cp app-build.xml $APP_WEBINF_ROOT/build.xml
cp ../lib/redist/ant.jar $APP_WEBINF_LIB_ROOT
cp ../lib/redist/xml-apis.jar $APP_WEBINF_LIB_ROOT
cp ../lib/redist/xerces.jar $APP_WEBINF_LIB_ROOT
cp ../lib/sparx.jar $APP_WEBINF_LIB_ROOT

chmod +x "$APP_WEBINF_ROOT/build.sh"

# **************************************************************************
# ** Now that we've bootstrapped the required libraries, run the build    **
# ** target in the Jakarta Ant build.xml file.                            **
# **************************************************************************

cd $APP_WEBINF_ROOT
./build.sh start-sparx-app
