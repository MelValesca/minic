#!/bin/sh
set -ex
./build.sh src/minic/MiniCC2.java
java -cp build minic.MiniCC2 "$@"
./perf.sh "minicc2 rars" "$@" java -jar rars-1.7.jar sm rv64 minicc2.out.s minic_rars.s || true
