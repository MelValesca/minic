extern int a[];
int (*p)[] = &a;
int a[10];
int main() {
	p = &a;
}
