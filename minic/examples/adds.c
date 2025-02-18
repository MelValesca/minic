#include "minic.c"
int main() {
	int a = 1;
	while(a<5) {
		a = a;
		a = a + 1;
		a = a;
		a = a + 1;
	}
	printint(a);
	println();
}
//stdout:5
