#include "minic.h"

int foo() {
	return true;
}

int main() {
	printint(foo());
}
//stderr:Got Bool but expected Int
