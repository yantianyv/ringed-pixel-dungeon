/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.effects.CircleArc;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.ColorMath;
import com.watabou.utils.GameMath;

public class StatusPane extends Component {

    private NinePatch bg;
    private Image avatar;
    private Button heroInfo;
    public static float talentBlink;
    private float warning;

    public static final float FLASH_RATE = (float) (Math.PI * 1.5f); //1.5 blinks per second

    private int lastTier = 0;

    private Image rawShielding;
    private Image shieldedHP;
    private Image hp;
    private BitmapText hpText;
    private Button heroInfoOnBar;

    private Image exp;
    private BitmapText expText;

    private int lastLvl = -1;

    private BitmapText level;

    private BuffIndicator buffs;
    private Compass compass;

    private BusyIndicator busy;
    private CircleArc counter;

    private boolean large;

    // Hunger display
    private Image hunger;
    private Image excessHunger;
    private BitmapText hungerText;

    public StatusPane(boolean large) {
        super();

        String asset = Assets.Interfaces.STATUS;

        this.large = large;

        if (large) {
            bg = new NinePatch(asset, 0, 64, 41, 39, 33, 0, 4, 0);
        } else {
            bg = new NinePatch(asset, 0, 0, 128, 36, 114, 0, 0, 0);
        }
        add(bg);

        heroInfo = new Button() {
            @Override
            protected void onClick() {
                Camera.main.panTo(Dungeon.hero.sprite.center(), 5f);
                GameScene.show(new WndHero());
            }

            @Override
            public GameAction keyAction() {
                return SPDAction.HERO_INFO;
            }

            @Override
            protected String hoverText() {
                return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info"));
            }
        };
        add(heroInfo);

        avatar = HeroSprite.avatar(Dungeon.hero);
        add(avatar);

        talentBlink = 0;

        compass = new Compass(Statistics.amuletObtained ? Dungeon.level.entrance() : Dungeon.level.exit());
        add(compass);

        // Initialize hunger display (right of HP bar)
        if (large) {
            hunger = new Image(asset, 0, 112, 64, 9);
            excessHunger = new Image(asset, 0, 112, 64, 9);
        } else {
            hunger = new Image(asset, 0, 36, 25, 4);
            excessHunger = new Image(asset, 0, 40, 25, 4);
        }
        hunger.color(0xC68749); // Orange
        excessHunger.color(0x00FF00);
        add(hunger);
        add(excessHunger);

        hungerText = new BitmapText(PixelScene.pixelFont);
        hungerText.alpha(0.3f);
        add(hungerText);

        if (large) {
            rawShielding = new Image(asset, 0, 112, 128, 9);
        } else {
            rawShielding = new Image(asset, 0, 40, 50, 4);
        }
        rawShielding.alpha(0.5f);
        add(rawShielding);

        if (large) {
            shieldedHP = new Image(asset, 0, 112, 128, 9);
        } else {
            shieldedHP = new Image(asset, 0, 40, 50, 4);
        }
        add(shieldedHP);

        if (large) {
            hp = new Image(asset, 0, 103, 128, 9);
        } else {
            hp = new Image(asset, 0, 36, 50, 4);
        }
        add(hp);

        hpText = new BitmapText(PixelScene.pixelFont);
        hpText.alpha(0.6f);
        add(hpText);

        heroInfoOnBar = new Button() {
            @Override
            protected void onClick() {
                Camera.main.panTo(Dungeon.hero.sprite.center(), 5f);
                GameScene.show(new WndHero());
            }
        };
        add(heroInfoOnBar);

        if (large) {
            exp = new Image(asset, 0, 121, 128, 7);
        } else {
            exp = new Image(asset, 0, 44, 16, 1);
        }
        add(exp);

        if (large) {
            expText = new BitmapText(PixelScene.pixelFont);
            expText.hardlight(0xFFFFAA);
            expText.alpha(0.6f);
            add(expText);
        }

        level = new BitmapText(PixelScene.pixelFont);
        level.hardlight(0xFFFFAA);
        add(level);

        buffs = new BuffIndicator(Dungeon.hero, large);
        add(buffs);

        busy = new BusyIndicator();
        add(busy);

        counter = new CircleArc(18, 4.25f);
        counter.color(0x808080, true);
        counter.show(this, busy.center(), 0f);
    }

