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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.specialrings.YogRing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;
import java.util.Collections;

public class CursingTrap extends Trap {

	{
		color = VIOLET;
		shape = WAVES;
	}

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[ pos ]) {
			CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
			Sample.INSTANCE.play(Assets.Sounds.CURSED);
		}

		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null){
			for (Item item : heap.items){
				if (item.isUpgradable() && !(item instanceof MissileWeapon))
					curse(item);
			}
		}

		if (Dungeon.hero.pos == pos && !Dungeon.hero.flying){
			curse(Dungeon.hero);
		}
	}

	public static void curse(Hero hero){
		//items the trap wants to curse because it will create a more negative effect
		ArrayList<Item> priorityCurse = new ArrayList<>();
		//items the trap can curse if nothing else is available.
		ArrayList<Item> canCurse = new ArrayList<>();

		KindOfWeapon weapon = hero.belongings.weapon();
		if (weapon instanceof Weapon && !(weapon instanceof MagesStaff)){
			if (((Weapon) weapon).enchantment == null)
				priorityCurse.add(weapon);
			else
				canCurse.add(weapon);
		}

		Armor armor = hero.belongings.armor();
		if (armor != null){
			if (armor.glyph == null)
				priorityCurse.add(armor);
			else
				canCurse.add(armor);
		}

		// 检查装备的 YogRing（优先被诅咒）
		Ring ring1 = hero.belongings.ring1();
		Ring ring2 = hero.belongings.ring2();
		Ring ring3 = hero.belongings.ring3();
		Ring ring4 = hero.belongings.ring4();
		Ring ring5 = hero.belongings.ring5();
		Ring ring6 = hero.belongings.ring6();
		
		// 只检查 YogRing，其他戒指不会被诅咒
		if (ring1 != null && ring1 instanceof YogRing && !ring1.cursed) {
			priorityCurse.add(ring1);
		}
		if (ring2 != null && ring2 instanceof YogRing && !ring2.cursed) {
			priorityCurse.add(ring2);
		}
		if (ring3 != null && ring3 instanceof YogRing && !ring3.cursed) {
			priorityCurse.add(ring3);
		}
		if (ring4 != null && ring4 instanceof YogRing && !ring4.cursed) {
			priorityCurse.add(ring4);
		}
		if (ring5 != null && ring5 instanceof YogRing && !ring5.cursed) {
			priorityCurse.add(ring5);
		}
		if (ring6 != null && ring6 instanceof YogRing && !ring6.cursed) {
			priorityCurse.add(ring6);
		}

		Collections.shuffle(priorityCurse);
		Collections.shuffle(canCurse);

		if (!priorityCurse.isEmpty()){
			curse(priorityCurse.remove(0));
		} else if (!canCurse.isEmpty()){
			curse(canCurse.remove(0));
		}

		EquipableItem.equipCursed(hero);
		GLog.n( Messages.get(CursingTrap.class, "curse") );
	}

	private static void curse(Item item){
		item.cursed = item.cursedKnown = true;

		if (item instanceof Weapon){
			Weapon w = (Weapon) item;
			if (w.enchantment == null){
				w.enchant(Weapon.Enchantment.randomCurse());
			}
		}
		if (item instanceof Armor){
			Armor a = (Armor) item;
			if (a.glyph == null){
				a.inscribe(Armor.Glyph.randomCurse());
			}
		}
	}
}
