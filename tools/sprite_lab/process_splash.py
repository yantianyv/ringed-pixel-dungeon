"""立绘后处理：裁水印 → 裁16:9 → 缩放800x450 → 导出 splashes/hacker.jpg"""
from PIL import Image
import os
import sys

SRC = sys.argv[1] if len(sys.argv) > 1 else r"C:\Users\yanti\Downloads\8A6840507172AE98D3339210A69EA67F.jpg"
BASE = os.path.dirname(os.path.abspath(__file__))
DST = os.path.join(BASE, "..", "..", "core", "src", "main", "assets", "splashes", "hacker.jpg")

img = Image.open(SRC).convert("RGB")
w, h = img.size
print(f"source: {w}x{h}")

# 从右侧裁到 16:9（水印在右下角，右側是空虚空，直接裁掉）
target_w = round(h * 16 / 9)
img = img.crop((0, 0, min(target_w, w), h))
print(f"after 16:9 crop: {img.size}")

# 缩放到官方规格 800x450
img = img.resize((800, 450), Image.LANCZOS)
img.save(DST, quality=90)
print("saved:", DST)
