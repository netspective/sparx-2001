#!/bin/sh

# $Id: build.sh,v 1.6 2002-09-16 02:07:20 shahid.shah Exp $

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

# the following is for developer.netspective.com

echo "Creating Symlinks to Samples"

SAMPLES_ROOT=$HOME/projects
SAMPLES_DOC_ROOT=`pwd`/../samples

ln -svf $SAMPLES_ROOT/hello $SAMPLES_DOC_ROOT/hello
ln -svf $SAMPLES_ROOT/library $SAMPLES_DOC_ROOT/library
ln -svf $SAMPLES_ROOT/cura $SAMPLES_DOC_ROOT/cura

