#include "minic.h"
int foo(int a, int b) {
	if (a<b) {
		return a;
	} else {
		return b;
	}
}
int main() {
	printint(foo(4,8));
	printint(foo(8,2));
	println();
}
//stdout:42
