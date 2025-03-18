#include "minic.h"
int main() {
	printint(4000+4000);
	printint(4000-4000);
	printint(4000*4000);
	printbool(4000<4000);
	println();
	int a = 4000;
	int b = 4000;
	printint(a+b);
	printint(a-b);
	printint(a*b);
	printbool(a<b);
	println();
}
//stdout:8000016000000false
//stdout:8000016000000false
