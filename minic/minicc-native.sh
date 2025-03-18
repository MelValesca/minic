#!/bin/sh
set -ex
./build.sh src/minic/MiniCC.java
java -cp build minic.MiniCC "$@"
gcc minicc.out.s minic_rt.c -static -o minicc.out.bin
./perf.sh "minicc native" "$@" ./minicc.out.bin
