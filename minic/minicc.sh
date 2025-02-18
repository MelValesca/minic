#!/bin/sh
set -ex
./build.sh src/minic/MiniCC.java
java -cp build minic.MiniCC "$1"
riscv64-linux-gnu-gcc minicc.out.s examples/minic.c -static -o minicc.out.bin
qemu-riscv64 ./minicc.out.bin
