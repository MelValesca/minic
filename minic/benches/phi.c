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

// Euler's totient function. A000010
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

// Sum of totient function. A002088
int main() {
	int n = 0;
	int s = 0;
	while (n < 5000) {
		s = s + phi(n);
		n = n + 1;
	}
	printint(s);
	println();
}

//stdout:7598458
