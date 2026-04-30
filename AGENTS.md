# AGENTS.md — Ringed Pixel Dungeon

This file contains essential context for AI coding agents working on the Ringed Pixel Dungeon codebase. The project is a mod of [Shattered Pixel Dungeon](https://shatteredpixel.com/shatteredpd/), an open-source traditional roguelike dungeon crawler. All agents should read this file before making code changes.

---

## Project Overview

- **Name**: Ringed Pixel Dungeon
- **Package**: `com.yantianyv.ringedpixeldungeon`
- **Version**: `0.1.5_spd3.2.0` (version code `859`)
- **License**: GNU General Public License v3.0
- **Original base**: Shattered Pixel Dungeon by Evan Debenham (based on Pixel Dungeon by Watabou)
- **Language**: Java (source compatibility Java 8)
- **Recommended JDK**: Java 17 (used by Android Studio and the desktop jpackage toolchain)

The game is a traditional roguelike with randomized levels, turn-based combat, and hundreds of items. It targets Android, Desktop (Windows/Linux/macOS), and has a dormant iOS module.

---

## Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Game Framework | libGDX | 1.12.1 |
| Build System | Gradle | 8.14.3 (via wrapper) |
| Android Gradle Plugin | `com.android.tools.build:gradle` | 8.11.1 |
| Desktop Packaging | `org.beryx.runtime` (jpackage) | 1.12.7 |
| iOS (dormant) | RoboVM | 2.3.23 |
| Controllers | gdx-controllers | 2.2.4 |
| JSON | org.json | 20170516 |

---

## Project Structure (Gradle Multi-Module)

```
ringed-pixel-dungeon/
├── SPD-classes/          # Low-level engine abstraction over libGDX
├── core/                 # Core game logic, actors, items, levels, scenes
├── desktop/              # Desktop launcher (LWJGL3 backend)
├── android/              # Android launcher and APK build
├── ios/                  # iOS launcher (RoboVM; commented out in settings.gradle)
├── services/             # Decoupled service interfaces + implementations
│   ├── updates/
│   │   ├── debugUpdates/     # No-op update checker (debug builds)
│   │   └── githubUpdates/    # GitHub release checker (release builds)
│   └── news/
│       ├── debugNews/        # No-op news feed (debug builds)
│       └── shatteredNews/    # ShatteredPixel news feed (release builds)
└── docs/                 # Human-readable guides
```

### Module Details

- **SPD-classes**: Contains the original Pixel Dungeon engine abstractions: OpenGL wrappers (`com.watabou.glwrap`), 2D scene graph (`com.watabou.noosa`), audio (`com.watabou.noosa.audio`), input (`com.watabou.input`), and utilities (`com.watabou.utils`). Core depends on this module.
- **core**: The bulk of the game. Depends on `SPD-classes` and `services`.
- **desktop**: Depends on `core`, `services:updates:githubUpdates`, and `services:news:shatteredNews`. Uses LWJGL3 and bundles a custom JRE via `jpackage`.
- **android**: Depends on `core`. Uses debug services for debug builds and production services for release builds. Includes native `.so` copying tasks for libGDX.
- **services**: Defines interfaces for updates and news. Implementations are swapped per build type to keep platform-specific networking out of `core`.

---

## Build and Run Commands

All commands are run from the project root. On Windows use `gradlew.bat`; on Linux/macOS use `./gradlew`.

### Desktop

```bash
# Run debug build (development)
./gradlew desktop:debug

# Build release fat-JAR
./gradlew desktop:release
# Output: desktop/build/libs/

# Build platform-native installer/image (requires jpackage)
./gradlew desktop:jpackageImage
# Output: desktop/build/jpackage/
```

On macOS the debug task automatically adds `-XstartOnFirstThread`.

### Android

```bash
# Debug APK
./gradlew android:assembleDebug
# Output: android/build/outputs/apk/debug/

# Release APK
./gradlew android:assembleRelease
# Output: android/build/outputs/apk/release/
```

Release signing expects a keystore at `android/key/key.jks` with password from the `PASSWORD` environment variable. A helper `build.bat` script exists for Windows that manages keystore creation/encryption and triggers release builds.

### General

```bash
# Clean all modules
./gradlew clean

# Stop Gradle daemon
./gradlew --stop
```

A convenience `run.bat` exists for Windows:
```bat
run.bat   # stops daemon then runs desktop:debug
```

---

## Code Organization (core Module)

The main package is `com.shatteredpixel.shatteredpixeldungeon`.

| Package | Responsibility |
|---------|---------------|
| `actors` | All acting entities: hero, mobs, buffs, blobs (area effects) |
| `items` | All item types: weapons, armor, potions, scrolls, rings, wands, food, trinkets, etc. |
| `levels` | Dungeon generation: builders, painters, rooms, traps, terrain |
| `scenes` | Game screens: title, game, inter-level transitions, menus |
| `sprites` | Character and item sprites, animations, visual states |
| `ui` | UI widgets, buttons, status indicators, changelogs |
| `windows` | In-game popup windows (inventory, examine, dialogs) |
| `effects` | Visual effects, particles, floating text |
| `messages` | Localization key constants |
| `journal` | In-game journal / document system |
| `mechanics` | Game mechanics helpers (e.g., shadows, visibility) |
| `plants` | Dungeon plant types |
| `tiles` | Tilemap rendering helpers |
| `utils` | General utilities used across core |

### Key Classes

- **`ShatteredPixelDungeon.java`**: Main `Game` subclass; entry point for scenes, version constants, and compatibility hooks.
- **`Dungeon.java`**: Global game state (current level, hero, depth, flags).
- **`Actor.java`**: Base class for everything that takes turns. Manages the action queue and time system (`spend()`, `TICK`).
- **`Char.java`**: Base class for all combat-capable characters (hero, mobs, NPCs).
- **`Item.java`**: Base class for all items.
- **`Level.java`** / **`RegularLevel.java`**: Dungeon level base and standard generation logic.
- **`Bundle.java`** / **`Bundlable.java`**: Save/load serialization system.
- **`GamesInProgress.java`**: Save slot management.
- **`SPDSettings.java`**: Persistent user preferences.

---

## Development Conventions

### Code Style

- **Indentation**: Tabs (width visually ~4 spaces).
- **Braces**: K&R style (opening brace on the same line).
- **Encoding**: UTF-8 (explicitly set in all Gradle compile tasks).
- **Imports**: Wildcard imports are common (e.g., `import com.watabou.noosa.*`).
- **Copyright header**: Every source file carries a multi-line GPL v3 header crediting Pixel Dungeon, Shattered Pixel Dungeon, and Ringed Pixel Dungeon. **Preserve this header on new files.**

### Naming

- Classes use `PascalCase`.
- Constants use `SCREAMING_SNAKE_CASE`.
- Methods and fields use `camelCase`.
- Boolean state getters often use plain nouns or adjectives (e.g., `active`, `visible`) rather than `is` prefixes.

### Version Compatibility

The codebase contains extensive backward-compatibility code keyed by version constants in `ShatteredPixelDungeon.java` (e.g., `v1_2_3 = 628`). When adding new persistent fields:

1. Use `bundle.contains("key")` before reading.
2. Provide sensible defaults for missing legacy data.
3. Increment `appVersionCode` in the root `build.gradle` when releasing.

---

## Ringed-Specific Resource System (1000+ Offset)

Ringed Pixel Dungeon isolates its custom sprites from upstream Shattered Pixel Dungeon via an **index offset of 1000**.

- **Index >= 1000** → loads from Ringed-specific atlases (e.g., `items_ringed.png`, `hero_icons_ringed.png`, `talent_icons_ringed.png`).
- **Index < 1000** → loads from original Shattered atlases (e.g., `items.png`).

Key files:
- `core/.../sprites/ItemSpriteSheet.java` — defines `RINGED_OFFSET = 1000` and `getFilm()` / `getRingedIndex()` helpers.
- `core/.../ui/HeroIcon.java` — same pattern for class icons.
- `core/.../ui/TalentIcon.java` — same pattern for talent icons.

**Rule for agents**: When adding new custom items, icons, or talents, assign them an index `>= RINGED_OFFSET` so future upstream merges do not collide. See `CLAUDE.md` for the full step-by-step workflow.

---

## Testing Strategy

**There are currently no automated tests in this project.** (`0` test files found across all modules.)

Validation is entirely manual:
1. Build and run the Desktop debug target (`desktop:debug`).
2. Play through affected levels/items/scenes.
3. Test Android builds on a device or emulator before release.

If you introduce automated tests, place them under `src/test/java` within the relevant module and ensure they use the same Java 8 compatibility.

---

## Assets

Game assets live in `core/src/main/assets/`:

| Directory | Contents |
|-----------|----------|
| `sprites` | Character and item sprite sheets |
| `interfaces` | UI textures, banners, icons (including `_ringed` variants) |
| `environment` | Tilesets, walls, floors, decorations |
| `effects` | Spell effects, particles |
| `messages` | `.properties` localization files (e.g., `actors_zh.properties`) |
| `music` | OGG background music |
| `sounds` | OGG sound effects |
| `fonts` | Bitmap fonts |
| `splashes` | Title-screen splashes |
| `gdx` | libGDX-specific data (shaders, etc.) |

Desktop and Android modules reference these assets from `core/src/main/assets` via their Gradle `sourceSets` or `processResources` configuration.

---

## Localization

Text is stored in `.properties` files under `core/src/main/assets/messages/`.
- The game supports many languages; keys are shared across locales.
- Chinese-specific files end with `_zh.properties`.
- Adding new user-facing strings requires:
  1. Adding the key/value pair to the relevant `.properties` file.
  2. Referencing the key via `Messages.get(Class, "key")` in code.

---

## Deployment / Release Process

### Desktop

1. Update `appVersionCode` and `appVersionName` in root `build.gradle`.
2. Run `./gradlew desktop:jpackageImage`.
3. Distribute the output from `desktop/build/jpackage/`.

### Android

1. Update version fields in root `build.gradle`.
2. Ensure keystore is present at `android/key/key.jks`.
3. Set `PASSWORD` environment variable to keystore password.
4. Run `./gradlew android:assembleRelease`.
5. APK output is in `android/build/outputs/apk/release/`.

On Windows, `build.bat` automates keystore generation, encryption with 7-Zip, and release building.

---

## Security Considerations

- **Keystore**: The Android signing keystore (`key.jks`) is **not** committed to Git. A zip-encrypted copy may exist locally in `android/key/key.zip`.
- **Passwords**: Build scripts read the keystore password from the `PASSWORD` environment variable. Do not hardcode passwords.
- **R8 / ProGuard**: Release Android builds enable `minifyEnabled` and `shrinkResources`. ProGuard rules are in `android/proguard-rules.pro`.
- **android.enableR8.fullMode=false**: This is intentionally disabled because certain R8 optimizations crash Shattered Pixel Dungeon at runtime.
- **Native libraries**: Android `.so` files are extracted from libGDX native JARs at build time via `copyAndroidNatives`. Do not commit extracted `.so` files.

---

## Useful Reference

- `CLAUDE.md` — Detailed Chinese-language architecture guide, including the 1000+ offset resource system, Actor/Buff/Level system deep dives, and debugging tips.
- `docs/getting-started-desktop.md` — Desktop build setup.
- `docs/getting-started-android.md` — Android build setup (includes Google Play distribution notes).
- `docs/getting-started-ios.md` — iOS build setup (module currently inactive).
- `docs/recommended-changes.md` — Checklist for forking the project (renaming, icons, credits).

---

## Quick Checklist for Agents

Before submitting changes:

- [ ] Preserve GPL v3 copyright headers on new/modified files.
- [ ] Use tabs for indentation, K&R braces.
- [ ] If adding a custom sprite/icon, use the `1000+` offset mechanism.
- [ ] If adding persistent state, handle missing keys for backward compatibility.
- [ ] Ensure `desktop:debug` runs without crashes.
- [ ] Verify UTF-8 encoding if editing `.properties` localization files.
- [ ] Do not commit `android/key/key.jks` or other secrets.
