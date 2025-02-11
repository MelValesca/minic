#include "minic.c"

int foo() {
	printint(42);
	return 5;
}
int bar(int i) {
	printint(i);
	printint(foo());
}

int main() {
	bar(12);
}
//stdout:12425
