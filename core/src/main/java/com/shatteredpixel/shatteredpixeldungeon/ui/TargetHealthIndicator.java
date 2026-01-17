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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class TargetHealthIndicator extends HealthBar {

    public static TargetHealthIndicator instance;

    private Char target;

    public TargetHealthIndicator() {
        super();

        instance = this;
    }

    @Override
    public void update() {
        super.update();

        // 友好怪物隐身时仍然可以选中并显示血条
        boolean isAlly = target != null && target.alignment == Char.Alignment.ALLY;
        boolean shouldHide = target != null && target.buff(Invisibility.class) != null && !isAlly;

        if (target != null && target.isAlive() && target.isActive()
                && target.sprite != null && target.sprite.visible
                && !shouldHide) {
            CharSprite sprite = target.sprite;
            width = sprite.width();
            x = sprite.x;
            y = sprite.y - 3;
            level(target);
            visible = true;
        } else {
            visible = false;
        }
    }

    public void target(Char ch) {
        // 友好怪物隐身时仍然可以选中
        boolean isAlly = ch != null && ch.alignment == Char.Alignment.ALLY;
        boolean canTarget = ch != null && ch.isAlive() && ch.isActive() 
                && (ch.buff(Invisibility.class) == null || isAlly);
        
        if (canTarget) {
            target = ch;
        } else {
            target = null;
        }
    }

    public Char target() {
        return target;
    }
}
