#!/bin/sh
set -e
mkdir -p build
if find src/ build/ -type f -printf '%T@ %p\n' | sort | tail -n 1 | grep java; then
#java=$(ls -tr src/*.java src/*/*.java | tail -n 1)
#class=$(ls -tr build/*.class | tail -n 1)
#tj=$(stat -c%Y "$java")
#tc=$(stat -c%Y "$class")
#if [ "$tj" -gt "$tc" ]; then
	javac -d build --source-path src "$@"
fi
