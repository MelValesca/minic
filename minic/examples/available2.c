#include "minic.h"
int foo(int n) {
	int a = n * n;
	int b = a - 1;
	if (n<0) {
		a = a - 1;
	} else {
		b = 0;
	}
	printint(n*n);
	printint(a-1);
	return b;
}
int main() {
	foo(0-2);
	println();
}
//stdout:42
