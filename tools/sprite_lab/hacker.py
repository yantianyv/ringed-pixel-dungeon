"""黑客英雄贴图 —— SPD 官方画风 v2
官方约定：无描边、大头短腿、3/4侧脸(脸朝右、左侧阴影)、tier0赤身
用法: python hacker.py  →  preview_hacker.png
     python compare.py →  与官方并排对比

字符表:
  .  透明
  d  兜帽最深   e 兜帽主色   f 兜帽受光
  b  皮肤高光   s 皮肤主色   S 皮肤阴影   q 皮肤深影(边缘)
  G  发光绿(眼睛/设备屏)
  w  缠腰布白   W 缠腰布灰
  r  草鞋深棕   R 草鞋浅棕
"""
from PIL import Image
import os

BASE = os.path.dirname(os.path.abspath(__file__))

PALETTE = {
    "d": (16, 16, 20),        # 纯黑兜帽·边缘影
    "e": (30, 32, 38),        # 纯黑兜帽·主色
    "f": (50, 54, 64),        # 纯黑兜帽·微光
    "T": (232, 220, 196),     # V字面具·奶油色
    "U": (188, 172, 146),     # 面具阴影
    "y": (34, 28, 24),        # 面具五官(眉眼胡须)
    "j": (130, 110, 92),      # 胡须过渡色
    "p": (226, 150, 132),     # 红晕脸颊
    "b": (240, 205, 170),
    "s": (224, 186, 150),
    "S": (188, 148, 118),
    "q": (150, 112, 88),
    "G": (110, 230, 140),
    "w": (228, 228, 233),
    "W": (188, 188, 198),
    "r": (118, 84, 52),
    "R": (150, 110, 70),
}

# 头部：兜帽+3/4侧脸（所有护甲等级共用，官方惯例头不变只换衣服）
# 关键手法(学rogue)：脸宽7px、左缘整列深影、眼睛挤在右半脸、额头右上最亮
HEAD = [
    "....deeed...",                 # 兜帽圆顶
    "...defffeed.",                 # 右侧受光
    "...defffffd.",
    "...dUTTTTUd.",                # 面具额头·明亮（V字面具的高额头是灵魂）
    "..ddUyTTyUd.",                # 高挑细眉(x5,x8)
    "..ddpGTTGpd.",                # 绿眼+红晕脸颊
    "...dTyyyyTd.",                # 八字胡
    "....qTyyTq..",                # 山羊胡
]

# tier 0：赤身+缠腰布+草鞋（官方英雄的基础形态）
IDLE = HEAD + [
    "..qssssssq..",                 # 肩
    ".qSsssssbSq.",                 # 胸·右侧高光
    ".qSssssssGq.",                 # 右腕发光腕带
    ".qSssssssSq.",
    "..qwwwwwq...",                 # 缠腰布
    "..sS...Ss...",                 # 小短腿
    "..rR...Rr...",                 # 草鞋
]

RUN_A = IDLE[:13] + [
    "..sS....Ss..",
    "..rR....Rr..",
]

def sh(row, n=1):
    return "." * n + row[:12 - n]

ATTACK = [sh(r) for r in IDLE[:9]] + [
    sh(IDLE[9]),
    "..qSsssssG..",                 # 直拳+绿色能量
    "..qSssssSq..",
    "...qwwwwq...",
    "..sS....Ss..",
    "..rR....Rr..",
]


def render_frame(rows):
    assert len(rows) == 15, f"need 15 rows, got {len(rows)}"
    img = Image.new("RGBA", (12, 15), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(rows):
        assert len(row) == 12, f"row {y} len={len(row)}: {row!r}"
        for x, ch in enumerate(row):
            if ch == ".":
                continue
            assert ch in PALETTE, f"unknown char {ch!r} at ({x},{y})"
            px[x, y] = PALETTE[ch] + (255,)
    return img


def main():
    frames = [("idle", IDLE), ("run_a", RUN_A), ("attack", ATTACK)]
    scale = 20
    sheet = Image.new("RGBA", (12 * len(frames) + 4 * (len(frames) - 1), 15), (60, 60, 70, 255))
    x = 0
    for name, rows in frames:
        sheet.paste(render_frame(rows), (x, 0))
        x += 16
    sheet = sheet.resize((sheet.width * scale, sheet.height * scale), Image.NEAREST)
    out = os.path.join(BASE, "preview_hacker.png")
    sheet.save(out)
    print("saved:", out)


if __name__ == "__main__":
    main()
