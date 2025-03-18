#include "minic.h"

int fib(int n) {
	if (n<2) { return n; }
	return fib(n-1) + fib(n-2);
}

int main() {
	printint(fib(15)+fib(15)-fib(15));
	println();
}
//stdout:610
