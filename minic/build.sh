#!/bin/sh
set -e

if [ ! -e sablecc-4-beta.2 ]; then
	wget http://downloads.sourceforge.net/sablecc/sablecc-4-beta.2.zip
	unzip sablecc-4-beta.2.zip
fi

if [ ! -e rars-1.7.jar ]; then
	wget https://github.com/rarsm/rars/releases/download/v1.7/rars-1.7.jar
fi


if find src/ ./minic.sablecc -type f -printf '%T@ %p\n' | sort | tail -n 1 | grep -q minic.sablecc; then
	java -jar sablecc-4-beta.2/lib/sablecc.jar minic.sablecc -p minic -d src
fi

mkdir -p build
if find src/ build/ -type f -printf '%T@ %p\n' | sort | tail -n 1 | grep -q java; then
	if [ "$#" = 0 ]; then
		set src/minic/*.java
	fi
	javac -d build --source-path src "$@"
fi
