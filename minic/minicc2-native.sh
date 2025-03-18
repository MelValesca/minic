#!/bin/sh
set -ex
./build.sh src/minic/MiniCC2.java
java -cp build minic.MiniCC2 "$@"
gcc minicc2.out.s minic_rt.c -static -o minicc2.out
./perf.sh "minicc2 native" "$@" ./minicc2.out
