template<int i>class X{};
template<class t>class Y{};
Y<X<(1>>2)>>a1;
Y<X< 1>>2 >>a2;
