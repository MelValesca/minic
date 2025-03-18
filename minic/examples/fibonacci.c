#include "minic.h"

int main() {
	int a = 0;
	int b = 1;
	int n = 0;
	while (n<15) {
		int t = a;
		a = b;
		b = b + t;
		n = n + 1;
	}
	printint(a);
	println();
}
//stdout:610
