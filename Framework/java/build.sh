SAVECP=$CLASSPATH
SAVEANTHOME=$ANT_HOME
SAVEJAVAHOME=$JAVA_HOME
SAVERESINHOME=$RESIN_HOME

JAVA_UTILS=/opt/java

if ["$ANT_HOME" = ""]; then
	export ANT_HOME=$JAVA_UTILS/jakarta-ant-1.3
fi

if [ "$RESIN_HOME" = "" ]; then
     export RESIN_HOME=/opt/resin-1.2.7
fi

if [ "$OROMATCHER_JAR" = "" ]; then
	export OROMATCHER_JAR=$JAVA_UTILS/jakarta-oro-2.0.2/jakarta-oro-2.0.2.jar
fi

if [ "$LOG4J_JAR" = "" ]; then
	export LOG4J_JAR=$JAVA_UTILS/jakarta-log4j-1.1.3/dist/lib/log4j.jar
fi

export CLASSPATH=.:$CLASSPATH:$ANT_HOME/lib/ant.jar:$ANT_HOME/lib/xerces.jar

echo $CLASSPATH
echo $ANT_HOME
echo $RESIN_HOME
echo $OROMATCHER_JAR
echo $LOG4J_JAR

$ANT_HOME/bin/ant -buildfile build.xml $1

export CLASSPATH=$SAVECP
export SAVECP=
export ANT_HOME=$SAVEANTHOME
export JAVA_HOME=$SAVJAVAHOME
export RESIN_HOME=$SAVERESINHOME