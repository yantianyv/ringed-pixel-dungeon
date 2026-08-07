"""任务D：items.png —— 便携终端 xy(1,26) / 骇客职业甲 xy(2,26)
风格：SPD 物品惯例 = 彩色物件 + 深色轮廓/立体阴影
配色：hacker 兜帽色系（深黑布料）+ 矩阵绿点缀
用法: python gen_item_icons.py  →  写回贴图 + 输出 _preview_items.png
"""
from PIL import Image
import os

BASE = os.path.dirname(os.path.abspath(__file__))
ITEMS = os.path.join(BASE, "..", "..", "core", "src", "main", "assets", "sprites", "items.png")

PAL = {
    "q": (16, 16, 20),      # 布料最深/轮廓
    "K": (0, 0, 0),          # 纯黑
    "Q": (30, 32, 38),      # 布料主色
    "f": (50, 54, 64),      # 布料微光
    "G": (57, 255, 20),   # 亮绿
    "g": (20, 140, 30),     # 暗绿（代码行）
    "W": (245, 245, 250),   # 白
    "w": (120, 124, 134),   # 键帽灰
}
WIDTH = 16

ICONS = {}

# ---------- 便携终端：扁平化 —— 深色方块 + 荧光绿 >_ ----------
ICONS[(1, 26)] = dict(rows=[
    "................",
    ".KKKKKKKKKKKKKK.",
    ".KKGGGKKKKKKKKK.",
    ".KKKGGGKKKKKKKK.",
    ".KKKKGGGKKKKKKK.",
    ".KKKKKGGGKKKKKK.",
    ".KKKKKGGGKKKKKK.",
    ".KKKKGGGKKKKKKK.",
    ".KKKGGGKKKKKKKK.",
    ".KKKKKKKGGGGGGK.",
    ".KKKKKKKGGGGGGK.",
    ".KKKKKKKKKKKKKK.",
    ".KKKKKKKKKKKKKK.",
    "................",
    "................",
    "................",
])

# ---------- 骇客职业甲：黑连帽衫（兜帽开口） + 绿拉链 ----------
ICONS[(12, 12)] = dict(rows=[
    "................",
    ".....ffffff.....",
    "....fQQQQQQf....",
    "...fQQQQQQQQf...",
    "..fQQQQQQQQQQf..",
    ".fQQQQQQQQQQQQf.",
    ".fQqQQGffGQQqQf.",
    ".fQqQQGffGQQqQf.",
    ".fQqQQGffGQQqQf.",
    ".fQqQQGffGQQqQf.",
    ".fQqQQGffGQQqQf.",
    ".fQqQQGffGQQqQf.",
    "..fQQQGffGQQQf..",
    "...ffffffffff...",
    "................",
    "................",
])



def _center(layer, size, bg):
    """内容层自动居中：bbox 对齐画布中心"""
    bbox = layer.getbbox()
    img = Image.new("RGBA", (size, size), bg)
    if bbox:
        w, h = bbox[2] - bbox[0], bbox[3] - bbox[1]
        img.paste(layer, ((size - w) // 2 - bbox[0], (size - h) // 2 - bbox[1]), layer)
    return img

def render(spec):
    layer = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, row in enumerate(spec["rows"]):
        assert len(row) == 16, f"row {y} len={len(row)}"
        for x, ch in enumerate(row):
            if ch != ".":
                layer.putpixel((x, y), PAL[ch] + (255,))
    return _center(layer, 16, (0, 0, 0, 0))


def main():
    sheet = Image.open(ITEMS).convert("RGBA")
    for (x, y), spec in ICONS.items():
        icon = render(spec)
        sheet.paste(icon, ((x - 1) * 16, (y - 1) * 16))
    sheet.save(ITEMS)
    print("written:", ITEMS)

    # 预览：两个新物品 + 原版参照（行2武器/行27末区几个）放大
    scale = 16
    picks = [(1, 26), (2, 26), (1, 2), (2, 2), (5, 14), (1, 32)]
    prev = Image.new("RGBA", (len(picks) * 17 + 1, 35), (40, 40, 48, 255))
    for i, (x, y) in enumerate(picks):
        box = ((x - 1) * 16, (y - 1) * 16, (x - 1) * 16 + 16, (y - 1) * 16 + 16)
        prev.paste(sheet.crop(box), (1 + i * 17, 1))
        prev.paste(sheet.crop(box), (1 + i * 17, 18))
    prev = prev.resize((prev.width * scale // 2, prev.height * scale // 2), Image.NEAREST)
    out = os.path.join(BASE, "_preview_items.png")
    prev.save(out)
    print("preview:", out)


if __name__ == "__main__":
    main()
