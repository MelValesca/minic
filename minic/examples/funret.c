#include "minic.h"

int foo() {
	return 42;
	return 0;
}

int main() {
	printint(foo());
	println();
}
//stdout:42
