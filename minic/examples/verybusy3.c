#include "minic.h"

int foo(int a) {
	int b = a + 1;
	int c = 3 * b;
	int d = 0;
	if(0<c) {
		b = 0;
		d = c + 1;
	}
	d = a + 1;
	printint(d);
}
int main() {
	foo(1);
	foo(0-2);
}
//stdout:2-1
