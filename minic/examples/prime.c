#include "minic.h"

// Compte le nombre de nombre premiers inférieurs à max
int main() {
	int n = 2;
	int nb = 0;
	int max = 200;
	while (n<max) {
		int f = 2;
		bool prime = true;
		while (f<n) {
			// calcul du modulo m%f
			int rem = n;
			while (f < rem+1) {
				rem = rem - f;
			}
			if (rem < 1) {
				prime = false;
			}
			f = f + 1;
		}
		if (prime) {
			nb = nb + 1;
		}

		n = n + 1;
	}
	printint(nb);
	println();
}
//stdout:46
