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
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAgility;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MirrorSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class MirrorImage extends NPC {

    {
        spriteClass = MirrorSprite.class;

        HP = HT = 1;
        defenseSkill = 1;

        alignment = Alignment.ALLY;
        state = HUNTING;

        //before other mobs
        actPriority = MOB_PRIO + 1;
    }

    private Hero hero;
    private int heroID;
    public int armTier;
    public boolean swapped = false; // 是否已经换位过（三仙归洞）

    @Override
    protected boolean act() {

        if (hero == null) {
            hero = (Hero) Actor.findById(heroID);
            if (hero == null) {
                die(null);
                sprite.killAndErase();
                return true;
            }
        }

        if (hero.tier() != armTier) {
            armTier = hero.tier();
            ((MirrorSprite) sprite).updateArmor(armTier);
        }

        // 三仙归洞：可换位镜像显示粒子效果
        if (hero.subClass == HeroSubClass.MAGICIAN && hero.hasTalent(Talent.THREE_IMMORTALS)) {
            boolean canSwap = !swapped 
                    && Dungeon.level.heroFOV[pos]
                    && this.canInteract(hero);
            
            Talent.SwapReady swapBuff = buff(Talent.SwapReady.class);
            if (canSwap && swapBuff == null) {
                Buff.affect(this, Talent.SwapReady.class);
            } else if (!canSwap && swapBuff != null) {
                swapBuff.detach();
            }
        }

        return super.act();
    }

    private static final String HEROID = "hero_id";
    private static final String SWAPPED = "swapped";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEROID, heroID);
        bundle.put(SWAPPED, swapped);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        heroID = bundle.getInt(HEROID);
        swapped = bundle.getBoolean(SWAPPED);
    }

    public void duplicate(Hero hero) {
        duplicate(hero, true);
    }

    public void duplicate(Hero hero, boolean invisible) {
        this.hero = hero;
        heroID = this.hero.id();
        if (invisible) {
            Buff.affect(this, MirrorInvis.class, Short.MAX_VALUE);
        }
        
        // 以假乱真：镜像继承魔术师部分生命值
        if (hero.subClass == HeroSubClass.MAGICIAN && hero.hasTalent(Talent.FAKE_REALITY)) {
            int points = hero.pointsInTalent(Talent.FAKE_REALITY);
            float hpPercent = 0.05f * points;
            HT = Math.max(1, (int)(hero.HT * hpPercent));
            HP = HT;
        }
        
        // 三仙归洞+2：英雄获得镜像5回合视野
        if (hero.subClass == HeroSubClass.MAGICIAN && hero.hasTalent(Talent.THREE_IMMORTALS) && hero.pointsInTalent(Talent.THREE_IMMORTALS) >= 2) {
            Buff.append(hero, TalismanOfForesight.CharAwareness.class, 5f).charID = this.id();
        }
    }
    
    // 检查并更新可换位状态（用于镜像生成后立即显示粒子效果）
    public void updateSwapReady() {
        if (hero != null && hero.subClass == HeroSubClass.MAGICIAN && hero.hasTalent(Talent.THREE_IMMORTALS)) {
            boolean canSwap = !swapped 
                    && Dungeon.level.heroFOV[pos]
                    && this.canInteract(hero);
            
            Talent.SwapReady swapBuff = buff(Talent.SwapReady.class);
            if (canSwap && swapBuff == null && sprite != null) {
                Buff.affect(this, Talent.SwapReady.class);
            } else if (!canSwap && swapBuff != null) {
                swapBuff.detach();
            }
        }
    }

    @Override
    public boolean canInteract(Char c) {
        // 三仙归洞：允许英雄在视野内与未换位过的镜像交互
        if (c == Dungeon.hero 
                && hero != null 
                && hero.subClass == HeroSubClass.MAGICIAN 
                && hero.hasTalent(Talent.THREE_IMMORTALS)
                && Dungeon.level.heroFOV[pos]
                && !swapped) {
            PathFinder.buildDistanceMap(c.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
            return PathFinder.distance[pos] != Integer.MAX_VALUE;
        }
        return super.canInteract(c);
    }

    @Override
    public int damageRoll() {
        int damage;
        if (hero.belongings.weapon() != null) {
            damage = hero.belongings.weapon().damageRoll(this);
        } else {
            damage = hero.damageRoll(); //handles ring of force
        }
        // 以假乱真：镜像继承魔术师部分伤害
        if (hero.subClass == HeroSubClass.MAGICIAN && hero.hasTalent(Talent.FAKE_REALITY)) {
            int points = hero.pointsInTalent(Talent.FAKE_REALITY);
            damage = (int)(damage * (0.55f + 0.05f * points));
        } else {
            damage = (int)(damage * 0.5f);
        }
        return damage;
    }

    @Override
    public int attackSkill(Char target) {
        //same base attack skill as hero, benefits from accuracy ring and weapon
        int attackSkill = 9 + hero.lvl;
        if (Random.Float(1) < RingOfAgility.agilityChance(this)) {
            return INFINITE_ACCURACY;
        } else if (Random.Float(1) < -RingOfAgility.agilityChance(this)) {
            return 0;
        }
        if (hero.belongings.attackingWeapon() != null) {
            attackSkill *= hero.belongings.attackingWeapon().accuracyFactor(this, target);
        }
        return attackSkill;
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (hero != null) {
            int baseEvasion = 4 + hero.lvl;
            int heroEvasion = (int) ((4 + hero.lvl));
            if (Random.Float(1) < RingOfAgility.agilityChance(this)) {
                return INFINITE_EVASION;
            } else if (Random.Float(1) < -RingOfAgility.agilityChance(this)) {
                return 0;
            }

            //if the hero has more/less evasion, 50% of it is applied
            //includes ring of evasion boost
            return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
        } else {
            return 0;
        }
    }

    @Override
    public float attackDelay() {
        return hero.attackDelay(); //handles ring of furor
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy) || (hero.belongings.weapon() != null && hero.belongings.weapon().canReach(this, enemy.pos));
    }

    @Override
    public int drRoll() {
        int dr = super.drRoll();
        if (hero != null && hero.belongings.weapon() != null) {
            return dr + Random.NormalIntRange(0, hero.belongings.weapon().defenseFactor(this) / 2);
        } else {
            return dr;
        }
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);

        MirrorInvis buff = buff(MirrorInvis.class);
        if (buff != null) {
            buff.detach();
        }

        if (enemy instanceof Mob) {
            ((Mob) enemy).aggro(this);
        }
        if (hero.belongings.weapon() != null) {
            damage = hero.belongings.weapon().proc(this, enemy, damage);
            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail(this);
                GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", name())));
            }
            return damage;
        } else {
			//hero benefits from holy weapon and body form when unarmed, so do mirror images
			boolean wasEnemy = enemy.alignment == Alignment.ENEMY;
			if (hero.buff(BodyForm.BodyFormBuff.class) != null
					&& hero.buff(BodyForm.BodyFormBuff.class).enchant() != null){
				damage = hero.buff(BodyForm.BodyFormBuff.class).enchant().proc(new WornShortsword(), this, enemy, damage);
			}
			if (!wasEnemy || enemy.alignment == Alignment.ENEMY) {
				if (hero.buff(HolyWeapon.HolyWepBuff.class) != null) {
					int dmg = hero.subClass == HeroSubClass.PALADIN ? 6 : 2;
					enemy.damage(Math.round(dmg * Weapon.Enchantment.genericProcChanceMultiplier(this)), HolyWeapon.INSTANCE);
				}
			}
            return damage;
        }
    }

    @Override
    public CharSprite sprite() {
        CharSprite s = super.sprite();

        hero = (Hero) Actor.findById(heroID);
        if (hero != null) {
            armTier = hero.tier();
        } else {
            armTier = 1;
        }
        ((MirrorSprite) s).updateArmor(armTier);
        return s;
    }

    {
        immunities.add(ToxicGas.class);
        immunities.add(CorrosiveGas.class);
        immunities.add(Burning.class);
        immunities.add(AllyBuff.class);
    }

    public static class MirrorInvis extends Invisibility {

        {
            announced = false;
        }

        @Override
        public int icon() {
            return BuffIndicator.NONE;
        }
    }
}


