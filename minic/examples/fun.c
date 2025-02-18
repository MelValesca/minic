#include "minic.c"

int foo(int a) {
	printint(a);
	println();
	return a + 1;
}

int bar(int b) {
	return foo(b+1) + 1;
}

int main() {
	int r = bar(1);
	printint(r);
	println();
}
//stdout:2
//stdout:4
