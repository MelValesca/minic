#!/bin/sh
set -e

if [ ! -e sablecc-4-beta.2 ]; then
	wget http://downloads.sourceforge.net/sablecc/sablecc-4-beta.2.zip
	unzip sablecc-4-beta.2.zip
fi

if [ ! -e rars-1.7.jar ]; then
	wget https://github.com/rarsm/rars/releases/download/v1.7/rars-1.7.jar
fi

if [ ! -e src/minic/language_minic -o minic.sablecc -nt src/minic/language_minic ]; then
	rm -rf src/minic/language_minic
	java -jar sablecc-4-beta.2/lib/sablecc.jar minic.sablecc -d src >/dev/null
fi

mkdir -p build
if find src/ build/ -type f -printf '%T@ %p\n' | sort | tail -n 1 | grep -q java; then
	if [ "$#" = 0 ]; then
		set src/minic/*.java
	fi
	javac --release 17 -d build --source-path src "$@"
fi
