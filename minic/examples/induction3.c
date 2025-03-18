#include "minic.h"
int foo(int a) {
	int s = 2;
	int i = 40;
	while(0<i) {
		int j = 2 * i;
		int k = a - j;
		i = i - 4;
		int l = k * 2;
		s = j + k - l + s - 83;
	}
	printint(s);
}

int main() {
	foo(1);
	println();
}
//stdout:42
