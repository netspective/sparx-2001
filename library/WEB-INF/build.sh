#!/bin/sh

# $Id: build.sh,v 1.1 2002-08-09 21:03:42 shahid.shah Exp $

# **************************************************************************
# ** This script should be be run from the APP_ROOT/WEB-INF directory.    **
# ** It is basically a "launcher" for Ant and the actual work is done in  **
# ** the build.xml file.                                                  **
# **************************************************************************

if [ -z $JAVA_HOME ]; then
	JAVA_HOME=/usr/java/home
fi

BASEDIR=`pwd`

#
# The SPARX_HOME, if not set, is assumed to be at the same level as apps.
# For example: if application hello is at /some/where/hello and this script
#              is executing from /some/where/hello/WEB-INF then the Sparx
#              home directory is assumed be /some/where/Sparx.
#

if [ -z $SPARX_HOME ]; then
	SPARX_HOME=$BASEDIR/../../Sparx
	export SPARX_HOME
fi	

SPARX_REDIST_HOME=$SPARX_HOME/lib/redist
APP_CLASSES=$BASEDIR/classes
APP_LIB=$BASEDIR/lib
APP_BUILD_FILE=$BASEDIR/build.xml

# **************************************************************************
# ** Setup location of all the Sparx prerequisites                        **
# **   Apache Xerces 1.4 or above (http://xml.apache.org)                 **
# **   Apache Xalan 2.1 or above (http://xml.apache.org)                  **
# **   Jakarta ORO Matcher 2.0 or above (http://jakarta.apache.org)       **
# **   Jakarta Log4J 1.1 or above (http://jakarta.apache.org)             **
# **   Java Servlet API 2.2 or above (http://java.sun.com)                **
# **   Java JDBC 2.0 Standard Extensions (http://java.sun.com)            **
# **************************************************************************

SPARX_JAR=$APP_LIB/sparx.jar
ANT_JAR=$APP_LIB/ant.jar
XERCES_JAR=$APP_LIB/xerces.jar
XALAN_JAR=$APP_LIB/xalan.jar
OROMATCHER_JAR=$APP_LIB/oro.jar
LOG4J_JAR=$APP_LIB/log4j.jar
BSF_JAR=$APP_LIB/bsf.jar
BSF_JS_JAR=$APP_LIB/js.jar

SERVLETAPI_JAR=$SPARX_REDIST_HOME/servlet.jar
JDBC2X_JAR=$SPARX_REDIST_HOME/jdbc.jar

if [ -f $JAVA_HOME/lib/tools.jar ]; then
	JAVACP=$JAVA_HOME/lib/tools.jar
fi

if [ -f $JAVA_HOME/lib/classes.zip ]; then
	JAVACP=$JAVA_HOME/lib/classes.zip
fi

USE_CLASS_PATH=$APP_CLASSES:$XERCES_JAR:$SPARX_JAR:$OROMATCHER_JAR:$LOG4J_JAR:$SERVLETAPI_JAR:$JDBC2X_JAR:$XALAN_JAR:$JAVACP:$ANT_JAR:$BSF_JAR:$BSF_JS_JAR

# **************************************************************************
# ** Now that all the variables are set, execute Ant                      **
# **************************************************************************

java -classpath $USE_CLASS_PATH org.apache.tools.ant.Main -Dbasedir=$BASEDIR -buildfile $APP_BUILD_FILE $1 $2 $3 $4 $5
