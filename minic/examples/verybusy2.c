#include "minic.h"

int foo(int n) {
	int i = 0;
	int m = n + 1;
	int s = 0;
	while (i < m*m) {
		s = s + i;
		i = i + 1;
	}
	printint(m*m);
	printint(s);
	println();
	return s;
}

int main() {
	foo(1);
	foo(5);
}
//stdout:46
//stdout:36630
