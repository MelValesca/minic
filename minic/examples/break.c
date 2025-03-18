#include "minic.h"
int foo(int a) {
	if(a<1) {
		return 1;
		a = 2;
	}
	while (a<2) {
		return 2;
		a = 3;
	}
	if(a<3) {
		return 3;
		a = 4;
	} else {
		return 4;
		a = 5;
	}
	return 5;
	a = 6;
}
int main() {
	printint(foo(0));
	printint(foo(1));
	printint(foo(2));
	printint(foo(3));
	println();
}
//stdout:1234
