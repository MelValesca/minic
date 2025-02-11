#include "minic.c"

int main() {
	bool t = true;
	bool f = false;
	printbool(!t);
	printbool(!f);
	println();

	printbool(t&&t);
	printbool(t&&f);
	printbool(f&&t);
	printbool(f&&f);
	println();

	printbool(t||t);
	printbool(t||f);
	printbool(f||t);
	printbool(f||f);
	println();

	if(1 + 2 < 4 && 2 < 6 || true && !false) {
		printbool(true);
	}
	println();
}
//stdout:falsetrue
//stdout:truefalsefalsefalse
//stdout:truetruetruefalse
//stdout:true
