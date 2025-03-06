#!/bin/bash

set +e

cmd=$1
shift

opts=()
while [ "${1:0:1}" = "-" ]; do
	opts+=("$1")
	shift
done

if [ "$#" = 0 ]; then
	set examples/*.c
fi

#echo "cmd: $cmd"
#echo "opts: ${opts[*]}"
#echo "args: ${*}"

mkdir -p out

for src do
	if grep -q '^//skip' "$src"; then
		echo "$src: skipped"
		continue
	fi
	b=$(basename "$src" .c)
	echo "$src"
	sed -n 's|^//stdout:\(.*\)|\1|p' "$src" > "out/$b.expected"
	stderr=$(grep -P -o '//stderr:\K.*' "$src")
	"$cmd" "${opts[@]}" "$src" > "out/$b.got" 2> "out/$b.log"
	ret=$?
	if [ -n "$stderr" ]; then
		if ! grep -q -F "$stderr" "out/$b.log"; then
			echo "Expected error: $stderr"
		fi
	elif [ "$ret" != "0" ]; then
		echo "Got error"
	elif ! diff -w -u "out/$b.expected" "out/$b.got"; then
		sed '/^\/\/stdout:/d' "$src" > "out/$b.c"
		sed 's|\(.*\)|//stdout:\1|' "out/$b.got" >> "out/$b.c"
	fi
done
