#!/bin/bash

readarray -t list <<END
./gcc.sh -O0
./gcc.sh -O1
./gcc.sh -O2
./gcc-native.sh -O0
./gcc-native.sh -O1
./tcc.sh
#./gcc-native.sh -O2
./clang.sh -O0
./clang.sh -O1
#./clang.sh -O2
#./minic.sh
./minicc.sh
./minicc2.sh
./minicc2.sh -O1
./minicc2.sh -O1 -kempe
#./minicc-rars.sh
#./minicc2-rars.sh
#./gcc-rars.sh -O0
#./gcc-rars.sh -O1
#./gcc-rars.sh -O2
END

readarray -t prog <<END
benches/prime.c
benches/phi.c
benches/fibrec.c
benches/mandelbrot.c
END

#readarray -t prog <<END
#examples/prime.c
#examples/phi.c
#examples/fibrec.c
#examples/mandelbrot.c
#END

rm out/perf* || true
for i in `seq 1`; do
	for engine in "${list[@]}"; do
		if [[ $engine == \#* ]]; then continue; fi
		echo "$engine :"
		for j in `seq 1`; do
			./test.sh $engine "${prog[@]}"
		done
	done
done
