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
    printint(c);
    println();
    return c;
}

int main() {
	gcd(40,24);
	gcd(12,8);
}

//stdout:8
//stdout:4
