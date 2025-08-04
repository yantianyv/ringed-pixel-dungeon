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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Paralysis extends FlavourBuff {

	// 晕眩效果的持续时间
	public static final float DURATION	= 10f;

	// 设置buff类型为负面效果，并且被宣布
	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	// 将buff附加到目标上
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			target.paralysed++;
			return true;
		} else {
			return false;
		}
	}
	
	// 处理伤害
	public void processDamage( int damage ){
		if (target == null) return;
		ParalysisResist resist = target.buff(ParalysisResist.class);
		if (resist == null){
			resist = Buff.affect(target, ParalysisResist.class);
		}
		resist.damage += damage;
		if (Random.NormalIntRange(0, resist.damage) >= Random.NormalIntRange(0, target.HP)){
			if (Dungeon.level.heroFOV[target.pos]) {
				target.sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "out"));
			}
			detach();
		}
	}
	
	// 从目标上移除buff
	@Override
	public void detach() {
		super.detach();
		if (target.paralysed > 0)
			target.paralysed--;
	}
	
	// 返回buff的图标
	@Override
	public int icon() {
		return BuffIndicator.PARALYSIS;
	}

	// 返回buff的图标透明度
	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	// 添加或移除buff的视觉效果
	@Override
	public void fx(boolean on) {
		if (on)                         target.sprite.add(CharSprite.State.PARALYSED);
		else if (target.paralysed <= 1) target.sprite.remove(CharSprite.State.PARALYSED);
	}

	// 晕眩抵抗buff
	public static class ParalysisResist extends Buff {
		
		// 设置buff类型为正面效果
		{
			type = buffType.POSITIVE;
		}
		
		// 记录伤害值
		private int damage;
		
		// 每个回合减少伤害值，并检查是否需要移除buff
		@Override
		public boolean act() {
			if (target.buff(Paralysis.class) == null) {
				damage -= Math.ceil(damage / 10f);
				if (damage >= 0) detach();
			}
			spend(TICK);
			return true;
		}
		
		private static final String DAMAGE = "damage";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			damage = bundle.getInt(DAMAGE);
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			bundle.put( DAMAGE, damage );
		}
	}
}
