SAVECP=$CLASSPATH

JAVA_UTILS=/opt/java

if ["$ANT_HOME" = ""]; then
	export ANT_HOME=$JAVA_UTILS/jakarta-ant-1.3
fi

if ["$XERCES_JAR" = ""]; then
	export XERCES_JAR=$JAVA_UTILS/xerces-1_4_1/xerces.jar
fi

if ["$XALAN_JAR" = ""]; then
	export XALAN_JAR=$JAVA_UTILS/xalan-j_2_1_0/bin/xalan.jar
fi

if [ "$OROMATCHER_JAR" = "" ]; then
	export OROMATCHER_JAR=$JAVA_UTILS/jakarta-oro-2.0.2/jakarta-oro-2.0.2.jar
fi

if [ "$LOG4J_JAR" = "" ]; then
	export LOG4J_JAR=$JAVA_UTILS/jakarta-log4j-1.1.3/dist/lib/log4j.jar
fi

if [ "$SERVLETAPI_JAR" = "" ]; then
	export SERVLETAPI_JAR=/usr/local/resin/lib/jsdk22.jar
fi

if [ "$JDBC2X_JAR" = "" ]; then
	export JDBC2X_JAR=/usr/local/resin/lib/jdbc2_0-stdext.jar
fi

export CLASSPATH=$CLASSPATH:$ANT_HOME/lib/ant.jar:$XERCES_JAR:$XALAN_JAR:$OROMATCHER_JAR:$LOG4J_JAR:$SERVLETAPI_JAR:$JDBC2X_JAR

echo $CLASSPATH

$ANT_HOME/bin/ant -buildfile build.xml $1

export CLASSPATH=$SAVECP
export SAVECP=
