#!/bin/sh
set -ex
clang "$@" minic_rt.c -o a.out
./perf.sh "clang native" "$@" ./a.out
