#include "minic.h"
int foo(int a) {
	int s = 0;
	int i = 0;
	while(i<a) {
		int k = i + 2;
		printint(k);
		i = i + 1;
		int l = k + 3;
		printint(l);
		s = s + l;
	}
	return s;
}

int main() {
	foo(1);
	println();
}
//stdout:25
