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
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff.Element;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;

// 焰浪法杖
public class WandOfFireblast extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_FIREBOLT;

        // 仅用于瞄准，实际抛射逻辑是 Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID
        collisionProperties = Ballistica.WONT_STOP;
    }

    // 1/2/3 基础伤害，根据使用的充能数进行 1/2/3 的缩放
    public int min(int lvl) {
        return (1 + lvl) * chargesPerCast();
    }

    // 2/8/18 基础伤害，根据使用的充能数进行 2/4/6 的缩放
    public int max(int lvl) {
        switch (chargesPerCast()) {
            case 1:
            default:
                return 2 + 2 * lvl;
            case 2:
                return 2 * (4 + 2 * lvl);
            case 3:
                return 3 * (6 + 2 * lvl);
        }
    }

    ConeAOE cone;
    int target;
    @Override
    public void onZap(Ballistica bolt) {

        // 创建一个列表，用于存储受影响的角色
        ArrayList<Char> affectedChars = new ArrayList<>();
        // 遍历锥形区域中的每一个单元格
        for (int cell : cone.cells) {

            // 忽略施法者所在的单元格
            if (cell == bolt.sourcePos) {
                continue;
            }

            // 打开门
            if (Dungeon.level.map[cell] == Terrain.DOOR) {
                Level.set(cell, Terrain.OPEN_DOOR);
                GameScene.updateMap(cell);
            }

            // 如果该单元格上有角色，将其添加到受影响角色列表中
            Char ch = Actor.findChar(cell);
            if (ch != null) {
                affectedChars.add(ch);
            }
        }

        // 遍历受影响的角色列表
        for (Char ch : affectedChars) {
            // 处理角色受到的魔法效果
            wandProc(ch, chargesPerCast());
            // 计算角色的伤害值
            int dmg = (int) (damageRoll() * ElementBuff.apply(Element.PYRO, ch, ch, 1f + 0.5f * level()));
            // 对角色造成伤害
            ch.damage(dmg, this);
            // 如果角色存活，根据法杖的充能数施加不同的效果
            if (ch.isAlive()) {
                // Buff.affect(ch, Burning.class).reignite(ch);
                switch (chargesPerCast()) {
                    case 1:
                        break; // 无效果
                    case 2:
                        Buff.affect(ch, Cripple.class, 4f);
                        break;
                    case 3:
                        Buff.affect(ch, Paralysis.class, 4f);
                        break;
                }
            }
        }
        // 如果target在视野中则点燃这个格子
        if (Dungeon.level.heroFOV[target]) {
            // 点燃强度为消耗充能数+等级的平方根
            GameScene.add(Blob.seed(target, chargesPerCast(), Fire.class));
            // 如果这一格有怪物，那就给怪物也挂上燃烧buff
            Char ch = Actor.findChar(target);
            if (ch != null) {
                Buff.affect(ch, Burning.class).reignite(ch);
            }
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        // 行为类似于炽热附魔
        new FireBlastOnHit().proc(staff, attacker, defender, damage);
    }

    // 定义一个名为FireBlastOnHit的内部类，继承自Blazing类
    public static class FireBlastOnHit extends Blazing {

        // 重写procChanceMultiplier方法，返回Wand类的procChanceMultiplier方法的返回值
        @Override
        protected float procChanceMultiplier(Char attacker) {
            return Wand.procChanceMultiplier(attacker);
        }
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        // 需要在这里执行火焰扩散逻辑，以确定在哪些单元格中放置火焰。

        // 5/7/9 距离
        int maxDist = 3 + 2 * chargesPerCast();

        cone = new ConeAOE(bolt,
                maxDist,
                30 + 20 * chargesPerCast(),
                Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

        target = bolt.target;

        // 仅对尖端单元格进行施法，而不是所有单元格，以获得更好的性能。
        Ballistica longestRay = null;
        for (Ballistica ray : cone.outerRays) {
            if (longestRay == null || ray.dist > longestRay.dist) {
                longestRay = ray;
            }
            ((MagicMissile) curUser.sprite.parent.recycle(MagicMissile.class)).reset(
                    MagicMissile.FIRE_CONE,
                    curUser.sprite,
                    ray.path.get(ray.dist),
                    null
            );
        }

        // 在最长射线的半距离处进行最终施法，以确定实际法杖效果的时间
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.FIRE_CONE,
                curUser.sprite,
                longestRay.path.get(longestRay.dist / 2),
                callback);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
    }

    @Override
    protected int chargesPerCast() {
        if (cursed
                || (charger != null && charger.target != null && charger.target.buff(WildMagic.WildMagicTracker.class) != null)) {
            return 1;
        }
        // 消耗当前充能的 30%，向上取整，最少为 1，最多为 3。
        return (int) GameMath.gate(1, (int) Math.ceil(curCharges * 0.3f), 3);
    }

    @Override
    public String statsDesc() {
        if (levelKnown) {
            return Messages.get(this, "stats_desc", chargesPerCast(), min(), max());
        } else {
            return Messages.get(this, "stats_desc", chargesPerCast(), min(0), max(0));
        }
    }

    @Override
    public String upgradeStat1(int level) {
        return (1 + level) + "-" + (2 + 2 * level);
    }

    @Override
    public String upgradeStat2(int level) {
        return (2 + 2 * level) + "-" + 2 * (4 + 2 * level);
    }

    @Override
    public String upgradeStat3(int level) {
        return (3 + 3 * level) + "-" + 3 * (6 + 2 * level);
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0xEE7722);
        particle.am = 0.5f;
        particle.setLifespan(0.6f);
        particle.acc.set(0, -40);
        particle.setSize(0f, 3f);
        particle.shuffleXY(1.5f);
    }

}


