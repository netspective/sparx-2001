#!/bin/sh

# $Id: build.sh,v 1.7 2002-09-08 23:31:22 shahid.shah Exp $

# **************************************************************************
# ** This script should be be run from the SPARX_HOME\java directory.     **
# ** It is basically a "launcher" for Ant and the actual work is done in  **
# ** the build.xml file.                                                  **
# **************************************************************************

if [ -z $JAVA_HOME ]; then
	echo JAVA_HOME environment variable is not set.
	exit
fi

if [ -z $SPARX_HOME ]; then
	SPARX_HOME=../
	export SPARX_HOME
fi

SPARX_REDIST_HOME=$SPARX_HOME/lib/redist
JAVACP=

if [ -f $JAVA_HOME/lib/tools.jar ]; then
	JAVACP=$JAVA_HOME/lib/tools.jar
fi

if [ -f $JAVA_HOME/lib/classes.zip ]; then
	JAVACP=$JAVA_HOME/lib/classes.zip
fi

java -cp $SPARX_REDIST_HOME/ant.jar:$SPARX_REDIST_HOME/ant-optional.jar:$SPARX_REDIST_HOME/xerces.jar:$JAVACP org.apache.tools.ant.Main $1 $2 $3 $4 $5
