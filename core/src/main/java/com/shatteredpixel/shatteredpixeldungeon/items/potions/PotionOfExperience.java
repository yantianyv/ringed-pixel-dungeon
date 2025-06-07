/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Cheat;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfExperience extends Potion {

    {
        icon = ItemSpriteSheet.Icons.POTION_EXP;

        bones = true;

        talentFactor = 2f;
    }

    @Override
    public void apply(Hero hero) {
		identify();
		new Flare(6, 32).color(0xFFFF00, true).show(curUser.sprite, 2f);
        if (Dungeon.isCheated(Cheat.XP_DUNGEON)) {
            // 赋予英雄祝福效果
            Buff.prolong(hero, Bless.class, Bless.DURATION);
			// 赋予英雄治疗效果
			PotionOfHealing.cure(hero);
			// 赋予英雄无敌效果
			Buff.prolong(hero, Invulnerability.class, Invulnerability.DURATION);    
			// 终止后续结算        
			return;
        }
        hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(hero.maxExp()), FloatingText.EXPERIENCE);
        hero.earnExp(hero.maxExp(), getClass());
    }

    @Override
    public int value() {
        return isKnown() ? 50 * quantity : super.value();
    }

    @Override
    public int energyVal() {
        return isKnown() ? 10 * quantity : super.energyVal();
    }

	@Override
	public String desc() {
		return isKnown()&&Dungeon.isCheated(Cheat.XP_DUNGEON) ? Messages.get(this, "xpdungeon_desc"):super.desc();
	}
}