    @Override
    protected void layout() {
        height = large ? 39 : 32;

        bg.x = x;
        bg.y = y;
        if (large) {
            bg.size(160 + 64, bg.height); // Width for combined bars
        } else {
            bg.size(width + 25, bg.height);
        }

        avatar.x = bg.x - avatar.width / 2f + 15;
        avatar.y = bg.y - avatar.height / 2f + (large ? 15 : 16);
        PixelScene.align(avatar);

        heroInfo.setRect(x, y + (large ? 0 : 1), 30, large ? 40 : 30);

        compass.x = avatar.x + avatar.width / 2f - compass.origin.x;
        compass.y = avatar.y + avatar.height / 2f - compass.origin.y;
        PixelScene.align(compass);

        if (large) {
            // HP bar position
            hp.x = shieldedHP.x = rawShielding.x = x + 30;
            hp.y = shieldedHP.y = rawShielding.y = y + 19;

            // Hunger bar positioned immediately right of HP bar with no gap
            hunger.x = excessHunger.x = hp.x + hp.width;
            hunger.y = excessHunger.y = hp.y;

            hungerText.x = hunger.x;
            hungerText.y = hunger.y + 1;
            PixelScene.align(hungerText);

            hpText.x = hp.x + (128 - hpText.width()) / 2f;
            hpText.y = hp.y + 1;
            PixelScene.align(hpText);

            exp.x = x + 30;
            exp.y = y + 30;

            expText.x = exp.x + (192 - expText.width()) / 2f;
            expText.y = exp.y;
            PixelScene.align(expText);

            heroInfoOnBar.setRect(heroInfo.right(), y + 19, 130, 20);

            buffs.setRect(x + 31, y, 128, 16);

            busy.x = x + bg.width + 1;
            busy.y = y + bg.height - 9;
        } else {
            // Small layout remains unchanged
            hp.x = shieldedHP.x = rawShielding.x = x + 30;
            hp.y = shieldedHP.y = rawShielding.y = y + 3;

            hunger.x = excessHunger.x = hp.x + hp.width + 2;
            hunger.y = excessHunger.y = hp.y;

            hungerText.scale.set(PixelScene.align(0.5f));
            hungerText.x = hunger.x + 1;
            hungerText.y = hunger.y + (hunger.height - (hungerText.baseLine() + hungerText.scale.y)) / 2f;
            hungerText.y -= 0.001f;
            PixelScene.align(hungerText);

            hpText.scale.set(PixelScene.align(0.5f));
            hpText.x = hp.x + 1;
            hpText.y = hp.y + (hp.height - (hpText.baseLine() + hpText.scale.y)) / 2f;
            hpText.y -= 0.001f;
            PixelScene.align(hpText);

            exp.x = x;
            exp.y = y;

            heroInfoOnBar.setRect(heroInfo.right(), y, 50, 9);

            buffs.setRect(x + 31, y + 9, 50, 8);

            busy.x = x + 1;
            busy.y = y + 33;
        }

        level.text(Integer.toString(Dungeon.hero.lvl));
        level.measure();
        if (large) {
            level.x = x + (30f - level.width()) / 2f;
            level.y = y + 33f - level.baseLine() / 2f;
        } else {
            level.x = x + 27.5f - level.width() / 2f;
            level.y = y + 28.0f - level.baseLine() / 2f;
        }
        PixelScene.align(level);

        counter.point(busy.center());
    }

    private static final int[] warningColors = new int[]{0x660000, 0xCC0000, 0x660000};

    private int oldHP = 0;
    private int oldShield = 0;
    private int oldMax = 0;

