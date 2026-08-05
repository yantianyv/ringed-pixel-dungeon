# 黑客英雄 选人界面立绘（splash）提示词

规格：800×450，JPG，风格对齐 Shattered Pixel Dungeon 官方 splash（厚涂数字绘画，非像素风）。

## 英文主提示词

```
Digital painting, painterly fantasy game splash art. A mysterious vigilante
hacker stands centered-left, full body, facing the viewer at a slight
three-quarter angle: wearing a plain black hooded jacket with the hood up,
a cream-white Guy Fawkes mask with thin arched eyebrows, upturned mustache
and small goatee, rosy cheeks, and softly glowing green eyes behind the
mask. He holds one hand forward with faint green holographic code fragments
floating above his palm. The background transitions from left to right:
behind him on the left, a dark dungeon corridor of rough stone bricks with
a warm torch glow, and the stones are dissolving into streams of glowing
green digital glyphs; the right side opens into a vast sci-fi matrix
void of falling green code rain and dark cyberspace. He stands on the
boundary, half in the dungeon, half in the digital world. Dramatic rim
lighting, deep shadows, cinematic composition, dark fantasy meets cyberpunk,
high detail, ultrawide 21:9 landscape, keep the far-right and bottom-right
corner mostly empty background.
```

## 负面提示词

```
pixel art, lowres, blurry, extra limbs, deformed hands, text, watermark, logo,
photorealistic face, unmasked face, modern clothing logos, bright background
```

## 关键要素对照（给模型加权用）

| 要素 | 权重建议 | 说明 |
|------|---------|------|
| Guy Fawkes mask | 高 | 奶油色面具是角色核心标识，可用 `(cream-white Guy Fawkes mask:1.3)` |
| black hood up | 高 | 纯黑连帽衫 |
| glowing green eyes | 中 | 绿光是角色配色记忆点 |
| green holographic code | 中 | 手上的绿色代码碎片，点到即止 |
| dungeon corridor + torch | 中 | 与官方 splash 场景语言一致 |

## 建议流程

1. 用主提示词出大图，**画幅用 21:9（如 2016×864），人物偏左构图、右下角留空**——为裁掉豆包水印预留余量
2. 挑一张满意的，面具细节不对可以用 inpainting 局部修
3. 出图后交给我，我写脚本做：裁掉右下水印区 → 裁回 16:9 → 缩放到 800×450 → 色调微调 → 导出 JPG 到 `core/src/main/assets/splashes/hacker.jpg`
