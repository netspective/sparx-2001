#!/bin/sh

BUILD_FILE=build.xml

JAVA_LIB=/usr/local/oracle/java-library/common
APP_SERVER_LIB=/usr/local/oracle/resin/lib

ANT_HOME=$JAVA_LIB/jakarta-ant-1.4.1
XERCES_JAR=$JAVA_LIB/xerces-1_4_4/xerces.jar
XALAN_JAR=$JAVA_LIB/xalan-j_2_1_0/bin/xalan.jar
OROMATCHER_JAR=$JAVA_LIB/jakarta-oro-2.0.4/jakarta-oro-2.0.4.jar
LOG4J_JAR=$JAVA_LIB/jakarta-log4j-1.1.3/dist/lib/log4j.jar
SERVLETAPI_JAR=$APP_SERVER_LIB/jsdk23.jar
JDBC2X_JAR=$APP_SERVER_LIB/jdbc2_0-stdext.jar
JAVACP=$JAVA_HOME/lib/tools.jar

USE_CLASS_PATH=$APP_CLASSES:$XERCES_JAR:$FRAMEWORK_JAR:$OROMATCHER_JAR:$LOG4J_JAR:$SERVLETAPI_JAR:$JDBC2X_JAR:$XALAN_JAR:$JAVACP

java -Dant.home=$ANT_HOME -classpath $USE_CLASS_PATH:$ANT_HOME/lib/ant.jar org.apache.tools.ant.Main -Dbasedir=$APP_ROOT -buildfile $BUILD_FILE $1 $2 $3 $4 $5
