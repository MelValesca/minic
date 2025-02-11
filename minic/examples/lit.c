#include "minic.c"
int main() {
	int sum = 0;
	int i = 0;
	while(i<10000) {
		int j = 0;
		while(j<10000) {
			sum = sum + 123456789;
			j = j + 1;
		}
		i = i + 1;
	}
	printint(sum);
	println();
}
