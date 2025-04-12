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
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff.Element;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Chill extends FlavourBuff {

    public static final float DURATION = 10f;

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        // 移除燃烧效果
        Buff.detach(target, Burning.class);

        // 附加冰元素效果，初始量为持续时间比例
        ElementBuff.apply(Element.CRYO, null, target, DURATION / 2f);

        return super.attachTo(target);
    }

    @Override
    public boolean act() {
        // 每回合维持冰元素量
        if (target.buff(ElementBuff.CryoElement.class) != null) {
            target.buff(ElementBuff.CryoElement.class).quantity = Math.min(
                    target.buff(ElementBuff.CryoElement.class).quantity + 0.5f,
                    DURATION
            );
        }

        return super.act();
    }

    // 减速效果基于冰元素量
    public float speedFactor() {
        float cryoAmount = target.buff(ElementBuff.CryoElement.class) != null
                ? target.buff(ElementBuff.CryoElement.class).quantity : 0f;
        // 基础减速10%，每点冰元素量额外减速8%，最大减速50%
        return Math.max(0.5f, 1f - 0.1f - cryoAmount * 0.08f);
    }

    @Override
    public void detach() {
        // 移除时清除冰元素
        ElementBuff.detach(target, Element.CRYO);
        super.detach();
    }

    @Override
    public int icon() {
        return BuffIndicator.FROST;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION - visualcooldown()) / DURATION);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            target.sprite.add(CharSprite.State.CHILLED);
        } else {
            target.sprite.remove(CharSprite.State.CHILLED);
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(), Messages.decimalFormat("#.##", (1f - speedFactor()) * 100f));
    }
}
