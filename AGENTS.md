# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Ringed Pixel Dungeon 是基于 Shattered Pixel Dungeon 的修改版本（mod），一个开源的传统 roguelike 地牢探险游戏。项目使用 Java 开发，支持 Android、iOS 和 Desktop 平台。

## 项目元数据

以下信息来自 `build.gradle` 与 `gradle.properties`：

- 应用名称：`Ringed Pixel Dungeon`
- 包名：`com.yantianyv.ringedpixeldungeon`
- 版本代码：`859`
- 版本名称：`0.1.6_spd3.2.0`
- Java 源码兼容性：`JavaVersion.VERSION_1_8`（构建时推荐使用 JDK 17）
- Android Compile SDK：`35`（Android 15）
- Android Min SDK：`14`（Android 4.0）
- Android Target SDK：`34`（Android 14）
- libGDX：`1.12.1`
- gdx-controllers：`2.2.4`
- RoboVM：`2.3.23`
- Android Gradle Plugin：`8.11.1`
- Gradle：`8.14.3`
- `gradle.properties` 关键项：
  - `android.enableR8.fullMode=false` — 关闭某些会导致 Shattered 崩溃的 R8 优化。
  - `android.overridePathCheck=true` — 项目路径包含非 ASCII 字符，Windows 上需要关闭 Android 路径检查。

## 仓库与贡献策略

- 本仓库**不接受 Pull Requests**。代码按原样提供，不寻求社区直接贡献。
- 问题报告（bug 报告、功能请求等）仍然欢迎。
- 平台编译指南位于 `/docs` 目录：
  - `docs/getting-started-android.md`
  - `docs/getting-started-desktop.md`
  - `docs/getting-started-ios.md`
  - `docs/recommended-changes.md`

## 构建和运行命令

项目使用 Gradle 构建，Java 源码兼容性为 Java 8（`appJavaCompatibility = JavaVersion.VERSION_1_8`），但推荐使用 Java 17 JDK 进行编译。Windows 上使用 `gradlew.bat` 替代 `./gradlew`。

### 常用 Gradle 命令

```bash
# 桌面调试运行
./gradlew desktop:debug

# 构建桌面发布 JAR，输出到 /desktop/build/libs
./gradlew desktop:release

# 生成平台特定可执行文件（需要 JPackage）
./gradlew desktop:jpackageimage

# 构建 Android 调试 APK
./gradlew android:assembleDebug

# 构建 Android 发布 APK
./gradlew android:assembleRelease

# 完整构建所有模块
./gradlew build

# 清理构建产物
./gradlew clean

# 查看所有可用任务
./gradlew tasks --all
```

### 模块结构

`settings.gradle` 中实际包含的模块：

- `SPD-classes`：底层引擎抽象层。
- `core`：核心游戏逻辑。
- `android`：Android 平台入口。
- `desktop`：Desktop 平台入口。
- `services`：服务接口及实现（updates/news）。

> 注意：`ios` 模块在 `settings.gradle` 中当前被注释掉，未参与构建。

### 辅助脚本

项目根目录与 `desktop/` 下提供了几个非 Gradle 辅助脚本：

- `build.bat`（Windows，仓库根目录）
  - 设置 `ANDROID_HOME=./asdk/` 并写入 `local.properties`。
  - 未输入 Keystore 密码时运行 `gradlew assembleDebug`，成功后打开 `android\build\outputs\apk\debug`。
  - 输入密码时尝试用同目录的 `android\key\7z.exe` 解密 `android\key\key.zip` 得到 `key.jks`；若 Keystore 不存在则交互式创建并加密保存。
  - 发布流程执行 `gradlew build`，成功后打开 `android\build\outputs\apk\release`，最后删除明文 `key.jks`。
- `run.bat`（Windows，仓库根目录）
  - 先执行 `gradlew --stop`，再执行 `gradlew debug`（即桌面调试任务）。
- `desktop/notarize.sh`（macOS）
  - 对 `desktop:jpackageimage` 生成的 `.app` 进行 dylib/JAR 签名、深度签名、压缩上传 Apple 公证（notarization）以及 `stapler staple`。
  - 用法：`notarize.sh <path-to-.app> <entitlements.plist> <certificate-name> <apple-id> <app-password>`

### 测试与静态检查

当前仓库没有配置自动化测试或静态分析工具：

- 不存在 `src/test` 或 `src/androidTest` 源码集。
- 没有引入 JUnit、Espresso 或其他测试框架。
- 没有 CI 配置文件。
- Gradle 中没有配置 lint、Checkstyle、SpotBugs、PMD、Detekt 等静态分析工具。
- Android 发布构建使用 R8/ProGuard 进行代码压缩，规则文件为 `android/proguard-rules.pro`。

