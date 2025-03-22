#include "minic.h"

int main() {
	int *p = new int;
	*p = 10;
	printint(*p);
	println();
	delete p;
}
//stdout:10

