#include "minic.c"
int main() {
	int a = 1;
	if(0<1) {
		int a = 2;
		a = 3;
		printint(a);
		int a = 4;
		printint(a);
	}
	printint(a);
	println();
}
