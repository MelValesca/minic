#include "minic.c"

int one() { return 1; }

int main() {
	int i = 0;
	int a = one();
	int c = one() + 9;
	int d = 0;
	int e = 0;
	while (i < a * c) {
		int b = a * c;
		d = d + b;
		e = c;
		i = i + 1;
	}
	printint(d);
	printint(e);
	println();
}
//stdout:10010
