#include "minic.h"
int main() {
	int a = 1;
	if (1<2) {
		a = 2;
		int a = 3;
		a = 4;
		printint(a);
	}
	printint(a);
}
//stdout:42