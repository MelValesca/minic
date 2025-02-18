#!/bin/sh
set -ex
gcc "$1" -o a.out
./a.out
