#!/bin/sh

# $Id: build.sh,v 1.4 2002-08-25 21:27:58 shahid.shah Exp $

# **************************************************************************
# ** This script should be be run from the SPARX_HOME/documentation/src   **
# ** directory. It is basically a "launcher" for Ant and the actual work  **
# ** is done in the build.xml file.                                       **
# **************************************************************************

BUILD_FILE=build.xml
SPARX_HOME=`pwd`/../../
SPARX_REDIST_HOME=$SPARX_HOME/lib/redist

SPARX_JAR=$SPARX_HOME/lib/sparx.jar
ANT_JAR=$SPARX_REDIST_HOME/ant.jar:$SPARX_REDIST_HOME/ant-optional.jar
XERCES_JAR=$SPARX_REDIST_HOME/xerces.jar
XALAN_JAR=$SPARX_REDIST_HOME/xalan.jar
OROMATCHER_JAR=$SPARX_REDIST_HOME/oro.jar
LOG4J_JAR=$SPARX_REDIST_HOME/log4j.jar
SERVLETAPI_JAR=$SPARX_REDIST_HOME/servlet.jar
JDBC2X_JAR=$SPARX_REDIST_HOME/jdbc.jar

if [ -f $JAVA_HOME/lib/tools.jar ]; then
	JAVACP=$JAVA_HOME/lib/tools.jar
fi

if [ -f $JAVA_HOME/lib/classes.zip ]; then
	JAVACP=$JAVA_HOME/lib/classes.zip
fi

USE_CLASS_PATH=$XERCES_JAR:$SPARX_JAR:$OROMATCHER_JAR:$LOG4J_JAR:$SERVLETAPI_JAR:$JDBC2X_JAR:$XALAN_JAR:$JAVACP

java -classpath $USE_CLASS_PATH:$ANT_JAR org.apache.tools.ant.Main -buildfile $BUILD_FILE $1 $2 $3 $4 $5

echo "Creating Symlinks to Samples"
ln -s $HOME/projects/hello ../samples/hello
ln -s $HOME/projects/library ../samples/library
ln -s $HOME/projects/cura ../samples/cura