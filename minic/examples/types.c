#include "minic.h"
int main() {
	int a = 1;
	a = 1 + 2 - 3 * 4;
	bool b = true;
	b = false;
	b = 1 < 2;
	if (b) {}
	if (b) {} else {}
	while(false) {}
	printint(a);
	println();
	printbool(b);
	println();
}
//stdout:-9
//stdout:true
