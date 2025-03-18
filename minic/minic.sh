#!/bin/sh
set -ex
./build.sh src/minic/MiniC.java
./perf.sh "minic java" "$@" java -cp build minic.MiniC "$@"
