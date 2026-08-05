"""量产：合成黑客英雄完整 sprite sheet (256x128, 21帧 x 7tier)
策略：
  tier 0-5 动作帧(0-7,13-20): 我的头部 + 官方rogue身体(肤色重映射)
  tier 0-5 死亡帧(8-12):      手绘"面具幽灵消散"
  tier 6 动作帧:              矩阵头 + 官方t0身体剪影的像素瀑布(每帧不同种子→流动效果)
  tier 6 死亡帧:              绿色数字幽灵消散
"""
from PIL import Image
import os
import random
import hacker

BASE = os.path.dirname(os.path.abspath(__file__))
SPRITES = os.path.join(BASE, "..", "..", "core", "src", "main", "assets", "sprites")
ROGUE = Image.open(os.path.join(SPRITES, "rogue.png")).convert("RGBA")

P = hacker.PALETTE
SKIN_MAP = {
    (220, 180, 151): P["s"],
    (190, 127, 89): P["S"],
    (133, 89, 56): P["q"],
    (247, 230, 209): P["b"],
}

# ---------- 死亡动画：面具幽灵逐渐消散 ----------
# 5帧：完整幽灵 → 缩小 → 只剩绿色碎粒
WISP = [
    # f8 完整幽灵（兜帽+面具+绿眼+胡须+尾部）
    [
        "............",
        "............",
        "....deed....",
        "...deffed...",
        "...dUTTUd...",
        "...dTGGTd...",
        "...dTyyTd...",
        "....dTTd....",
        "....d..d....",
        ".....dd.....",
        "............",
        "............",
        "............",
        "............",
        "............",
    ],
    # f9 幽灵略缩，绿色碎粒开始飘散
    [
        "............",
        ".........g..",
        "....deed....",
        "...deffed.g.",
        "...dUTTUd...",
        "...dTGGTd...",
        "...dTyyTd.g.",
        "....dTTd....",
        ".....dd.....",
        "............",
        "......g.....",
        "............",
        "............",
        "............",
        "............",
    ],
    # f10 缩小一圈
    [
        "............",
        "............",
        "............",
        "....deed..g.",
        "....dTTd....",
        "....dGGd....",
        "....dTTd.g..",
        ".....dd.....",
        "......g.....",
        "............",
        "....g.......",
        "............",
        "............",
        "............",
        "............",
    ],
    # f11 只剩核心+碎粒
    [
        "............",
        ".....g......",
        "............",
        "....dTTd....",
        "....dGGd.g..",
        ".....dd.....",
        "......g.....",
        "...g........",
        "............",
        ".....g......",
        "............",
        "............",
        "............",
        "............",
        "............",
    ],
    # f12 几乎完全消散
    [
        "............",
        ".....g......",
        "............",
        ".....G......",
        "..g.........",
        "............",
        "......g.....",
        "............",
        "............",
        "............",
        "............",
        "............",
        "............",
        "............",
        "............",
    ],
]

# tier6 死亡幽灵的调色板（矩阵绿版）
PAL6 = {
    "k": (10, 12, 11),
    "K": (24, 30, 26),
    "G": (110, 230, 140),
    "g": (50, 120, 70),
}
WISP6_MAP = {"d": "k", "e": "K", "f": "K", "U": "K", "T": "K", "y": "g", "G": "G", "g": "g"}


def remap_skin(px):
    r, g, b, a = px
    if a < 128:
        return (0, 0, 0, 0)
    return SKIN_MAP.get((r, g, b), (r, g, b)) + (255,)


def official_body(tier, frame):
    """官方 rogue 某 tier 某帧的身体(rows 8-14)"""
    body = Image.new("RGBA", (12, 7), (0, 0, 0, 0))
    for y in range(7):
        for x in range(12):
            body.putpixel((x, y), remap_skin(ROGUE.getpixel((frame * 12 + x, tier * 15 + 8 + y))))
    return body


