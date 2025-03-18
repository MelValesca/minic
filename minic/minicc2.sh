#!/bin/sh
set -ex
./build.sh src/minic/MiniCC2.java
java -cp build minic.MiniCC2 "$@"
#rars-flatlaf.jar minicc-rv64.s minic_rv.s
riscv64-linux-gnu-gcc minicc2.out.s minic_rt.c -static -o minicc2.out
./perf.sh "minicc2 qemu" "$@" qemu-riscv64 ./minicc2.out
