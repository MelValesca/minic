#!/usr/bin/env python3
# Compte le nombre de backslash par ligne
import sys
import re
for l in sys.stdin:
    print(len(re.findall('\\\\', l))) # Actual backslash, for real this time. XKCD-1638
