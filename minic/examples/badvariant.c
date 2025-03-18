#include "minic.h"
int main() {
	int i = 0;
	int j = 0;
	int k = 0;
	int s = 0;
	while(i<21) {
		s = s + 1;
		if (0<j) {
			i = i + 1;
			j = 0;
		} else {
			j = 1;
		}
		k = j * 2;
	}
	printint(s);
	println();
}
//stdout:42
