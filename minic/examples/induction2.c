#include "minic.h"
int foo(int a) {
	int s = 0;
	int i = 0;
	while(i<a) {
		i = i + 1;
		int j = i + 3;
		int k = j - 2;
		int l = k - 4;
		int m = 5 - l;
		int n = 6 - m;
		int o = n * 7;
		s = s + o;
		printint(j);
		printint(k);
		printint(l);
		printint(m);
		printint(n);
		printint(o);
	}
	printint(s);
}

int main() {
	foo(1);
	println();
}
//stdout:42-27-1-7-7
