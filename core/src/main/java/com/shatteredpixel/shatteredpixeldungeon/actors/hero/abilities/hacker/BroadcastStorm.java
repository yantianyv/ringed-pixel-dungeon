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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.hacker;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.PortableTerminal;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

// 广播风暴：骇客护甲技能。
// 消耗 50% 护甲充能构建网络回路，对视野内所有敌人累计发起多次攻击，
// 每次攻击造成 0~n 点伤害（n = 视野内敌人数量）并触发协同骇入。
// 首次攻击在 2 回合后，之后每次攻击间隔缩短至当前的 90%（天赋可提升）。
public class BroadcastStorm extends ArmorAbility {

    {
        baseChargeUse = 50; // 50% 充能
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        armor.charge -= chargeUse(hero);

        int attacks = 5;
        if (hero.hasTalent(Talent.LONG_LASTING)) {
            attacks += 2 * hero.pointsInTalent(Talent.LONG_LASTING); // 7/9/11/13
        }

        Buff.affect(hero, Storm.class).set(attacks);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        hero.sprite.operate(hero.pos);
    }

    @Override
    public int icon() {
        return HeroIcon.BROADCAST_STORM;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.LONG_LASTING, Talent.FULL_OPTIC_FIBER, Talent.SIGNAL_AMPLIFICATION, Talent.HEROIC_ENERGY};
    }

    // 管理多次延迟攻击的 buff
    public static class Storm extends Buff {

        private int attacksLeft = 0;
        private float interval = 2f; // 首次攻击前的等待回合
        private float shrinkFactor = 0.9f; // 每次攻击后间隔缩短系数

        {
            type = buffType.POSITIVE;
        }

        public void set(int attacks) {
            this.attacksLeft = attacks;
            // 全牢光纤：间隔缩短系数 80%/70%/60%/50%
            if (Dungeon.hero.hasTalent(Talent.FULL_OPTIC_FIBER)) {
                shrinkFactor = 1.0f - 0.1f * (Dungeon.hero.pointsInTalent(Talent.FULL_OPTIC_FIBER) + 1);
            }
            spend(interval);
        }

        @Override
        public boolean act() {
            Hero hero = Dungeon.hero;
            if (hero == null || attacksLeft <= 0) {
                detach();
                return true;
            }

            attacksLeft--;

            // 数当前视野内敌人数量作为 n
            int n = 0;
            ArrayList<Char> enemies = new ArrayList<>();
            for (Char ch : Actor.chars()) {
                if (ch.alignment == Char.Alignment.ENEMY
                        && ch.isAlive()
                        && Dungeon.level.heroFOV[ch.pos]) {
                    n++;
                    enemies.add(ch);
                }
            }

            // 信号增幅：伤害 +50%/+100%/+150%/+200%
            float dmgMulti = 1f;
            if (hero.hasTalent(Talent.SIGNAL_AMPLIFICATION)) {
                dmgMulti = 1f + 0.5f * hero.pointsInTalent(Talent.SIGNAL_AMPLIFICATION);
            }

            for (Char enemy : enemies) {
                int dmg = (int) Math.round(Random.IntRange(0, n) * dmgMulti);
                if (dmg > 0) {
                    enemy.damage(dmg, this);
                    enemy.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(dmg), FloatingText.SHOCKING);
                }
                // 触发协同骇入
                PortableTerminal.hackTarget(hero, enemy, PortableTerminal.coopHackLayers(hero));
                // 电火花特效
                CellEmitter.get(enemy.pos).burst(SparkParticle.FACTORY, 3 + n);
            }

            // 间隔缩短
            interval *= shrinkFactor;
            if (interval < 0.1f) interval = 0.1f;
            if (attacksLeft > 0) {
                spend(interval);
            } else {
                detach();
            }
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.OVERCLOCK;
        }

        @Override
        public String toString() {
            return Messages.get(BroadcastStorm.class, "storm_name");
        }

        @Override
        public String desc() {
            return Messages.get(BroadcastStorm.class, "storm_desc", attacksLeft);
        }

        private static final String ATTACKS = "attacks";
        private static final String INTERVAL = "interval";
        private static final String SHRINK = "shrink";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ATTACKS, attacksLeft);
            bundle.put(INTERVAL, interval);
            bundle.put(SHRINK, shrinkFactor);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            attacksLeft = bundle.getInt(ATTACKS);
            interval = bundle.getFloat(INTERVAL);
            shrinkFactor = bundle.getFloat(SHRINK);
        }
    }
}