因此，**没有“运行单个测试”或“lint”命令可用**。

## 项目架构

### 技术栈

项目基于 **libGDX** 框架（版本 1.12.1）构建，采用 Gradle 多模块结构：

- **SPD-classes**: 底层引擎抽象层，包含 OpenGL 封装（`com.watabou.glwrap/gltextures/glscripts`）、输入处理（`com.watabou.input`）、音频（`com.watabou.noosa.audio`）和 2D 场景图（`com.watabou.noosa`）。继承自原始 Pixel Dungeon 引擎，对 libGDX 提供进一步的抽象封装。
- **core**: 核心游戏逻辑，依赖 SPD-classes 提供的渲染和输入能力。
- **desktop/android/ios**: 平台启动入口和平台特定配置，通过 libGDX 的后端实现跨平台。
- **services**: 服务模块，通过接口与 core 解耦：
  - `updates`: 更新检查服务（`debugUpdates` 用于开发，`githubUpdates` 用于生产）
  - `news`: 新闻推送服务（`debugNews` 用于开发，`shatteredNews` 用于生产）

### 核心代码结构

核心代码位于 `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/`：

- **actors**: 游戏中的所有行动实体
  - `Actor.java`: 所有可行动实体的基类，管理行动队列和时间系统
  - `Char.java`: 所有角色的基类（英雄、敌人、NPC 等）
  - `hero/`: 英雄相关代码
  - `mobs/`: 怪物类
  - `buffs/`: 状态效果系统
  - `blobs/`: 区域效果（如毒气、火焰等）

- **items**: 游戏物品系统
  - 按类型分为：armor, artifacts, bags, bombs, food, potions, rings, scrolls, spells, wands, weapon 等
  - `Item.java`: 所有物品的基类
  - `Generator.java`: 物品生成逻辑

- **levels**: 地牢层级和房间系统
  - `Level.java`: 地牢层级的基类
  - `RegularLevel.java`: 标准地牢层级的生成逻辑
  - `builders/`: 地牢构建器
  - `painters/`: 房间绘制器
  - `rooms/`: 各种房间类型
  - `traps/`: 陷阱系统

- **scenes**: 游戏场景和界面
  - `GameScene.java`: 主游戏场景
  - `TitleScene.java`: 标题画面
  - `InterLevelScene.java`: 地牢层级切换场景

- **sprites**: 精灵和动画系统
  - `CharSprite.java`: 角色精灵基类
  - `MobSprite.java`: 怪物精灵基类

- **ui**: 用户界面组件

- **messages**: 本地化文本（位于 `core/src/main/assets/messages/`）

- `Dungeon.java`: 游戏状态管理的中心类
- `ShatteredPixelDungeon.java`: 主游戏类

### 重要架构概念

#### 行动系统 (Actor System)

游戏的核心行动系统基于 `Actor` 类。所有能够采取行动的实体都继承自 `Actor`。行动通过队列管理，每个 Actor 有 `act()` 方法。系统使用 `spend()` 方法来消耗时间，`TICK` 是基本时间单位。

#### 角色 (Char) 系统

`Char` 类是所有可战斗角色的基类，包含 HP、防御、攻击等属性。关键方法：
- `damage()`: 受到伤害
- `die()`: 死亡处理
- `act()`: 行动逻辑

#### 怪物 AI 系统

怪物通过 `State` 系统管理行为模式，常见状态包括：
- `SLEEPING`: 睡眠状态
- `WANDERING`: 游荡状态
- `HUNTING`: 追逐状态
- `FLEEING`: 逃跑状态

#### Buff 系统

Buff 系统通过 `Buff` 类实现所有状态效果，继承自 `FlavourBuff`。关键方法：
- `attachTo()`: 附加到角色
- `detach()`: 从角色移除
- `act()`: 每回合效果

#### 地牢生成系统

地牢生成使用构建器-绘制器模式：
1. `Level` 创建基本结构
2. `Builder` 分割地牢空间
3. `Painter` 填充房间内容
4. `Room` 定义特定房间的生成规则

#### 物品系统

物品通过 `Item` 类及其子类实现。关键方法：
- `collect()`: 拾取物品
- `drop()`: 丢弃物品
- `execute()`: 使用物品

#### 存档系统

存档通过 `Bundle` 类实现，所有需要持久化的类都需要实现：
- `storeInBundle(Bundle)`: 保存状态
- `restoreFromBundle(Bundle)`: 恢复状态

新增字段时必须检查 `bundle.contains()` 以保证向后兼容。

## Ringed Pixel Dungeon 特有架构

### 法师专精「旅行者」(Traveler) 系统

