/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
* 
 * Ringed Pixel Dungeon
 * Copyright (C) 2025-2025 yantianyv
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
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTakeout;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfHunger;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Hunger extends Buff implements Hero.Doom {

    public static final float HUNGRY = 300f;
    public static final float STARVING = 450f;

    private float level;
    private float partialDamage;

    private static final String LEVEL = "level";
    private static final String PARTIALDAMAGE = "partialDamage";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
        bundle.put(PARTIALDAMAGE, partialDamage);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getFloat(LEVEL);
        partialDamage = bundle.getFloat(PARTIALDAMAGE);
    }

    @Override
    public boolean act() {

        // 触发吃得好buff
        if (Dungeon.level.locked
                || target.buff(WellFed.class) != null
                || SPDSettings.intro()
                || target.buff(ScrollOfChallenge.ChallengeArena.class) != null) {
            spend(TICK);
            return true;
        }

        if (target.isAlive() && target instanceof Hero) {

            Hero hero = (Hero) target;
            // 触发大胃王饥饿结算
            if (hero.subClass == HeroSubClass.MUKBANGER && full() >= 3000) {
                float x = full() / 1000;
                level += x;
                hero.HP += Math.log(1 + x) / Math.log(2);
                hero.updateHT(false);
            }// 触发极度饥饿结算 
            else if (isStarving()) {
                partialDamage += target.HT / 1000f;
                if (partialDamage > 1) {
                    target.damage((int) partialDamage, this);
                    partialDamage -= (int) partialDamage;
                }
            }// 触发普通饥饿结算
            else {
                float hungerDelay = 1f;
                if (target.buff(Shadows.class) != null) {
                    hungerDelay *= 1.5f;
                }
                hungerDelay /= SaltCube.hungerGainMultiplier();

                float newLevel = level + (1f / hungerDelay);
                if (newLevel >= STARVING) {

                    GLog.n(Messages.get(this, "onstarving"));
                    // 如果装备了膨胀神券之戒，则免去进入饥饿时的伤害
                    if (hero.buff(RingOfTakeout.Takeout.class) == null) {
                        hero.damage(1, this);
                    }

                    hero.interrupt();
                    newLevel = STARVING;

                } else if (newLevel >= HUNGRY && level < HUNGRY) {

                    GLog.w(Messages.get(this, "onhungry"));

                    if (!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_FOOD)) {
                        GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_FOOD);
                    }

                }
                level = newLevel;

            }

            spend(TICK);

        } else {

            diactivate();

        }

        return true;
    }

    // 满足方法，传入能量参数
    public void satisfy(float energy) {
        // 调用影响饥饿方法，传入能量参数和布尔值false
        affectHunger(energy, false);
    }

    public void affectHunger(float energy) {
        affectHunger(energy, false);
    }

    public void setHunger(float level){
        this.level = level;
    }

    public float full() {
        if (Dungeon.hero != null) {
            return STARVING - level;
        } else {
            return 0;
        }
    }

    // affectHunger函数用于影响目标的饥饿值
    public void affectHunger(float energy, boolean overrideLimits) {

        // 如果energy小于0且目标有WellFed类buff，则将buff的left属性加上energy
        if (energy < 0 && target.buff(WellFed.class) != null) {
            target.buff(WellFed.class).left += energy;
            BuffIndicator.refreshHero();
            return;
        }

        // 保存旧等级
        float oldLevel = level;

        // 减少等级
        level -= energy;
        // 如果等级小于-10e30f，则将其设为-10e30f
        level = level < -10e30f ? -10e30f : level;

        // 获取最大饥饿值
        float maxfull = ShardOfHunger.extraHunger();
        // 如果目标选择了Mukbanger子类，则跳过以下代码
        if (Dungeon.hero.subClass == HeroSubClass.MUKBANGER) {
            // pass
        // 如果等级小于最大饥饿值且不覆盖限制，则将等级设为最大饥饿值
        } else if (level < -maxfull && !overrideLimits) {
            level = -ShardOfHunger.extraHunger();
        // 如果等级大于STARVING，则计算超出部分并造成伤害
        } else if (level > STARVING) {
            float excess = level - STARVING;
            level = STARVING;
            partialDamage += excess * (target.HT / 1000f);
            if (partialDamage > 1f) {
                target.damage((int) partialDamage, this);
                partialDamage -= (int) partialDamage;
            }
        }

        // 如果旧等级小于HUNGRY且当前等级大于等于HUNGRY，则显示警告信息
        if (oldLevel < HUNGRY && level >= HUNGRY) {
            GLog.w(Messages.get(this, "onhungry"));
        // 如果旧等级小于STARVING且当前等级大于等于STARVING，则显示警告信息并造成伤害
        } else if (oldLevel < STARVING && level >= STARVING) {
            GLog.n(Messages.get(this, "onstarving"));
            target.damage(1, this);
        }

        // 刷新Buff指示器
        BuffIndicator.refreshHero();
    }

    public boolean isStarving() {
        return level >= STARVING;
    }

    public int hunger() {
        return (int) Math.ceil(level);
    }

    @Override
    public int icon() {
        if (level < HUNGRY) {
            return BuffIndicator.NONE;
        } else if (level < STARVING) {
            return BuffIndicator.HUNGER;
        } else {
            return BuffIndicator.STARVATION;
        }
    }

    @Override
    public String name() {
        if (level < STARVING) {
            return Messages.get(this, "hungry");
        } else {
            return Messages.get(this, "starving");
        }
    }

    @Override
    public String desc() {
        String result;
        if (level < STARVING) {
            result = Messages.get(this, "desc_intro_hungry");
        } else {
            result = Messages.get(this, "desc_intro_starving");
        }

        result += Messages.get(this, "desc");

        return result;
    }

    @Override
    public void onDeath() {

        Badges.validateDeathFromHunger();

        Dungeon.fail(this);
        GLog.n(Messages.get(this, "ondeath"));
    }
}
