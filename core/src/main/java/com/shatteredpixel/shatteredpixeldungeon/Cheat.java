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
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Cheat extends Buff {

    @Override
    public int icon() {
        return BuffIndicator.CHEAT;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    public static final int XP_DUNGEON     = 1;

    
    public static final int MAX_VALUE      = 16;

    public static final String[] NAME_IDS = {
            "xp_dungeon"
    };

    public static final int[] MASKS = {
            XP_DUNGEON
    };

    public static int activeCheat() {
        int chCount = 0;
        for (int ch : MASKS) {
            if ((Dungeon.cheat & ch) != 0) {
                chCount++;
            }
        }
        return chCount;
    }

    public static String name( int cheat ){
        return NAME_IDS[cheat];
    }

    public static boolean isCheating() {
        return activeCheat() > 0;
    }

}
