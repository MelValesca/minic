#include "minic.h"
int foo(int n) {
	int s = 0;
	int i = 0;
	while (i < n) {
		int j = i;
		while (j < n+2) {
			s = (n*2) + (i*2) + (j*2) + s;
			j = j + 1;
		}
		i = i + 1;
	}

	printint(s-478);
	println();
}

int main() {
	foo(5);
}
//stdout:42
