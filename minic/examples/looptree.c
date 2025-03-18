#include "minic.h"
int main() {
	int i=0;
	while(i<42) {
		i = i + 1;
		while (i<21) {
			i = i + 1;
			while (i<10) {
				i = i + 1;
			}
			while (i<10) {
				i = i + 1;
			}
		}
		while (i<21) {
			i = i + 1;
			while (i<10) {
				i = i + 1;
			}
		}
	}
	printint(i);
	println();
}
//stdout:42
