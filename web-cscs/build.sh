#!/bin/sh

SPARX_HOME=$HOME/projects/newproj/Sparx
export SPARX_HOME

APP_ROOT=`pwd`
APP_CLASSES=$APP_ROOT/Site/WEB-INF/classes
BUILD_FILE=$SPARX_HOME/tools/app-build.xml

JAVA_LIB=/root/projects/newproj/java
APP_SERVER_LIB=/root/projects/newproj/resin-2.0.5/lib

ANT_HOME=$JAVA_LIB/jakarta-ant-1.4.1
XERCES_JAR=$JAVA_LIB/xerces-1_4_3/xerces.jar
#XALAN_JAR=$JAVA_LIB/xalan-j_2_1_0/bin/xalan.jar
XALAN_JAR=$JAVA_LIB/xalan-j_2_2_D13/bin/xml-apis.jar:$JAVA_LIB/xalan-j_2_2_D13/bin/xalan.jar
OROMATCHER_JAR=$JAVA_LIB/jakarta-oro-2.0.4/jakarta-oro-2.0.4.jar
LOG4J_JAR=$JAVA_LIB/jakarta-log4j-1.1.3/dist/lib/log4j.jar
SERVLETAPI_JAR=$APP_SERVER_LIB/jsdk23.jar
JDBC2X_JAR=$APP_SERVER_LIB/jdbc2_0-stdext.jar
FRAMEWORK_JAR=$SPARX_HOME/lib/sparx.jar
JAVACP=$JAVA_HOME/lib/tools.jar

USE_CLASS_PATH=$APP_CLASSES:$XERCES_JAR:$FRAMEWORK_JAR:$OROMATCHER_JAR:$LOG4J_JAR:$SERVLETAPI_JAR:$JDBC2X_JAR:$XALAN_JAR:$JAVACP

java -Dant.home=$ANT_HOME -classpath $USE_CLASS_PATH:$ANT_HOME/lib/ant.jar org.apache.tools.ant.Main -Dbasedir=$APP_ROOT -buildfile $BUILD_FILE $1 $2 $3 $4 $5
