#include "minic.h"
int foo(int x) {
	int a = 2;
	printint(a+a);
	if(0<a) {
		if(x<a) {
			x = x + 1;
			printint(a);
		} 
	}
}

int main() {
	foo(0);
	println();
}
//stdout:42
