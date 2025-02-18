#!/bin/sh
set -ex
./build.sh src/minic/MiniCC.java
java -cp build minic.MiniCC "$1"
java -jar rars-1.7.jar sm rv64 minicc.out.s minic_rars.s || true
