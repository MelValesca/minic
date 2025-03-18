#include "minic.h"
int foo(int a) {
	if(0 < a) {
		if (a < 10) {
			printint(a);
		} else {
			if (false) {
				printint(0);
			} else {
				printint(a-10);
			}
		}
	} else {
		printint(a-2);
	}
}

int main() {
	foo(4);
	foo(12);
	println();
}
//stdout:42
