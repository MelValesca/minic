#include "minic.h"

int foo(int a, int b) {
	return a - b;
}

int bar(int c, int d) {
	return foo(d, c);
}

int main() {
	printint(foo(50,8));
	printint(bar(50,8));
	println();
}

//stdout:42-42
