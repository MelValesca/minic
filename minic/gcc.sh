#!/bin/sh
set -ex
riscv64-linux-gnu-gcc "$@" minic_rt.c -static -o a.out
./perf.sh "gcc qemu" "$@" qemu-riscv64 ./a.out
