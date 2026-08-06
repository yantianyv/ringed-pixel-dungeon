"""任务A：hero_icons_ringed.png 四帧 —— 旅行者修复 + 木马大师/架构师/广播风暴
风格基准：原版惯例 = 满幅彩色背景 + 中央对比色剪影符号
用法: python gen_hero_icons.py  →  写回贴图 + 输出 _preview_hero.png 供视觉审查
"""
from PIL import Image
import os

BASE = os.path.dirname(os.path.abspath(__file__))
ASSETS = os.path.join(BASE, "..", "..", "core", "src", "main", "assets", "interfaces")
SHEET = os.path.join(ASSETS, "hero_icons_ringed.png")

# 背景色用空格字符表示（ASCII 中 '.' = 透明/保持背景）
# 每帧: (帧索引, 背景色, ASCII符号层, 调色板)
ICONS = {}

# ---------- 帧2 旅行者：深蓝底 + 亮青沙漏（时间旅行/元素流转） ----------
ICONS[2] = dict(
    bg=(36, 30, 74),
    pal={
        "S": (120, 226, 255),   # 沙漏主体·亮青
        "s": (250, 250, 255),   # 沙粒·亮白
    },
    rows=[
        "................",
        "................",
        "..SSSSSSSSSSSS..",
        "..SS........SS..",
        "...SS......SS...",
        "....SS....SS....",
        ".....SS..SS.....",
        "......SS........",
        "......SS........",
        ".....SS..SS.....",
        "....SS.s..SS....",
        "...SS.sss..SS...",
        "..SS.sssss..SS..",
        "..SSSSSSSSSSSS..",
        "................",
        "................",
    ],
)

# ---------- 帧10 木马大师(借牧师位)：黑底 + 矩阵绿马头侧影（特洛伊木马） ----------
# 马头侧影(朝左)：双耳尖、长鼻梁、鼻孔、粗脖颈 —— 国际象棋 knight 式轮廓
# 正面马头：竖耳 + 长脸 + 双眼 + 鼻孔（对称构图，16px 下最稳）
# ---------- 帧10 木马大师：完整木马（头/鬃/尾/四腿） ----------
ICONS[10] = dict(
    bg=(14, 16, 18),
    pal={
        "T": (232, 220, 196),  # 面具·奶油色
        "U": (188, 172, 146),  # 面具阴影
        "y": (34, 28, 24),     # 眉眼胡须
        "p": (226, 150, 132),  # 红晕
        "G": (57, 255, 20),    # 绿眼·荧光绿
    },
    rows=[
        "................",
        "....TTTTTT......",
        "...TTTTTTTT.....",
        "..UTTTTTTTTU....",
        "..UyyTTTTyyU....",
        "..UyGTTTTGyU....",
        "..UTTTTTTTTU....",
        "..UpTTTTTTpU....",
        "...UTyTTyTU.....",
        "....TyyyyT......",
        ".....yyyy.......",
        ".....TyyT.......",
        "......TT........",
        "................",
        "................",
        "................",
    ],
)

# ---------- 帧11 架构师(借圣骑位)：蓝图蓝底 + 亮青三角尺（镂空内三角） ----------
ICONS[11] = dict(
    bg=(18, 42, 74),
    pal={
        "C": (140, 230, 255),  # 尺·亮青
        "c": (60, 130, 170),   # 刻度量角·暗青
    },
    rows=[
        "................",
        "..C.............",
        "..CC............",
        "..C.C...........",
        "..C..C..........",
        "..C...C.........",
        "..C....C........",
        "..C...cC........",
        "..C..c..C.......",
        "..C..ccc.C......",
        "..C.......C.....",
        "..C........C....",
        "..C.c.c.c..C....",
        "..CCCCCCCCCCCC..",
        "................",
        "................",
        "................",
    ],
)

# ---------- 帧35 广播风暴(abilities空档)：黑底 + 绿天线塔 + 三层弧波 ----------
# 信号塔：塔顶信号点 + 两侧三层同心圆弧(数学取点,内亮外淡) + 桁架底座
ICONS[35] = dict(
    bg=(14, 16, 18),
    pal={
        "G": (57, 255, 20),
        "g": (20, 140, 30),   # 外层弧·暗绿
    },
    rows=[
        "................",
        "................",
        ".....g....g.....",
        "....g......g....",
        "...g..G..G..g...",
        "..g...G..G...g..",
        "..g..G.GG.G..g..",
        "..g..G.GG.G..g..",
        "..g..G.GG.G..g..",
        "...g..G..G..g...",
        "....g..GG..g....",
        "......GGGG......",
        ".....GG..GG.....",
        "....GGGGGGGG....",
        "................",
        "................",
    ],
)



def _center(layer, size, bg):
    """内容层自动居中：bbox 对齐画布中心"""
    bbox = layer.getbbox()
    img = Image.new("RGBA", (size, size), bg)
    if bbox:
        w, h = bbox[2] - bbox[0], bbox[3] - bbox[1]
        img.paste(layer, ((size - w) // 2 - bbox[0], (size - h) // 2 - bbox[1]), layer)
    return img

def render_icon(spec):
    layer = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, row in enumerate(spec["rows"]):
        assert len(row) == 16, f"row {y} len={len(row)}"
        for x, ch in enumerate(row):
            if ch != ".":
                layer.putpixel((x, y), spec["pal"][ch] + (255,))
    return _center(layer, 16, spec["bg"] + (255,))


def main():
    sheet = Image.open(SHEET).convert("RGBA")
    cols = sheet.width // 16
    for idx, spec in ICONS.items():
        icon = render_icon(spec)
        sheet.paste(icon, ((idx % cols) * 16, (idx // cols) * 16), icon)
    sheet.save(SHEET)
    print("written:", SHEET)

    # 预览：新四帧 + 原版风格参照帧(0狂战/2战法/3术士) 放大对比
    ref = Image.open(os.path.join(ASSETS, "hero_icons.png")).convert("RGBA")
    scale = 16
    picks = [("new", 2), ("new", 10), ("new", 11), ("new", 35), ("ref", 0), ("ref", 2), ("ref", 3)]
    W = len(picks) * 17 + 1
    prev = Image.new("RGBA", (W, 35), (40, 40, 48, 255))
    for i, (kind, idx) in enumerate(picks):
        src = sheet if kind == "new" else ref
        box = ((idx % cols) * 16, (idx // cols) * 16, (idx % cols) * 16 + 16, (idx // cols) * 16 + 16)
        prev.paste(src.crop(box), (1 + i * 17, 1))
        # 缩小到游戏内实际观感(1x)放第二行，检验可辨识度
        prev.paste(src.crop(box), (1 + i * 17, 18))
    prev = prev.resize((prev.width * scale // 2, prev.height * scale // 2), Image.NEAREST)
    out = os.path.join(BASE, "_preview_hero.png")
    prev.save(out)
    print("preview:", out)


if __name__ == "__main__":
    main()
