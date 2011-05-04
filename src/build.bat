@echo off
if exist *.class del *.class
if exist "../bin/hasm.jar" del "../bin/hasm.jar"

:COMPILE
javac hasm.java
if not ERRORLEVEL==0 goto CLEANUP

:BUILD
jar -cvfm hasm.jar Manifest.mft *.class
if ERRORLEVEL==0 goto CLEANUP

:BUILDFAIL
echo Failed Build
echo Deleting Output....
del Build.jar

:CLEANUP
if exist *.class del *.class
move hasm.jar "../bin/"
:END