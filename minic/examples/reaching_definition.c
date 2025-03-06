#include "minic.c"

int foo(bool b) {
	int a = 0;
	if (b) {
		a = 42;
	} else {
		a = 124;
	}
	printint(a);
}

int main() {
	foo(true);
	foo(false);
	println();
}
//stdout:42124
