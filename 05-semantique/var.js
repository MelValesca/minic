#!/usr/bin/env node
a = 1
{
	console.log(a)
}
{
	a = 2
	let a
	console.log(a)
}
console.log(a)
