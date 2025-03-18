#include "minic.h"
int foo(int a) {
	int s = 2;
	int i = 3;
	while(i<22) {
		int j = i + 4;
		int k = a * i;
		i = i + 2;
		s = j - k + s;
	}
	printint(s);
}

int main() {
	foo(1);
	println();
}
//stdout:42
