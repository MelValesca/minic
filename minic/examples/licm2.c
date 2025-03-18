#include "minic.h"
int foo(int a) {
	int s = 0;
	int i = 0;
	while(i<a*2) {
		int j = i;
		while (j<i*2) {
			s = s + 1;
			j = j + 1;
		}
		i = i + 1;
	}
	return s;
}
int main() {
	printint(foo(5)-3);
	println();
}
//stdout:42
