#!/bin/sh

BUILD_FILE=build.xml
SPARX_REDIST_HOME=`pwd`/../lib/redist

ANT_JAR=$SPARX_REDIST_HOME/ant.jar:$SPARX_REDIST_HOME/ant-optional.jar
XMLAPIS_JAR=$SPARX_REDIST_HOME/xml-apis.jar
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

USE_CLASS_PATH=$XMLAPIS_JAR:$XERCES_JAR:$SPARX_JAR:$OROMATCHER_JAR:$LOG4J_JAR:$SERVLETAPI_JAR:$JDBC2X_JAR:$XALAN_JAR:$JAVACP

java -classpath $USE_CLASS_PATH:$ANT_JAR org.apache.tools.ant.Main -buildfile $BUILD_FILE $1 $2 $3 $4 $5
