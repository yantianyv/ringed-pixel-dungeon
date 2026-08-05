"""组装黑客英雄的 tier 0-6 全身预览
tier 0: 自制赤身   tier 1-5: 官方rogue护甲描摹(肤色重映射)   tier 6: 自制发光电路战甲
"""
from PIL import Image
import os
import hacker

BASE = os.path.dirname(os.path.abspath(__file__))
SPRITES = os.path.join(BASE, "..", "..", "core", "src", "main", "assets", "sprites")

# 官方肤色 -> 我的肤色
SKIN_MAP = {
    (220, 180, 151): hacker.PALETTE["s"],
    (190, 127, 89): hacker.PALETTE["S"],
    (133, 89, 56): hacker.PALETTE["q"],
    (247, 230, 209): hacker.PALETTE["b"],
}

def get_official_body(tier):
    """提取 rogue 某 tier 的身体(rows 8-14)，肤色重映射"""
    img = Image.open(os.path.join(SPRITES, "rogue.png")).convert("RGBA")
    body = Image.new("RGBA", (12, 7), (0, 0, 0, 0))
    for y in range(7):
        for x in range(12):
            r, g, b, a = img.getpixel((x, tier * 15 + 8 + y))
            if a < 128:
                continue
            rgb = SKIN_MAP.get((r, g, b), (r, g, b))
            body.putpixel((x, y), rgb + (255,))
    return body

# tier 6：遁入数字状态——兜帽剪影+垂直字符雨+底部消散（参考：黑客帝国）
PAL6 = {
    "k": (10, 12, 11),     # 剪影黑·边缘
    "K": (24, 30, 26),     # 剪影黑·内部
    "G": (110, 230, 140),  # 荧光绿·亮字符
    "g": (50, 120, 70),    # 暗绿·尾迹
}

def build_tier6_frame(seed=42):
    """以角色自身轮廓为剪影，生成数字雨全身图（15行整图）"""
    import random
    base = hacker.render_frame(hacker.IDLE)
    sil = set()
    for y in range(15):
        for x in range(12):
            if base.getpixel((x, y))[3] >= 128:
                sil.add((x, y))
    rng = random.Random(seed)
    grid = {}
    for (x, y) in sil:
        edge = (x - 1, y) not in sil or (x + 1, y) not in sil
        grid[(x, y)] = "k" if edge else "K"
    for (x, y) in list(grid):  # 底部消散
        if y >= 11 and rng.random() < (y - 10) * 0.25:
            del grid[(x, y)]
    # 极简：只撒少量零星亮点
    cells = sorted(grid)
    rng.shuffle(cells)
    for (x, y) in cells[:5]:
        grid[(x, y)] = "G"
    for (x, y) in cells[5:8]:
        grid[(x, y)] = "g"
    img = Image.new("RGBA", (12, 15), (0, 0, 0, 0))
    for (x, y), ch in grid.items():
        img.putpixel((x, y), PAL6[ch] + (255,))
    return img

def render(rows, palette):
    img = Image.new("RGBA", (12, len(rows)), (0, 0, 0, 0))
    for y, row in enumerate(rows):
        assert len(row) == 12, f"row {y}: {row!r} len={len(row)}"
        for x, ch in enumerate(row):
            if ch != ".":
                img.putpixel((x, y), palette[ch] + (255,))
    return img

def main():
    head = hacker.render_frame(hacker.HEAD + ["............"] * 7).crop((0, 0, 12, 8))
    tier0 = hacker.render_frame(["............"] * 8 + hacker.IDLE[8:]).crop((0, 8, 12, 15))

    bodies = [tier0] + [get_official_body(t) for t in range(1, 6)] + [None]
    tier6_full = build_tier6_frame()

    scale = 16
    gap = 4
    W = (12 + gap) * 7 + gap
    H = 15 + 2 * gap
    sheet = Image.new("RGBA", (W, H), (50, 50, 60, 255))
    for t, body in enumerate(bodies):
        if t == 6:
            sheet.paste(tier6_full, (gap + t * (12 + gap), gap), tier6_full)
            continue
        frame = Image.new("RGBA", (12, 15), (0, 0, 0, 0))
        h = head
        frame.paste(h, (0, 0), h)
        frame.paste(body, (0, 8), body)
        sheet.paste(frame, (gap + t * (12 + gap), gap), frame)
    sheet = sheet.resize((sheet.width * scale, sheet.height * scale), Image.NEAREST)
    out = os.path.join(BASE, "hacker_tiers.png")
    sheet.save(out)
    print("saved:", out, "| tier0-6")

if __name__ == "__main__":
    main()
