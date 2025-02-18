#include "minic.c"
int main() {
	int x = 10+5;
	int y = 5*2+(x-5);
	printint(y);
	println();
}
//stdout:20
