#include "minic.h"

int main() {
	int *p = new int;
	*p = 99;
	delete[] p;
}
//stderr:Type mismatch.
