#include "minic.h"
int foo(int b, int c, int d) {
	int a = b + c;
	b = a - d;
	c = b + c;
	d = a - d;
	return d;
}

int main() {
	printint(foo(20,30,8));
	println();

}
//stdout:42
