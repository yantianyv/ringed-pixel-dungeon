"""任务C：buffs.png(7x7) + large_buffs.png(16x16) —— 帧87 骇入 / 帧88 超频
风格：buff 图标惯例 = 满幅背景 + 单色剪影；与天赋图标同一视觉语言
用法: python gen_buff_icons.py  →  写回两贴图 + 输出 _preview_buff.png
"""
from PIL import Image
import os

BASE = os.path.dirname(os.path.abspath(__file__))
ASSETS = os.path.join(BASE, "..", "..", "core", "src", "main", "assets", "interfaces")

PAL = {
    "G": (57, 255, 20),   # 亮绿
    "g": (20, 140, 30),     # 暗绿
    "W": (245, 245, 250),   # 白
    "w": (165, 168, 178),   # 灰
    "Y": (255, 220, 90),    # 黄
}
DARK = (14, 16, 18)

# ---------- 大图标 16x16 ----------
LARGE = {
    # 87 骇入：灰核心 + 绿缠绕环 + 四角数据线头
    87: dict(bg=DARK, rows=[
        "................",
        ".G..........G...",
        "..GGGGGGGGGG....",
        "..G........G....",
        "..G..wwww..G....",
        "..G.wWWWWw.G....",
        "..G.wWWWWw.G....",
        "..G.wWWWWw.G....",
        "..G..wwww..G....",
        "..G........G....",
        "..GGGGGGGGGG....",
        ".G..........G...",
        "................",
        "................",
        "................",
        "................",
    ]),
    # 88 超频：绿芯片 + 黄闪电（与天赋232同语言）
    88: dict(bg=DARK, rows=[
        "................",
        "....G..G..G.....",
        "..GGGGGGGGGG....",
        "..G........G....",
        "..G...Y....G....",
        "..G..YY....G....",
        "..G..YY....G....",
        "..G...YY...G....",
        "..G....YY..G....",
        "..G...YY...G....",
        "..G..YY....G....",
        "..G........G....",
        "..GGGGGGGGGG....",
        "....G..G..G.....",
        "................",
        "................",
    ]),
}

# ---------- 小图标 7x7 ----------
SMALL = {
    87: dict(bg=DARK, rows=[
        ".......",
        ".GGGGG.",
        "G.www.G",
        "G.wWw.G",
        "G.www.G",
        ".GGGGG.",
        ".......",
    ]),
    88: dict(bg=DARK, rows=[
        ".GGGGG.",
        "G..Y..G",
        "G.YY..G",
        "G..YY.G",
        "G.YY..G",
        ".GGGGG.",
        ".......",
    ]),
}



def _center(layer, size, bg):
    """内容层自动居中：bbox 对齐画布中心"""
    bbox = layer.getbbox()
    img = Image.new("RGBA", (size, size), bg)
    if bbox:
        w, h = bbox[2] - bbox[0], bbox[3] - bbox[1]
        img.paste(layer, ((size - w) // 2 - bbox[0], (size - h) // 2 - bbox[1]), layer)
    return img

def render(spec, size):
    layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    for y, row in enumerate(spec["rows"]):
        assert len(row) == size, f"row {y} len={len(row)}"
        for x, ch in enumerate(row):
            if ch != ".":
                layer.putpixel((x, y), PAL[ch] + (255,))
    return _center(layer, size, spec["bg"] + (255,))


def paste(sheet_path, icons, size, cols):
    sheet = Image.open(sheet_path).convert("RGBA")
    for idx, spec in icons.items():
        icon = render(spec, size)
        sheet.paste(icon, ((idx % cols) * size, (idx // cols) * size), icon)
    sheet.save(sheet_path)
    print("written:", sheet_path)
    return sheet


def main():
    small = paste(os.path.join(ASSETS, "buffs.png"), SMALL, 7, 18)
    large = paste(os.path.join(ASSETS, "large_buffs.png"), LARGE, 16, 16)

    # 预览：大图标 + 小图标 + 原版 buff 风格参照（帧 3燃烧/4 等大图）
    scale = 16
    prev = Image.new("RGBA", (8 * 17 + 1, 35), (40, 40, 48, 255))
    items = [(large, 87, 16), (large, 88, 16), (large, 3, 16), (large, 6, 16),
             (small, 87, 7), (small, 88, 7), (small, 3, 7), (small, 6, 7)]
    for i, (sheet, idx, sz) in enumerate(items):
        cols = 16 if sz == 16 else 18
        box = ((idx % cols) * sz, (idx // cols) * sz, (idx % cols) * sz + sz, (idx // cols) * sz + sz)
        prev.paste(sheet.crop(box), (1 + i * 17, 1))
        prev.paste(sheet.crop(box), (1 + i * 17, 18))
    prev = prev.resize((prev.width * scale // 2, prev.height * scale // 2), Image.NEAREST)
    out = os.path.join(BASE, "_preview_buff.png")
    prev.save(out)
    print("preview:", out)


if __name__ == "__main__":
    main()
