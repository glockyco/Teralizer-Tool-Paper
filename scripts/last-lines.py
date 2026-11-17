"""List paragraphs of build/main.pdf whose last line is shorter than a share of the column width.

A line of body text that starts at the left edge of its column and ends early is the last line of
a paragraph, because all other lines are justified. The lines of the output listing in Fig. 3 are
skipped by their height.

Usage: python scripts/last-lines.py [share]    (default 0.5, needs pdftotext)
"""

import html
import re
import subprocess
import sys

# Left and right edge of the two columns of IEEEtran conference papers on letter paper, in points.
COLUMNS = ((48.9, 300.96), (311.9, 563.04))
BODY_LINE_HEIGHT = (8.0, 13.0)

share = float(sys.argv[1]) if len(sys.argv) > 1 else 0.5
subprocess.run(["pdftotext", "-bbox-layout", "build/main.pdf", "build/main.bbox.html"], check=True)
with open("build/main.bbox.html", encoding="utf-8") as file:
    pages = re.split(r"<page ", file.read())[1:]

line_pattern = re.compile(
    r'<line xMin="([\d.]+)" yMin="([\d.]+)" xMax="([\d.]+)" yMax="([\d.]+)">(.*?)</line>', re.S
)
found = False
for number, page in enumerate(pages, 1):
    for match in line_pattern.finditer(page):
        x_min, y_min, x_max, y_max = map(float, match.group(1, 2, 3, 4))
        left, right = COLUMNS[0] if x_min < 305 else COLUMNS[1]
        filled = (x_max - left) / (right - left)
        height = y_max - y_min
        starts_at_edge = abs(x_min - left) < 3
        if starts_at_edge and filled < share and BODY_LINE_HEIGHT[0] < height < BODY_LINE_HEIGHT[1]:
            words = re.findall(r"<word[^>]*>(.*?)</word>", match.group(5), re.S)
            text = " ".join(html.unescape(word) for word in words)
            print(f"page {number}, {'left' if x_min < 305 else 'right'} column, {filled:.0%}: {text}")
            found = True
sys.exit(1 if found else 0)
