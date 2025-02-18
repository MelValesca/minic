#include "minic.c"

int foo() {
	int a = 42;
}

int main() {
	printint(foo());
	println();
}
//stdout:0
