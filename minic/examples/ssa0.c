#include "minic.h"

int main() {
	int a = 10;
	printint(a);
	println();
	a = a + 11;
	if(0<a) {
		printint(a);
	}
	println();
	a = a * 2;
	printint(a);
	println();
}
//stdout:10
//stdout:21
//stdout:42
