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
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

public class TalentIcon extends Image {

    private static TextureFilm film;
    private static final int SIZE = 16;

    public TalentIcon(Talent talent) {
        this(talent.icon());
    }

    public TalentIcon(int icon) {
        super(getIconResource(icon));  // 把逻辑移到静态方法里
        if (film == null) {
            film = new TextureFilm(texture, SIZE, SIZE);
        }
        frame(film.get(getAdjustedIcon(icon)));
    }

    // 辅助方法：决定使用哪个资源文件
    private static Object getIconResource(int icon) {
        if (icon < 1000) {
            return Assets.Interfaces.TALENT_ICONS;
        } else {
            return Assets.Interfaces.TALENT_ICONS_RINGED;
        }
    }

    // 辅助方法：调整 icon 值（如果 >=1000 就减去 1000）
    private static int getAdjustedIcon(int icon) {
        return (icon < 1000) ? icon : icon - 1000;
    }

}
