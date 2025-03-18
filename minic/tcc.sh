#!/bin/sh
set -ex
tcc "$@" minic_rt.c -o a.out
./perf.sh "tcc native" "$@" ./a.out
