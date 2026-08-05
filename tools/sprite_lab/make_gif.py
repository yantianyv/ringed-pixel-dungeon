"""从 hacker.png 生成动画预览 GIF
展示 tier 0 / 3 / 6 并排播放：idle → run → attack → die → operate → read
"""
from PIL import Image
import os

BASE = os.path.dirname(os.path.abspath(__file__))
SHEET = os.path.join(BASE, "..", "..", "core", "src", "main", "assets", "sprites", "hacker.png")

# 动画序列（与 HeroSprite.java 的帧定义一致）
SEQUENCES = [
    ("idle",   [0, 0, 0, 1, 0, 0, 1, 1]),
    ("run",    [2, 3, 4, 5, 6, 7]),
    ("attack", [13, 14, 15, 0]),
    ("die",    [8, 9, 10, 11, 12, 11]),
    ("operate",[16, 17, 16, 17]),
    ("read",   [19, 20, 20, 20, 20, 20, 20, 20, 20, 19]),
]
TIERS = [0, 3, 6]
SCALE = 8
GAP = 4


def main():
    sheet = Image.open(SHEET).convert("RGBA")

    def frame_at(tier, f):
        return sheet.crop((f * 12, tier * 15, f * 12 + 12, tier * 15 + 15))

    frames = []
    durations = []
    for name, seq in SEQUENCES:
        for f in seq:
            panel = Image.new("RGBA", ((12 + GAP) * len(TIERS) + GAP, 15 + 2 * GAP), (40, 40, 50, 255))
            for i, t in enumerate(TIERS):
                panel.paste(frame_at(t, f), (GAP + i * (12 + GAP), GAP), frame_at(t, f))
            frames.append(panel.resize((panel.width * SCALE, panel.height * SCALE), Image.NEAREST))
            durations.append(150 if name in ("run",) else 200)

    out = os.path.join(BASE, "animation_preview.gif")
    frames[0].save(
        out, save_all=True, append_images=frames[1:],
        duration=durations, loop=0, disposal=1,
    )
    print("saved:", out, f"({len(frames)} frames)")
    print("layout: tier0 | tier3 | tier6; sequence:", " → ".join(n for n, _ in SEQUENCES))


if __name__ == "__main__":
    main()
