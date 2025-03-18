#include "minic.h"
int mod(int n, int d) {
	int rem = n;
	while (d < rem+1) {
		rem = rem - d;
	}
	return rem;
}

int main() {
	printint(mod(20,8));
	printint(mod(101,3));
	println();
}
//stdout:42
