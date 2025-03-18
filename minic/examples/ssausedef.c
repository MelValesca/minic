#include "minic.h"
int main() {
	int a = 1;
	if (false) {
		a = 2;
	} else {
		if (true) {
			a = 3;
		}
	}
	printint(a+1);
	printint(a-1);
	println();
	return a-3;
}
//stdout:42
