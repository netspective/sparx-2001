--------------
BUILDING SPARX
--------------

1. Get the Java JDK -- Release 1.2 or above is required
2. Setup the environment variable JAVA_HOME 
3. Add JAVA_HOME/bin to your path
4. Run build.bat or build.sh
5. Run SPARX_HOME/documentation/src/build.bat or build.sh to create docs

---------------
COMMON PROBLEMS
---------------

PROBLEM: "Cannot use classic compiler, as it is not available. A common solution is to set the environment variable JAVA_HOME to your jdk directory".
SOLUTION: Make sure the JAVA_HOME variable points to a valid JDK

PROBLEM: "Javadoc failed: java.io.IOException: CreateProcess: javadoc.exe -d"
SOLUTION: Add JAVA_HOME/bin to your PATH