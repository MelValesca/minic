#include "minic.h"

int foo(int a) {
	int i = 0;
	int c = a + 9;
	int d = 4*4;
	int e = 0;
	while (i < a * c + 2) {
		int b = a + c;
		d = d + b - 12;
		e = c - 8;
		i = i + 1;
	}
	printint(d);
	printint(e);
	println();
}

int main() {
	foo(1);
}
//stdout:42