项目在法师子职业中新增「旅行者」专精（见 `HeroSubClass.java` 的 `TRAVELER` 及 `HeroIcon.TRAVELER`）。核心实现：
- `core/src/main/java/.../actors/hero/abilities/mage/TravelerSpells.java`：元素战技/元素爆发逻辑（魔弹、雷霆、解离、焰浪、酸蚀等各法杖分支）
- `core/src/main/java/.../items/rings/RingOfTimetraveler.java`：旅行者专属回响/充能戒指
- `core/src/main/java/.../actors/blobs/TravelerCorrosiveGas.java` 与 `actors/buffs/TravelerCorrosion.java`：酸蚀战技产生的气体与腐蚀状态
- 数值与机制设计文档：根目录 `法师专精设计.md`（元素精通/充能/转化机制说明）

该专精仍在开发中，涉及 buff、blob、spell、i18n 多个模块的联动改动。

### 资源兼容层系统（1000+ 索引机制）

Ringed 版本在部分图标系统中使用 1000+ 索引偏移，把自定义图标与原始 Shattered 图标隔离到不同的 PNG 资源文件。实现方式基本一致：索引 `< 1000` 使用原始资源，索引 `>= 1000` 使用 `_ringed.png` 资源，并在使用时把索引减去 1000 以定位贴图坐标。

当前实际使用该系统的地方：

#### 1. 英雄图标兼容层 (`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/HeroIcon.java`)
- 原始资源：`Assets.Interfaces.HERO_ICONS`（`interfaces/hero_icons.png`）
- Ringed 资源：`Assets.Interfaces.HERO_ICONS_RINGED`（`interfaces/hero_icons_ringed.png`）
- 示例：`MUKBANGER = 1000`（美食家）、`MAGICIAN = 1004`（魔术师）使用 Ringed 图标。
- 逻辑：`icon < 1000` 使用原始资源，否则使用 `_ringed` 资源并把 `icon - 1000`。

#### 2. 天赋图标兼容层 (`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/TalentIcon.java`)
- 原始资源：`Assets.Interfaces.TALENT_ICONS`（`interfaces/talent_icons.png`）
- Ringed 资源：`Assets.Interfaces.TALENT_ICONS_RINGED`（`interfaces/talent_icons_ringed.png`）
- 逻辑与 `HeroIcon` 相同：`< 1000` 走原始资源，`>= 1000` 走 `_ringed` 并减 1000。

#### 3. 浮动文字图标 (`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/FloatingText.java`)
- 原始资源：`Assets.Effects.TEXT_ICONS`（`effects/text_icons.png`）
- Ringed 资源：`Assets.Effects.TEXT_ICONS_RINGED`（`effects/text_icons_ringed.png`）
- 逻辑：`iconIdx < 1000` 时使用 `iconFilm`，否则使用 `ringedIconFilm` 并把索引减 1000。

#### 4. 物品贴图 (`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/ItemSpriteSheet.java`) — 当前未实现
- **注意：`ItemSpriteSheet` 目前没有实现 1000+ 偏移机制。**
- 它只使用单一资源 `Assets.Sprites.ITEMS`（`sprites/items.png`）和单个 `TextureFilm film`。
- 文件中没有 `RINGED_OFFSET`、`filmRinged` 或 `getRingedIndex()`。
- 虽然 `Assets.Sprites.ITEMS_RINGED` 常量被定义为 `"sprites/items_ringed.png"`，但仓库中并不存在对应的 `items_ringed.png` 文件，`ItemSpriteSheet` 也没有引用该常量。
- Ringed 特有物品常量（例如 `RINGED_CAKE`）仍然定义在普通 `< 1000` 索引空间内，通过 `assignItemRect()` 分配到 `items.png` 上。

## 多语言支持

游戏使用 `.properties` 文件存储本地化文本，位于 `core/src/main/assets/messages/`，按模块分子目录，每个子目录内再按语言拆分文件（`<文件名>_<语言代码>.properties`）。中文翻译文件（`_zh`）分布在：
- `messages/actors/actors_zh.properties`：角色/怪物/buff 相关文本
- `messages/items/items_zh.properties`：物品相关文本
- `messages/levels/levels_zh.properties`：地牢相关文本
- `messages/ui/ui_zh.properties`：界面相关文本
- 另有 `journal`、`misc`、`plants`、`scenes`、`windows` 各子目录的 `_zh` 文件，以及根目录 `messages/strings_zh.properties`（基础中文串，当前内容较少）

默认英文文件为相同目录下无语言后缀的 `.properties`（如 `actors.properties`）。

> 注：旧版 AGENTS.md 提到的顶层 `actors_zh.properties` 等路径已过时，中文文件现位于对应子目录内。
