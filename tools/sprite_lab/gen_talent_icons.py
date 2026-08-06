"""任务B：talent_icons_ringed.png 骇客 20 天赋图标 —— 帧 224-243（无主空帧区）
风格基准：天赋图标惯例 = 满幅背景 + 多色小物件（2-4色）
骇客主题：深底 + 矩阵绿物件 + 白/灰/黄/蓝点缀
用法: python gen_talent_icons.py  →  写回贴图 + 输出 _preview_talent.png 供视觉审查
"""
import math
from PIL import Image
import os

BASE = os.path.dirname(os.path.abspath(__file__))
ASSETS = os.path.join(BASE, "..", "..", "core", "src", "main", "assets", "interfaces")
SHEET = os.path.join(ASSETS, "talent_icons_ringed.png")

# 公用调色板
PAL = {
    "G": (57, 255, 20),   # 亮绿·骇客主色
    "g": (20, 140, 30),     # 暗绿
    "W": (245, 245, 250),   # 白
    "w": (165, 168, 178),   # 灰
    "Q": (90, 92, 100),     # 深灰（暗部/轮廓）
    "Y": (255, 220, 90),    # 黄
    "B": (100, 170, 255),   # 蓝
    "b": (70, 110, 180),    # 暗蓝
    "R": (235, 90, 80),     # 红
    "n": (150, 110, 70),    # 棕
}
DARK = (14, 16, 18)

ICONS = {}

# ---------- 224 全息口粮：三明治 + 绿色全息虚线框 ----------
ICONS[224] = dict(bg=DARK, rows=[
    "................",
    "..G.G.G.G.G.G.G.",
    "................",
    "..G..........G..",
    ".....wwwwww.....",
    "....wWWWWWWw....",
    "....wGGGGGGw....",   # 生菜层
    "....wnnnnnnw....",   # 馅料层
    "....wWWWWWWw....",
    ".....wwwwww.....",
    "..G..........G..",
    "................",
    "..G.G.G.G.G.G.G.",
    "................",
    "................",
    "................",
])

# ---------- 225 符石混淆：灰符石 + 绿问号 ----------
ICONS[225] = dict(bg=DARK, rows=[
    "................",
    ".....wwww.......",
    "...wwWWWWWWww...",
    "..wWWWWWWWWWWw..",
    "..wWW..GG..WWw..",
    "..wW..G..G..Ww..",
    "..wW.....G..Ww..",
    "..wW....G...Ww..",
    "..wW....G...Ww..",
    "..wW.......Ww...",
    "..wW....G...Ww..",
    "..wWW......WWw..",
    "..wWWWWWWWWWWw..",
    "...wwWWWWWWww...",
    ".....wwww.......",
    "................",
])

# ---------- 226 护甲穿透：绿箭贯穿灰盾 ----------
ICONS[226] = dict(bg=DARK, rows=[
    "................",
    "......wwww......",
    ".....wWWWWw.....",
    "....wWWWWWWw....",
    "...wWWWWWWWWw...",
    ".G.wWWWQWWWWw...",
    "GGGGwWWQ.WWWw...",
    "GGGGGwWQ..WWw...",
    "GGGGwWWQ.WWWw...",
    ".G.wWWWQWWWWw...",
    "...wWWWWWWWWw...",
    "....wWWWWWWw....",
    ".....wWWWWw.....",
    "......wWWw......",
    "................",
    "................",
])

# ---------- 227 反向代理：防火墙（砖墙+火苗） ----------
ICONS[227] = dict(bg=DARK, rows=[
    "....Y...Y.......",
    "...YRY.YRY......",
    ".wwwwwwwwwwwwww.",
    ".wQQ.wQQ..wQQ.w.",
    ".wwwwwwwwwwwwww.",
    ".wQ..wQQ.wQQ..w.",
    ".wwwwwwwwwwwwww.",
    ".wQQ.wQQ..wQQ.w.",
    ".wwwwwwwwwwwwww.",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
])

# ---------- 228 屏蔽一餐：三明治 + 右下小盾 ----------
ICONS[228] = dict(bg=DARK, rows=[
    "................",
    "....wwwwww......",
    "...wWWWWWWw.....",
    "...wGGGGGGw.....",
    "...wnnnnnnw.....",
    "...wWWWWWWw.....",
    "....wwwwww......",
    ".........www....",
    ".......wwWWWw...",
    ".......wWgWWw...",
    ".......wWWWWw...",
    "........wWWw....",
    ".........ww.....",
    "................",
    "................",
    "................",
])

# ---------- 229 液冷散热：蓝液滴 + 白米字雪花 ----------
ICONS[229] = dict(bg=(18, 30, 48), rows=[
    "................",
    "......BB........",
    ".....BBBB.......",
    "....BBBBBB......",
    "....BBbbBB......",
    "....BbbbbB......",
    "....BBBBBB......",
    ".....BBBB.......",
    "................",
    "....W.....W.....",
    ".....W...W......",
    "..WW..WWW..WW...",
    ".....W...W......",
    "....W.....W.....",
    "................",
    "................",
])

