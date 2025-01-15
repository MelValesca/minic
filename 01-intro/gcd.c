int gcdExtended(int *a, int *b, int* x, int* y)
{
    int x1, y1;
    int m = *b%*a;
    int d = *b/ *a;
    int gcd = gcdExtended(&m, a, &x1, &y1);
    *x = y1 - d * x1;
    *y = x1;
    return gcd;
}
