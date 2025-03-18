#!/bin/sh
set -ex
./build.sh src/minic/MiniCC.java
java -cp build minic.MiniCC "$@"
riscv64-linux-gnu-gcc minicc.out.s minic_rt.c -static -o minicc.out.bin
./perf.sh "minicc qemu" "$@" qemu-riscv64 ./minicc.out.bin
