#!/usr/bin/env ruby
a = 0
5.times do
  if rand > 0.5
    a = 1
  else
    a = 2
  end
end
print a
