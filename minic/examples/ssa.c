#include "minic.h"

int main() {
	int a = 1;
	if(true) {
		a = 2;
	} else {
		a = 3;
	}
	printint(a);
	println();
}
//stdout:2
