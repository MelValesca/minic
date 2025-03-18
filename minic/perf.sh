#!/bin/bash

set -e

engine=$1
shift

opts=()
while [ "${1:0:1}" = "-" ]; do
	opts+=("$1")
	shift
done

src=$1
shift

mkdir -p out
LANG=C taskset -c 1 perf stat -einstructions,cycles,task-clock,duration_time,user_time,migrations -o out/perf.json -j "$@"
grep '^{' out/perf.json > out/perf0.json
date=$(date -Ins)
time=$(date +%s.%N)
jq -s ".[] + {\"cmd\": \"$*\", \"date\": \"$date\", \"time\": \"$time\", \"engine\": \"$engine ${opts[*]}\", \"source\": \"$src\" }" out/perf0.json >> out/perf2.json
jq -s . out/perf2.json | grep -v '"nan"\|"<not counted>"' > out/perf3.json
jq  '. | select(.event=="duration_time") | ."counter-value" | tonumber / 1000000000.0' out/perf0.json >&2
