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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff.Element;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TravelerCorrosion;
import com.watabou.utils.Bundle;

public class TravelerCorrosiveGas extends CorrosiveGas {

	private int attackerID = -1;
	private Element element = Element.ANEMO;
	private float elementQuantity = 0.5f;

	public TravelerCorrosiveGas setAttacker(int id) {
		this.attackerID = id;
		return this;
	}

	public TravelerCorrosiveGas setElement(Element element, float quantity) {
		this.element = element;
		this.elementQuantity = quantity;
		return this;
	}

	@Override
	protected void affectCorrosion(Char ch) {
		TravelerCorrosion corrosion = Buff.affect(ch, TravelerCorrosion.class);
		corrosion.set(2f, strength, source);
		corrosion.setElemental(attackerID, element, elementQuantity);
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
