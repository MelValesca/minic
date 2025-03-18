#!/bin/sh
set -ex
gcc "$@" minic_rt.c -o a.out
./perf.sh "gcc native" "$@" ./a.out
