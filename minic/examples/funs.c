#include "minic.c"

int aaa() {}
bool bbb() {}
int ccc(int x) { return x; }
bool ddd(bool x) { return x; }

int main() {
	printint(aaa());
	printbool(bbb());
	printint(ccc(1));
	printbool(ddd(true));
	println();
}
//stdout:0false1true
