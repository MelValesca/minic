#include "minic.h"

int main() {
	if(true) { printint(1); }
	if(false) { printint(0); }
	if(true) { printint(2); } else { printint(0); }
	if(false) { printint(0); } else { printint(3); }
	println();
	if(1<2) { printint(1); }
	if(2<1) { printint(0); }
	if(1<2) { printint(2); } else { printint(0); }
	if(2<1) { printint(0); } else { printint(3); }
	println();
	int a = 1;
	int b = 2;
	if(a<b) { printint(1); }
	if(b<a) { printint(0); }
	if(a<b) { printint(2); } else { printint(0); }
	if(b<a) { printint(0); } else { printint(3); }
	println();
}
//stdout:123
//stdout:123
//stdout:123
