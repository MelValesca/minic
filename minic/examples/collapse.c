#include "minic.h"

int main() {
	int s = 0;
	int i = 0;
	while(i<42) {
		s = s + 1;
		i = i + 1;
	}
	printint(42);
	println();
}
//stdout:42
