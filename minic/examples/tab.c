#include "minic.h"

int main() {
	int *tab = new int[3];
	tab[0] = 7;
	tab[1] = 4;
	tab[2] = tab[0] + tab[1];
	printint(tab[2]);
	println();
	delete[] tab;
}
//stdout:11
