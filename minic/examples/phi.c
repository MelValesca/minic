#include "minic.h"

int gcd(int a, int b) {
    int c = a;
    int d = b;
    if (c < 1) {
        return d;
    }
    while (0 < d) {
        if (d < c) {
            c = c - d;
	} else {
            d = d - c;
	}
    }
    return c;
}

// Euler's totient function
int phi(int n) {
	int i=1;
	int r=0;
	while(i<n+1) {
		if(gcd(i,n)<2) {
			r = r + 1;
		}
		i = i + 1;
	}
	return r;
}

int main() {
	printint(phi(98));
	printint(phi(98));
	println();
}

//stdout:4242
