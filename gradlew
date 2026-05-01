#!/bin/sh
#
# Gradle start up script for UN*X
#
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
APP_HOME="`pwd -P`"

MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

OS_NAME="`uname`"
case $OS_NAME in
  CYGWIN*|MINGW*|MSYS*)
    APP_HOME="`cygpath --path --mixed "$APP_HOME"`"
    ;;
esac

if [ -z "$JAVA_HOME" ] ; then
  JAVACMD="java"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

GRADLE_OPTS="$GRADLE_OPTS -Dorg.gradle.appname=$APP_BASE_NAME"

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain "$@"
