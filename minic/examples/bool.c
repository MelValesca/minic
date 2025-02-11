#include "minic.c"
int main() {
	bool a = true;
	bool b = 0<1;
	if(b) { printbool(a); println(); }
}
//stdout:true
