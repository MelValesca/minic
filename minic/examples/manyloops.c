#include "minic.h"
int main() {
	int s=2;
	int i=0;
	while(i<10) {
		if(i<5) {
			int j = i;
			while(0<j) {
				j = j - 1;
				s = s - j;
			}
			int k = i;
			while(0<k) {
				k = k - 1;
				s = s + k * 2;
			}
		} else {
			int k = 0;
			while(k<100) {
				int z = k * 2;
				if (k-50 < 0) {
					s = s - z;
				} else {
					s = s + k;
				}
				int l = 666;
				while (566<l) {
					k = k + 1;
					l = l - 10;
				}
			}
		}
		s = s - i * 16;
		i = i + 1;
	}
	printint(s);
	println();
}
//stdout:42
