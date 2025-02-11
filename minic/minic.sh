#!/bin/sh
set -ex
./build.sh src/minic/MiniC.java
java -cp build minic.MiniC "$1"