# ---------- 230 广度优先：BFS 树（根-2-4） ----------
ICONS[230] = dict(bg=DARK, rows=[
    "................",
    ".......GG.......",
    ".......GG.......",
    "......G..G......",
    ".....G....G.....",
    "....GG....GG....",
    "....GG....GG....",
    "...G.G....G.G...",
    "..G...G..G...G..",
    ".GG...GG.GG...GG",
    ".GG...GG.GG...GG",
    "................",
    "................",
    "................",
    "................",
    "................",
])

# ---------- 231 设备提权：#（root 提示符） ----------
ICONS[231] = dict(bg=DARK, rows=[
    "................",
    ".....G..G.......",
    ".....G..G.......",
    "...GGGGGGGG.....",
    ".....G..G.......",
    ".....G..G.......",
    ".....G..G.......",
    "...GGGGGGGG.....",
    ".....G..G.......",
    ".....G..G.......",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
])

# ---------- 232 超频运算：绿芯片 + 黄闪电 ----------
ICONS[232] = dict(bg=DARK, rows=[
    "....G..G..G.....",
    "..GGGGGGGGGG....",
    "..G........G....",
    "..G.GGGG...G....",
    "G.G.G..YY..G.G..",
    "..G.G.YY...G....",
    "G.G.GYY.GG.G.G..",
    "..G.YY..GG.G....",
    "G.GYY..GG..G.G..",
    "..G.GGGG...G....",
    "..G........G....",
    "..GGGGGGGGGG....",
    "....G..G..G.....",
    "................",
    "................",
    "................",
])

# ---------- 233 零日漏洞：绿毛毛虫（bug，与骇入甲虫区分） ----------
ICONS[233] = dict(bg=DARK, rows=[
    "................",
    "...G......G.....",
    "....G....G......",
    "....GGGGGG......",
    "...GGGGGGGG.....",
    "..GGGGGGGGGG....",
    ".G.GGGGGGGG.G...",
    "..GGGGgGGGGG....",
    ".G.GGGgGGGG.G...",
    "..GGGGgGGGGG....",
    ".G.GGGGGGGG.G...",
    "..GGGGGGGGGG....",
    "...GGGGGGGG.....",
    "................",
    "................",
    "................",
])

# ---------- 234 僵尸网络：三绿屏互联 ----------
ICONS[234] = dict(bg=DARK, rows=[
    "................",
    "................",
    ".GGGGGGGGGGGGGG.",
    "...G....G....G..",
    "...G....G....G..",
    "..GGG..GGG..GGG.",
    "..GgG..GgG..GgG.",
    "..GGG..GGG..GGG.",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
])

# ---------- 235 内核爆破：绿芯片 + 黄星芒 ----------
ICONS[235] = dict(bg=DARK, rows=[
    "..Y..........Y..",
    "...Y........Y...",
    "....Y......Y....",
    ".....GGGGGG.....",
    "...YGGGGGGGGY...",
    "....GG..GG.G....",
    ".....G.YY..G....",
    "Y...GG.YY.G...Y.",
    ".....G.YY..G....",
    "....GG..GG.G....",
    "...YGGGGGGGGY...",
    ".....GGGGGG.....",
    "....Y......Y....",
    "...Y........Y...",
    "..Y..........Y..",
    "................",
])

# ---------- 236 花式入侵：白扑克牌 + 绿箭头 ----------
ICONS[236] = dict(bg=DARK, rows=[
    "................",
    "...WWWWWW.......",
    "...WRRRRW.......",
    "...W.R..W.......",
    "...W.RR.W.......",
    "...W.R..W.......",
    "...WRRRRW.......",
    "...WWWWWW.......",
    "................",
    "......G.........",
    ".......GG.......",
    "..GGGGGGGGGGGG..",
    ".......GG.......",
    "......G.........",
    "................",
    "................",
])

# ---------- 237 子网广播：同心圆波纹 ----------
ICONS[237] = dict(bg=DARK, rows=[
    "................",
    ".....gggg.......",
    "...gg......gg...",
    "..g...GGGG...g..",
    ".g..GG....GG..g.",
    ".g.G...gg...G.g.",
    "g..G..gGGg..G..g",
    "g..G..gGGg..G..g",
    ".g.G...gg...G.g.",
    ".g..GG....GG..g.",
    "..g...GGGG...g..",
    "...gg......gg...",
    ".....gggg.......",
    "................",
    "................",
    "................",
])

# ---------- 238 稳定映射：灰船锚（锚定=稳定） ----------
ICONS[238] = dict(bg=DARK, rows=[
    "................",
    "................",
    "................",
    "...GGG....GGG...",
    "..GGGGG..GGGGG..",
    "..GGGGGGGGGGGG..",
    "..GGGGG..GGGGG..",
    "...GGG....GGG...",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
])

# ---------- 239 动态类型：绿两脚插头 + 灰插座 ----------


# ---------- 240 内存回收：绿内存条 + 循环箭头 ----------


