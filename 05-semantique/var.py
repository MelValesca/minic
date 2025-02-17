#!/usr/bin/env python3
a = 1
def foo():
    print(a)
def bar():
    a = 2
    print(a)
def baz():
    print(a)
    a = 3
foo()
bar()
baz()
print(a)
