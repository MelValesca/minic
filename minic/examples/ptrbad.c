#include "minic.h"

int main() {
	int *p = new int;
	*p = 10;
	bool b = p;
	if (b) {
		printint(*p);
	}
	delete p;
}
//stderr:Type mismatch.
