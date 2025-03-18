#include "minic.h"
int foo(int a) {
       int i = 0;
       int cpt = 0;
       int j = 0;
       int k = 0;
       int l = 0;
       while(i<a) {
	       if (1<cpt) {
		       j = i * 2 - 2;
		       if (2<cpt) {
			       k = j * 2 - 3;
			       if (3<cpt) {
				       l = k * 2 - 4;
				       cpt = 0;
			       }
		       }
	       }
	       cpt = cpt + 1;
	       i = i + 1;
	       printint(l);
       }
       return k;
}
int main() {
	printint(foo(10));
	println();
}
//stdout:000014141414464625
