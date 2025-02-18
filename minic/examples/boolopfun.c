#include "minic.c"

bool i(bool x) {
	printbool(x);
	return x;
}

int main() {
	bool t = true;
	bool f = false;
	printbool(!i(t));
	println();
	printbool(!i(f));
	println();
	println();

	printbool(i(t)&&i(t));
	println();
	printbool(i(t)&&i(f));
	println();
	printbool(i(f)&&i(t));
	println();
	printbool(i(f)&&i(f));
	println();
	println();

	printbool(i(t)||i(t));
	println();
	printbool(i(t)||i(f));
	println();
	printbool(i(f)||i(t));
	println();
	printbool(i(f)||i(f));
	println();
	println();

	if(i(1 + 2 < 40) && i(2 < 6) || i(true) && !i(false)) {
		printbool(true);
	}
	println();
}
//stdout:truefalse
//stdout:falsetrue
//stdout:
//stdout:truetruetrue
//stdout:truefalsefalse
//stdout:falsefalse
//stdout:falsefalse
//stdout:
//stdout:truetrue
//stdout:truetrue
//stdout:falsetruetrue
//stdout:falsefalsefalse
//stdout:
//stdout:truetruetrue
