#include "minic.h"
int main() {
	int s = 2;
	int i = 0;
	while (i<10) {
		int k = i * 6 - 23;
		s = s + k;
		i = i + 1;
	}
	printint(s);
	println();
}
//stdout:42
