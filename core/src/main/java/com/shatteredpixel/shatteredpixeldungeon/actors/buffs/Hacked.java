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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

// 骇入：每一层削减敌人 1 点生命上限（对 BOSS 无效），无限叠加且不会衰减。
// 敌人被转化为友军时清除（见 AllyBuff.attachTo）。
// Swarm 分裂子体 / 亡灵复活会继承层数（revivePersists + 手动复制层数）。
public class Hacked extends Buff {

    public int layers = 1;

    // 是否已削减过生命上限（存档恢复走 attachTo，不能重复削减）
    private boolean htApplied = false;

    // 静默模式：不触发溢出伤害/死亡判定/状态显示（用于 Swarm 分裂、Ghoul 复活等子体继承场景，此时子体可能尚未加入场景）
    public boolean silent = false;

    {
        type = buffType.NEGATIVE;
        announced = true;
        revivePersists = true;
    }

    // 增加层数（主动骇入/协同骇入均走这里）
    public void addLayers(int n) {
        if (n <= 0) return;
        layers += n;
        applyHack(n, false);
        if (target != null && target.isAlive()) {
            target.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Messages.get(this, "hacked", layers), FloatingText.CORRUPTION);
        }
    }

    // 本次骇入 n 层的统一结算：先计算可能产生的溢出生命值并以 buff 为来源造成伤害
    // （走正常死亡链路，图标/掉落/动画正常销毁），然后再削减生命上限。
    // withEffects=false 时（静默模式/存档恢复）跳过伤害与死亡判定，只削减上限。
    private void applyHack(int n, boolean silent) {
        if (target == null) return;
        htApplied = true;
        if (!target.properties().contains(Char.Property.BOSS)) {
            if (!silent) {
                int newHT = target.HT - n;
                int overflow = target.HP - newHT;
                if (overflow > 0) {
                    target.damage(overflow, this);
                }
            }
            applyHTLoss(n);
        }
        if (!silent && target.HT <= 0 && target.isAlive()) {
            target.die(this);
        }
    }

    // 生命上限削减（对 BOSS 无效）
    public void applyHTLoss(int n) {
        if (target.properties().contains(Char.Property.BOSS)) {
            return;
        }
        target.HT -= n;
        if (target.HP > target.HT) {
            target.HP = target.HT;
        }
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            // 友军与英雄不受骇入影响
            if (target.alignment == Char.Alignment.ENEMY) {
                if (!htApplied) {
                    applyHack(layers, silent);
                } else if (!silent && target.HT <= 0 && target.isAlive()) {
                    target.die(this);
                }
                return true;
            } else {
                detach();
                return false;
            }
        }
        return false;
    }

    @Override
    public void detach() {
        if (target != null) {
            // BOSS 未削减过生命上限，还原时不补偿
            if (!target.properties().contains(Char.Property.BOSS)) {
                target.HT += layers;
                if (target.HP > target.HT) {
                    target.HP = target.HT;
                }
            }
        }
        super.detach();
    }

    // 每回合：内核爆破（木马大师）+ 花式入侵（木马大师）
    @Override
    public boolean act() {
        Hero hero = Dungeon.hero;
        if (hero != null && target.isAlive() && target.alignment == Char.Alignment.ENEMY) {

            // 内核爆破：每回合伤害 = log_base(层数) 向上取整 + 1，底数由天赋决定
            if (hero.hasTalent(Talent.KERNEL_BREACH)) {
                int base = 5 - (hero.pointsInTalent(Talent.KERNEL_BREACH) - 1); // 5/4/3
                int dmg;
                if (layers <= 1) {
                    dmg = 1;
                } else {
                    dmg = (int) Math.ceil(Math.log(layers) / Math.log(base)) + 1;
                }
                target.damage(dmg, this);
                if (target.isAlive()) {
                    target.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(dmg), FloatingText.CORROSION);
                }
            }

            // 花式入侵：每回合 0.1%/0.2%/0.3% * 层数 的几率获得随机 debuff（上限 33%/66%/99%），与 0 级腐化法杖一致
            if (target.isAlive() && hero.hasTalent(Talent.FANCY_INVASION)) {
                float chance = 0.001f * hero.pointsInTalent(Talent.FANCY_INVASION) * layers;
                float maxChance = 0.33f * hero.pointsInTalent(Talent.FANCY_INVASION);
                if (chance > maxChance) chance = maxChance;
                if (Random.Float() < chance) {
                    Class<? extends FlavourBuff> debuffCls = WandOfCorruption.randomMinorDebuff(target);
                    if (debuffCls != null) {
                        Buff.append(target, debuffCls, 6); // 6 = 0级腐化法杖的 debuff 时长（6 + buffedLvl*3）
                    }
                }
            }
        }
        spend(TICK);
        return true;
    }

    @Override
    public int icon() {
        return BuffIndicator.HACKED;
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(layers);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", layers);
    }

    private static final String LAYERS = "layers";
    private static final String HT_APPLIED = "ht_applied";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LAYERS, layers);
        bundle.put(HT_APPLIED, htApplied);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        layers = bundle.getInt(LAYERS);
        if (bundle.contains(HT_APPLIED)) {
            htApplied = bundle.getBoolean(HT_APPLIED);
        }
    }
}
