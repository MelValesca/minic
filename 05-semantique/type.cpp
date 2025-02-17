#include <string>
std::string foo() { return 0l; }
std::string foo2() { return (long)0; }
std::string bar() { long i = 0l; return i; }
