#include "minic.h"

int foo(int a) {
	int b = a;
	int i = 0;
	while(i<5) {
		b = a;
		i = i + 1;
	}
	printint(b);
	println();
	return i;
}


int main() {
	foo(42);
}

//stdout:42
