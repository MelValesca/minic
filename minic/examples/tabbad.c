#include "minic.h"

int main() {
	int *tab = new int[2];
	bool b = tab[0]; // erreur : tab[0] est un int, pas un bool
	if (b) {
		printint(1);
	}
	delete[] tab;
}
//stderr:Type mismatch.

