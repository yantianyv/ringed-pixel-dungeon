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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff.Element;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.particles.Emitter;

public class NanBeiLvDou extends MissileWeapon {

	{
		image = ItemSpriteSheet.SEED_SUNGRASS;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.1f;

		bones = false;

		tier = 3;
		baseUses = 5;
		sticky = false;
	}

    /**
     * 重写emitter方法，创建并配置一个发射器
     * @return 返回配置好的Emitter对象
     */
	@Override
	public Emitter emitter() {
        // 创建一个新的Emitter对象
		Emitter e = new Emitter();
        // 设置发射器的位置坐标为(5, 5)
		e.pos(5, 10);
        // 设置不填充目标
		e.fillTarget = true;
        // 以0.01f的速率发射通用类型的叶子粒子
		e.pour(LeafParticle.GENERAL, 0.1f);
        // 返回配置好的发射器对象
		return e;

	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage *= ElementBuff.apply(Element.DENDRO, attacker, defender, level());
		if (damage > 3) {
			damage /= 3;
		} else {
			damage = 1;
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	protected void onThrow(int cell) {
		Char enemy = Actor.findChar(cell); // 查找目标位置的角色
		if (enemy == null || enemy == curUser) { // 如果没有敌人或者目标是当前使用者
			parent = null; // 清除父对象引用

			// metamorphed seer shot logic - 变形先知射击逻辑
			if (curUser.hasTalent(Talent.SEER_SHOT) // 检查当前使用者是否拥有先知射击天赋
					&& curUser.heroClass != HeroClass.HUNTRESS // 且当前使用者不是猎人职业
					&& curUser.buff(Talent.SeerShotCooldown.class) == null) { // 且先知射击冷却已结束
				if (Actor.findChar(cell) == null) { // 如果目标位置没有角色
					RevealedArea a = Buff.affect(curUser, RevealedArea.class,
							5 * curUser.pointsInTalent(Talent.SEER_SHOT)); // 为当前使用者添加显示区域Buff
					a.depth = Dungeon.depth; // 设置Buff的深度为当前地下城深度
					a.pos = cell; // 设置Buff的位置为目标位置
					Buff.affect(curUser, Talent.SeerShotCooldown.class, 20f); // 为当前使用者添加先知射击冷却Buff
				}
			}

			if (!spawnedForEffect) { // 如果不是为了特效而生成的
				super.onThrow(cell); // 调用父类的投掷方法
			}
		} else { // 如果有敌人且不是当前使用者
			curUser.shoot(enemy, this);
			curUser.shoot(enemy, this);
			if (!curUser.shoot(enemy, this)) { // 如果当前使用者无法射击敌人
				rangedMiss(cell); // 执行射击未命中效果
			} else { // 如果成功射击

				rangedHit(enemy, cell); // 执行射击命中效果

			}
		}
	}
}
