call "C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\VC\Auxiliary\Build\vcvars64.bat"

%HOME%/.sdkman/candidates/java/current/bin/native-image.cmd --verbose --static --no-fallback -H:+ReportExceptionStackTraces --allow-incomplete-classpath -H:+TraceClassInitialization "--initialize-at-build-time=scala.Symbol$" -jar "logpack.jar" logpack
