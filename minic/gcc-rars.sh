#!/bin/sh
set -ex
riscv64-linux-gnu-gcc "$@" -static -S -fno-pic -fno-plt -o a.s
./perf.sh "gcc rars" "$@" java -jar rars-1.7.jar sm rv64 a.s minic_rars.s || true
