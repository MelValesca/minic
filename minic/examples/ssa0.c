#include "minic.c"

int main() {
	int a = 10;
	printint(a);
	println();
	a = a + 11;
	printint(a);
	println();
	a = a * 2;
	printint(a);
	println();
}
//stdout:10
//stdout:21
//stdout:42
