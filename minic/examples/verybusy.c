#include "minic.h"

int foo(bool b) {
	int a = 1;
	int c = 2;
	if (b) { c = c + 3; }
	if (b) { a = a + c * 2; }
	else { c = a + c * 2; }
	printint(a);
	printint(c);
}

int main() {
	foo(true);
	foo(false);
	println();
}
//stdout:11515
