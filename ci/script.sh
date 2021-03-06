#!/bin/bash

# Exit on any failure
set -e

sbt assembly

VERSION=`sbt version | tail -n 1 | awk '{print $2}'`

if [ $TRAVIS_OS_NAME = windows ]; then
  cp "target/scala-2.13/logpack-assembly-$VERSION.jar" logpack.jar
  ci/windows.bat
elif [ $TRAVIS_OS_NAME = osx ]; then
  native-image --verbose --no-fallback -H:+ReportExceptionStackTraces --allow-incomplete-classpath -H:+TraceClassInitialization '--initialize-at-build-time=scala.Symbol$' -jar "target/scala-2.13/logpack-assembly-$VERSION.jar" logpack
else
  native-image --verbose --static --no-fallback -H:+ReportExceptionStackTraces --allow-incomplete-classpath -H:+TraceClassInitialization '--initialize-at-build-time=scala.Symbol$' -jar "target/scala-2.13/logpack-assembly-$VERSION.jar" logpack
fi

mkdir release

if [ $TRAVIS_OS_NAME = windows ]; then
  mv logpack.exe release
else
  mv logpack "release/logpack-$TRAVIS_OS_NAME"
fi
