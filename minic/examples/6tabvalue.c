#include "minic.h"

int main() {
	int *p = new int;
	*p = 42;

	int *tab = new int[3];
	tab[0] = 1;
	tab[1] = 2;
	tab[2] = *p;

	printint(*p);
	println();
	printint(tab[0]);
	println();
	printint(tab[1]);
	println();
	printint(tab[2]);
	println();

	delete p;
	delete[] tab;
}
//stdout:42
//stdout:1
//stdout:2
//stdout:42
