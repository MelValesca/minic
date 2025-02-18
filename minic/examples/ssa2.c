#include "minic.c"

int foo(bool x) {
	int a = 1;
	while(x) {
		a = a + 1;
		int b = 1;
		if(x) {
			a = a + 2;
			b = b + 2;
		} else {
			a = a + 3;
			b = b + 3;
		}
		a = a + 4;
		b = b + 4;
		printint(b);
		println();
	}
	printint(a);
	println();
}

int main() {
	foo(false);
}
//stdout:1
