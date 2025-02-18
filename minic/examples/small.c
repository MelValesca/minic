#include "minic.c"
int main() {
	printint(1+1);
	printint(1-1);
	printint(1*1);
	printbool(1<1);
	println();
	int a = 1;
	int b = 1;
	printint(a+b);
	printint(a-b);
	printint(a*b);
	printbool(a<b);
	println();
}
//stdout:201false
//stdout:201false
