"""把黑客头像加入 avatars.png（24x32/格，按 HeroClass.ordinal 索引，HACKER=6）"""
from PIL import Image
import os

BASE = os.path.dirname(os.path.abspath(__file__))
SPRITES = os.path.join(BASE, "..", "..", "core", "src", "main", "assets", "sprites")

avatars = Image.open(os.path.join(SPRITES, "avatars.png")).convert("RGBA")
sheet = Image.open(os.path.join(SPRITES, "hacker.png")).convert("RGBA")

# tier0 idle 帧（12x15）放大2倍 → 24x30，居中放入 24x32 格
idle = sheet.crop((0, 0, 12, 15))
big = idle.resize((24, 30), Image.NEAREST)

ORDINAL = 6  # HACKER 在 HeroClass 枚举中的位置
cell = Image.new("RGBA", (24, 32), (0, 0, 0, 0))
cell.paste(big, (0, 2), big)
avatars.paste(cell, (ORDINAL * 24, 0), cell)

avatars.save(os.path.join(SPRITES, "avatars.png"))
print("avatar added at index", ORDINAL)

# 预览
preview = avatars.resize((avatars.width * 6, avatars.height * 6), Image.NEAREST)
preview.save(os.path.join(BASE, "avatars_preview.png"))
print("preview saved")
