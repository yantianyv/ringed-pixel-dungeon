# 骇客 & 旅行者 贴图生成规划

> **状态：贴图已完成（定稿）并验收**。历经三轮迭代（具象 → 抽象符号 → 用户反馈调整），最终版已合并为 4 个独立脚本。
> 管线：`tools/sprite_lab/` ASCII 程序化像素画 + 自动居中（内容层 bbox 对齐画布中心）。
> **生成脚本（各自幂等，任意顺序重跑均可）**：
> - `gen_hero_icons.py` → `hero_icons_ringed.png`（帧 2/10/11/35）
> - `gen_talent_icons.py` → `talent_icons_ringed.png`（帧 224–243，239/240 为程序化弧箭头）
> - `gen_buff_icons.py` → `buffs.png` + `large_buffs.png`（帧 87/88 两尺寸）
> - `gen_item_icons.py` → `items.png`（帧 400/401）
> 预览：`tools/sprite_lab/_preview_final.png`（全量 30 帧）。
> 已有资源（commit 7ce475775）：`sprites/hacker.png` 精灵表、`splashes/hacker.jpg` 立绘、`avatars.png` 第6格头像。
> 配色：荧光绿 `#39FF14` / 暗绿 `#148C1E` + 深底，与 hacker.png 矩阵绿一致。

## 帧位分配约定（用户确认）

**ringed 贴图帧位与原版一一对应，每个位置预留给该英雄的 mod 扩展**；牧师位可借用。talent_icons 原版 256 帧容量只用 0–186，**帧 187–255 为无主空帧区**（215–218 已被 mod 画过但无代码引用，避开）。

## 最终帧分配与完成状态

### A. `interfaces/hero_icons_ringed.png`（16×16）✅

| 帧 | 常量 | 内容 |
|---|---|---|
| 2 | TRAVELER = 1002 | **旅行者修复**：深蓝底 + 亮青沙漏（原为复制残留 BATTLEMAGE 法杖） |
| 10 | **新增 1010** | **木马大师**（借牧师位）：黑底 + 绿病毒颗粒（球体+刺突） |
| 11 | **新增 1011** | **架构师**（借圣骑位）：蓝图蓝底 + 亮青三角尺 |
| 35 | **新增 1035** | **广播风暴**（abilities 与牧师法术空档 35–39）：黑底 + 绿信号塔同心弧 |

### B. `interfaces/talent_icons_ringed.png`（16×16，帧 224–243 无主空帧区）✅

| 帧 | 天赋 | 帧 | 天赋 |
|---|---|---|---|
| 224 | 全息口粮（虚线框三明治） | 234 | 僵尸网络（LAN 总线标识） |
| 225 | 符石混淆（符石+?） | 235 | 内核爆破（芯片星芒） |
| 226 | 护甲穿透（箭穿盾） | 236 | 花式入侵（扑克+箭头） |
| 227 | 反向代理（⇄） | 237 | 子网广播（同心圆） |
| 228 | 屏蔽一餐（盾+叉） | 238 | 稳定映射（双节点连线） |
| 229 | 液冷散热（液滴+雪花） | 239 | 动态类型（双弧循环箭头） |
| 230 | 广度优先（BFS 树） | 240 | 内存回收（三弧♻️箭头） |
| 231 | 设备提权（王冠） | 241 | 长效续航（电池+∞） |
| 232 | 超频运算（芯片+闪电） | 242 | 全牢光纤（v1 细线束） |
| 233 | 零日漏洞（锁+裂纹） | 243 | 信号增幅（信号格+箭头） |

### C. `interfaces/buffs.png`（7×7）+ `large_buffs.png`（16×16）✅

| 帧 | buff | 内容 |
|---|---|---|
| 87 | **骇入**（debuff，需层数角标） | 黑底 + 绿瘦身甲虫（bug 双关） |
| 88 | **超频**（buff） | 黑底 + 绿芯片 + 黄闪电 |

### D. `sprites/items.png`（16×16）✅

| 位置 | 物品 | 内容 |
|---|---|---|
| xy(1,26) = 帧 400 | **便携终端** | 实心纯黑矩形 + 荧光绿 `>_` |
| xy(12,12) = 帧 187（**护甲区 ARMOR+11 空槽**） | **骇客职业甲**（广播风暴载体） | 黑连帽衫 + 轮廓描边 + 绿拉链 |

> 皇帝的新全息匕首 = 纯透明，不占美术；`ItemSpriteSheet` 指向一个全透明帧即可。

## 代码注册清单（实现时做）

- `ui/HeroIcon.java`：新增 `TROJAN_MASTER = 1010`、`ARCHITECT = 1011`、`BROADCAST_STORM = 1035`
- `actors/hero/HeroSubClass.java`：骇客两专精指向 1010/1011；`HeroClass.HACKER` 填入专精数组
- `actors/hero/Talent.java`：20 天赋枚举，icon 用 1224–1243；`initClassTalents/initSubclassTalents` 挂载 HACKER 分支
- `actors/hero/abilities/hacker/BroadcastStorm.java`：新建 ArmorAbility，`icon()` 返回 1035，`talents()` 挂帧 241/242/243 + HEROIC_ENERGY
- `ui/BuffIndicator.java`：新增 `HACKED = 87`、`OVERCLOCK = 88`
- `sprites/ItemSpriteSheet.java`：新增常量指向帧 400/187（`ARMOR + 11`）+ `assignItemRect`；匕首指全透明帧
- 验收：git 对比脚本已验证四张贴图差异帧与上表完全一致（hero 帧 8/9 决斗家预留位无残留）

## 预览图

`tools/sprite_lab/_preview_final.png`（全量 30 帧）、`_preview_v3.png`（v3 六帧）、`_preview_v2.png`（v1/v2 对比）。
