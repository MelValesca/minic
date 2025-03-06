#include "minic.c"

int main() {
	int a = 10;
	int b = 42;
	int c = 9000;
	int z = 0;
	int i = 0;
	while(i<10) {
		int t = a;
		a = b;
		b = c;
		c = t;
		i = i + 1;
		z = a;
	}
	printint(z);
	println();
}
//stdout:42
