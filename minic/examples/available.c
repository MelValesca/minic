#include "minic.c"
int main() {
	int a = 10;
	int b = a * 4 + 1;
	int c = a * 4 + 2;
	printint(b);
	printint(c);
	println();
}
//stdout:4142
