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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

// 皇帝的新全息匕首：骇客开局武器。
// 没有贴图（纯透明），更高的精准、更快的速度，伤害固定为 1~2。
public class HologramDagger extends MeleeWeapon {

    {
        image = ItemSpriteSheet.HOLOGRAM_DAGGER;
        tier = 1;

        ACC = 1.3f;  // 更高精准（默认 1）
        DLY = 0.8f;  // 更快速度（默认 1，越小越快）

        bones = false;
    }

    // 伤害固定为 1~2（更低伤害的惩罚性武器，主要价值是协同骇入与高精准/快速度）
    @Override
    public int min(int lvl) {
        return 1;
    }

    @Override
    public int max(int lvl) {
        return 2;
    }
}
