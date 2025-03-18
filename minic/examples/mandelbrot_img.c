#include "minic.h"
int divise(int n, int d) {
	int s = 1;
	if(n<0) {
		n = 0 - n;
		s = 0 - 1;
	}
	int q = 0;
	while(d<n+1) {
		n = n - d;
		q = q + 1;
	}
	return q*s;
}

int mandelbrot(int n) {
	int f = 2;
	int l = 100;
	int z = n*f;
	printint(n*3);
	println();
	printint(n*2);
	println();
	printint(l);
	println();
	int s = 0;
	int j = (0 - n)*f;
	while(j<n*f) {
		int i = (0 - n - n)*f;
		while(i<n*f) {
			int x = 0;
			int y = 0;
			int k = 0;
			bool ok = true;
			while(ok) {
				int sqx = divise(x*x,z);
				int sqy = divise(y*y,z);
				if (sqx+sqy<4*z) {
					int c = divise(x*y,z);
					x = sqx - sqy + i;
					y = 2*c + j;
					k = k + 1;
					if(l<k+1) {
						ok = false;
						k = 0;
					}
				} else {
					ok = false;
					s = s + 1;
				}
			}
			printint(k);
			println();
			i = i + f;
		}
		j = j + f;
	}
	return s;
}

int main() {
	mandelbrot(400);
}
//skip
