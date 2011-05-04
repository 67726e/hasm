@echo off
::Delete existing classes to allow for a clean build
if exist *.class del *.class
:COMPILE
@echo on
::Compile the source into new, clean classes
javac hasm.java
@echo off
::If there were errors in the compile, skip test run
if not ERRORLEVEL==0 goto CLEANUP

:TEST
@echo on
::Test the new build
java hasm nesasm_test.asm

:CLEANUP
@echo off
::Delete existing classes in anticipation of a new build
if exist *.class del *.class