def official_t0_silhouette(frame):
    """官方 t0 某帧全身剪影(12x15 bool)，用于 tier6 瀑布"""
    sil = set()
    for y in range(15):
        for x in range(12):
            if ROGUE.getpixel((frame * 12 + x, y))[3] >= 128:
                sil.add((x, y))
    return sil


def rain_image(sil, seed):
    """矩阵数字雨：黑色半透明剪影 + 每列连续垂直字符流（亮头+暗尾），底部消散"""
    img = Image.new("RGBA", (12, 15), (0, 0, 0, 0))
    rng = random.Random(seed)
    grid = {}
    for (x, y) in sil:
        edge = (x - 1, y) not in sil or (x + 1, y) not in sil
        grid[(x, y)] = "k" if edge else "K"
    # 底部逐渐消散成碎像素
    for (x, y) in list(grid):
        if y >= 11 and rng.random() < (y - 10) * 0.25:
            del grid[(x, y)]
    # 极简：只撒少量零星亮点（每帧换位 → 闪烁感）
    cells = sorted(grid)
    rng.shuffle(cells)
    for (x, y) in cells[:5]:
        grid[(x, y)] = "G"
    for (x, y) in cells[5:8]:
        grid[(x, y)] = "g"
    for (x, y), ch in grid.items():
        img.putpixel((x, y), PAL6[ch] + (255,))
    return img


def render_rows(rows, palette):
    img = Image.new("RGBA", (12, 15), (0, 0, 0, 0))
    for y, row in enumerate(rows):
        assert len(row) == 12, f"row {y} len={len(row)}"
        for x, ch in enumerate(row):
            if ch != ".":
                img.putpixel((x, y), palette[ch] + (255,))
    return img


def main():
    head = hacker.render_frame(hacker.HEAD + ["............"] * 7).crop((0, 0, 12, 8))

    sheet = Image.new("RGBA", (256, 128), (0, 0, 0, 0))
    ACTION_FRAMES = list(range(0, 8)) + list(range(13, 21))
    DIE_FRAMES = list(range(8, 13))

    for tier in range(7):
        for f in range(21):
            fx, fy = f * 12, tier * 15
            if tier == 6:
                if f in DIE_FRAMES:
                    rows = [[WISP6_MAP.get(c, c) for c in row] for row in WISP[f - 8]]
                    rows = ["".join(r) for r in rows]
                    frame_img = render_rows(rows, PAL6)
                else:
                    # 整体剪影+字符雨（连头部一起数字化，无脸幽灵）
                    frame_img = rain_image(official_t0_silhouette(f), seed=f * 7 + 1)
            else:
                if f in DIE_FRAMES:
                    frame_img = render_rows(WISP[f - 8], {**P, "g": (50, 120, 70)})
                else:
                    frame_img = Image.new("RGBA", (12, 15), (0, 0, 0, 0))
                    frame_img.paste(head, (0, 0), head)
                    frame_img.paste(official_body(tier, f), (0, 8))
            sheet.paste(frame_img, (fx, fy), frame_img)

    out_assets = os.path.join(SPRITES, "hacker.png")
    sheet.save(out_assets)
    print("saved sheet:", out_assets)

    # 预览接触表：7行 tier
    scale = 10
    gap = 2
    W = (12 + gap) * 21 + gap
    H = (15 + gap) * 7 + gap
    contact = Image.new("RGBA", (W, H), (50, 50, 60, 255))
    for tier in range(7):
        for f in range(21):
            frame = sheet.crop((f * 12, tier * 15, f * 12 + 12, tier * 15 + 15))
            contact.paste(frame, (gap + f * (12 + gap), gap + tier * (15 + gap)), frame)
    contact = contact.resize((contact.width * scale, contact.height * scale), Image.NEAREST)
    out = os.path.join(BASE, "hacker_sheet_preview.png")
    contact.save(out)
    print("saved preview:", out)


if __name__ == "__main__":
    main()
