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
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.OriginGem;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingString extends Bag {

    {
        image = ItemSpriteSheet.RINGSTRING;
    }

    @Override
    public boolean canHold(Item item) {
        if (item instanceof Ring || item instanceof OriginGem) {
            return super.canHold(item);
        } else {
            return false;
        }
    }

    @Override
    public int capacity() {
        if (Dungeon.hero == null || Dungeon.is_developer_mode()) {
            return 40;
        }
        return 9 + Dungeon.hero.lvl > 39 ? 39 : 9 + Dungeon.hero.lvl; // default container size
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public int value() {
        return 60;
    }

}