    @Override
    public void update() {
        super.update();

        int health = Dungeon.hero.HP;
        int shield = Dungeon.hero.shielding();
        int max = Dungeon.hero.HT;

		if (!Dungeon.hero.isAlive()) {
			avatar.tint(0x000000, 0.5f);
		} else if ((health/(float)max) < 0.334f) {
			warning += Game.elapsed * 5f *(0.4f - (health/(float)max));
			warning %= 1f;
			avatar.tint(ColorMath.interpolate(warning, warningColors), 0.5f );
		} else if (talentBlink > 0.33f){ //stops early so it doesn't end in the middle of a blink
			talentBlink -= Game.elapsed;
			avatar.tint(1, 1, 0, (float)Math.abs(Math.cos(talentBlink*FLASH_RATE))/2f);
		} else {
			avatar.resetColor();
		}

        hp.scale.x = Math.max(0, (health - shield) / (float) max);
        shieldedHP.scale.x = health / (float) max;

        if (shield > health) {
            rawShielding.scale.x = Math.min(1, shield / (float) max);
        } else {
            rawShielding.scale.x = 0;
        }

        if (oldHP != health || oldShield != shield || oldMax != max) {
            if (shield <= 0) {
                hpText.text(health + "/" + max);
            } else {
                hpText.text(health + "+" + shield + "/" + max);
            }
            oldHP = health;
            oldShield = shield;
            oldMax = max;
        }

        // Update hunger display
        if (Dungeon.hero.buff(Hunger.class) != null) {
            float hungerVal = Dungeon.hero.buff(Hunger.class).full();
            float maxHunger = Hunger.STARVING;

            if (hungerVal <= maxHunger) {
                hunger.scale.x = hungerVal / maxHunger;
                excessHunger.scale.x = 0;
            } else {
                hunger.scale.x = 1;
                float excess = (float) (Math.log(1 + (hungerVal - maxHunger)) / Math.log(1000) / 10);
                excessHunger.scale.x = Math.min(1, excess);
            }
            String hungerStr = String.valueOf((int) hungerVal);
            if (hungerStr.length() > 7) {
                hungerText.text(String.format("%.2e", hungerVal));
            } else {
                hungerText.text(hungerStr);
            }
            hungerText.measure();
            if (large) {
                hungerText.x = hunger.x + (64 / 2 - Math.round(hungerText.width() / 4) * 2);
            } else {
                hungerText.x = hunger.x + (25 / 2 - Math.round(hungerText.width() / 4) * 2);
            }
        } else {
            hunger.scale.x = 0;
            excessHunger.scale.x = 0;
            hungerText.text("null");
        }

        if (large) {
            exp.scale.x = (192 / exp.width) * Dungeon.hero.exp / Dungeon.hero.maxExp();

            hpText.measure();
            hpText.x = hp.x + (192 - hpText.width()) / 2f;

            expText.text(Dungeon.hero.exp + "/" + Dungeon.hero.maxExp());
            expText.measure();
            expText.x = exp.x + (192 - expText.width()) / 2f;
        } else {
            // exp.scale.x = (width / 100) * Dungeon.hero.exp / Dungeon.hero.maxExp();
            // exp.scale.x = exp.width / 2;
            exp.scale.x = (width / exp.width) * Dungeon.hero.exp / Dungeon.hero.maxExp();
        }

        if (Dungeon.hero.lvl != lastLvl) {
            if (lastLvl != -1) {
                showStarParticles();
            }

            lastLvl = Dungeon.hero.lvl;

            if (large) {
                level.text("lv. " + lastLvl);
            } else {
                level.text(Integer.toString(lastLvl));
            }
            level.measure();
            if (large) {
                level.x = x + (30f - level.width()) / 2f;
                level.y = y + 33f - level.baseLine() / 2f;
            } else {
                level.x = x + 27.5f - level.width() / 2f;
                level.y = y + 28.0f - level.baseLine() / 2f;
            }
            PixelScene.align(level);
        }

        int tier = Dungeon.hero.tier();
        if (tier != lastTier) {
            lastTier = tier;
            avatar.copy(HeroSprite.avatar(Dungeon.hero));
        }

        counter.setSweep((1f - Actor.now() % 1f) % 1f);
    }

    public void updateAvatar() {
        avatar.copy(HeroSprite.avatar(Dungeon.hero));
    }

    public void alpha(float value) {
        value = GameMath.gate(0, value, 1f);
        bg.alpha(value);
        avatar.alpha(value);
        rawShielding.alpha(0.5f * value);
        shieldedHP.alpha(value);
        hp.alpha(value);
        hpText.alpha(0.6f * value);
        hunger.alpha(value);
        excessHunger.alpha(value);
        hungerText.alpha(0.6f * value);
        exp.alpha(value);
        if (expText != null) {
            expText.alpha(0.6f * value);
        }
        level.alpha(value);
        compass.alpha(value);
        busy.alpha(value);
        counter.alpha(value);
    }

    public void showStarParticles() {
        Emitter emitter = (Emitter) recycle(Emitter.class);
        emitter.revive();
        emitter.pos(avatar.center());
        emitter.burst(Speck.factory(Speck.STAR), 12);
    }
}