# ---------- 241 长效续航：满格电池 + 黄闪电 ----------
ICONS[240] = dict(bg=DARK, rows=[
    "................",
    "......ww........",
    "..wwwwwwwwww....",
    "..wWWWWWWWWw....",
    "...wwwwwwww.....",
    "...wWWWWWWw.....",
    "...wWQWQWw......",
    "...wWQWQWw......",
    "...wWQWQWw......",
    "...wWQWQWw......",
    "...wWWWWWWw.....",
    "....wwwwww......",
    "................",
    "................",
    "................",
    "................",
])

ICONS[241] = dict(bg=DARK, rows=[
    "................",
    "..wwwwwwwwww....",
    ".wGGGGGGGGGGw.w.",
    ".wGG..YY..GGw.ww",
    ".wGG.YY..GGGw.w.",
    ".wGG..YY.GGGw...",
    ".wGG.YY..GGGw...",
    ".wGGYY...GGGw...",
    "..wwwwwwwwww....",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
])

# ---------- 242 全牢光纤：左并拢右扇形散开(绿蓝白, 2px粗) ----------
ICONS[242] = dict(bg=DARK, rows=[
    "................",
    "..............GG",
    ".............GG.",
    "............GG..",
    "..GGGGGGGGGG....",
    "..GGGGGGGGGG....",
    "..BBBBBBBBBBB...",
    "..BBBBBBBBBBB...",
    "..WWWWWWWWWW....",
    "..WWWWWWWWWW....",
    "............WW..",
    ".............WW.",
    "................",
    "................",
    "................",
    "................",
])

# ---------- 243 信号增幅：信号格阶梯 + 上箭头 ----------
ICONS[243] = dict(bg=DARK, rows=[
    "................",
    "...........G....",
    "..........GGG...",
    ".........GGGGG..",
    "...........G....",
    "..G........G....",
    "..G..G..........",
    "..G..G..G.......",
    "..G..G..G..G....",
    ".GG.GG.GG.GG....",
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
])


def arc_arrow(size, r, arcs, color):
    """程序化弧箭头：arcs=[(起始角, 结束角), ...] 逆时针，楔形箭头尖在结束端"""
    img = Image.new("RGBA", (size, size), DARK + (255,))
    cx = cy = size / 2 - 0.5
    for start, end in arcs:
        steps = max(12, abs(end - start) // 4)
        for t in range(steps + 1):
            a = math.radians(start + (end - start) * t / steps)
            img.putpixel((round(cx + r * math.cos(a)), round(cy - r * math.sin(a))), color + (255,))
        ae = math.radians(end)
        ex, ey = cx + r * math.cos(ae), cy - r * math.sin(ae)
        tx, ty = -math.sin(ae), -math.cos(ae)
        nx, ny = math.cos(ae), math.sin(ae)
        for back, half in [(-2, 0), (-1, 1), (0, 2), (1, 1)]:
            for side in range(-half, half + 1):
                img.putpixel((round(ex - tx * back + nx * side), round(ey - ty * back + ny * side)), color + (255,))
    return img


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
        assert len(row) == 16, f"row {y} len={len(row)}: {row!r}"
        for x, ch in enumerate(row):
            if ch != ".":
                layer.putpixel((x, y), PAL[ch] + (255,))
    return _center(layer, 16, spec["bg"] + (255,))


def main():
    sheet = Image.open(SHEET).convert("RGBA")
    cols = sheet.width // 16
    for idx, spec in ICONS.items():
        icon = render_icon(spec)
        sheet.paste(icon, ((idx % cols) * 16, (idx // cols) * 16), icon)
    # 程序化图标（无 ASCII 稿）：239 动态类型双弧循环 / 240 内存回收中心对称三弧
    for idx, icon in {
        239: arc_arrow(16, 5, [(170, 20), (350, 200)], PAL["G"]),
    }.items():
        sheet.paste(icon, ((idx % cols) * 16, (idx // cols) * 16), icon)
    sheet.save(SHEET)
    print("written:", SHEET)

    # 预览：两行放大 + 两行 1x 观感
    scale = 12
    idxs = sorted(ICONS)
    per_row = 10
    W = per_row * 17 + 1
    prev = Image.new("RGBA", (W, 35), (40, 40, 48, 255))
    rows_img = []
    for r in range(2):
        part = Image.new("RGBA", (W, 35), (40, 40, 48, 255))
        for i, idx in enumerate(idxs[r * per_row:(r + 1) * per_row]):
            box = ((idx % cols) * 16, (idx // cols) * 16, (idx % cols) * 16 + 16, (idx // cols) * 16 + 16)
            part.paste(sheet.crop(box), (1 + i * 17, 1))
            part.paste(sheet.crop(box), (1 + i * 17, 18))
        rows_img.append(part)
    prev = Image.new("RGBA", (W, 71), (40, 40, 48, 255))
    prev.paste(rows_img[0], (0, 0))
    prev.paste(rows_img[1], (0, 36))
    prev = prev.resize((prev.width * scale // 2, prev.height * scale // 2), Image.NEAREST)
    out = os.path.join(BASE, "_preview_talent.png")
    prev.save(out)
    print("preview:", out)


if __name__ == "__main__":
    main()
