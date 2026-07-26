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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff.Element;
import com.watabou.utils.Bundle;

public class TravelerCorrosion extends Corrosion {

	private int attackerID = -1;
	private Element element = Element.ANEMO;
	private float elementQuantity = 0.5f;

	public void setElemental(int attackerID, Element element, float quantity) {
		this.attackerID = attackerID;
		this.element = element;
		this.elementQuantity = quantity;
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			target.damage((int) damage, this);
			Object attacker = Actor.findById(attackerID);
			if (attacker == null) attacker = this;
			ElementBuff.apply(element, attacker, target, elementQuantity);
			if (damage < (Dungeon.scalingDepth() / 2) + 2) {
				damage++;
			} else {
				damage += 0.5f;
			}

			spend(TICK);
			if ((left -= TICK) <= 0) {
				detach();
			}
		} else {
			detach();
		}

		return true;
	}

	private static final String ATTACKER = "attacker";
	private static final String ELEMENT = "element";
	private static final String QUANTITY = "quantity";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ATTACKER, attackerID);
		bundle.put(ELEMENT, element);
		bundle.put(QUANTITY, elementQuantity);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		attackerID = bundle.getInt(ATTACKER);
		element = bundle.getEnum(ELEMENT, Element.class);
		elementQuantity = bundle.getFloat(QUANTITY);
	}
}
