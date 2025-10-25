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

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

/**
 * Frost类是一个负面的Debuff(减益效果)类，继承自FlavourBuff
 * 它使目标角色被冻结，并可能冻结其物品栏中的特定物品
 */
public class Frost extends FlavourBuff {

    // 持续时间常量，设置为10秒
    public static final float DURATION = 10f;

    // 初始化块，设置buff类型为负面效果，并且会 announced(显示通知)
    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    // 冰霜伤害变量，用于记录冰霜造成的伤害
    public int frost_damage = 0;

    /**
     * 将此buff附加到目标角色上
     * @param target 要附加此buff的角色
     * @return 如果成功附加返回true，否则返回false
     */
    @Override
    public boolean attachTo(Char target) {
        // 如果目标有燃烧buff，则先移除它
        Buff.detach(target, Burning.class);

        // 调用父类的attachTo方法
        if (super.attachTo(target)) {

            // 增加目标的麻痹计数
            target.paralysed++;
            // 移除目标的寒冷效果
            Buff.detach(target, Chill.class);

            // 如果目标是英雄
            if (target instanceof Hero) {

                Hero hero = (Hero) target;
                // 创建一个可冻结物品的列表
                ArrayList<Item> freezable = new ArrayList<>();
                //does not reach inside of containers
                if (!hero.belongings.lostInventory()) {
                    for (Item i : hero.belongings.backpack.items) {
                        if (!i.unique && (i instanceof Potion || i instanceof MysteryMeat)) {
                            freezable.add(i);
                        }
                    }
                }

                if (!freezable.isEmpty()) {
                    Item toFreeze = Random.element(freezable).detach(hero.belongings.backpack);
                    GLog.w(Messages.capitalize(Messages.get(this, "freezes", toFreeze.title())));
                    if (toFreeze instanceof Potion) {
                        ((Potion) toFreeze).shatter(hero.pos);
                    } else if (toFreeze instanceof MysteryMeat) {
                        FrozenCarpaccio carpaccio = new FrozenCarpaccio();
                        if (!carpaccio.collect(hero.belongings.backpack)) {
                            Dungeon.level.drop(carpaccio, target.pos).sprite.drop();
                        }
                    }
                }

            } else if (target instanceof Thief) {

                Item item = ((Thief) target).item;

                if (item instanceof Potion && !item.unique) {
                    ((Potion) ((Thief) target).item).shatter(target.pos);
                    ((Thief) target).item = null;
                } else if (item instanceof MysteryMeat) {
                    ((Thief) target).item = new FrozenCarpaccio();
                }

            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (target.paralysed > 0) {
            target.paralysed--;
        }
        // if (Dungeon.level.water[target.pos]) {
        //     Buff.prolong(target, Chill.class, Chill.DURATION / 2f);
        // }
        target.damage(frost_damage, new ElementBuff());
    }

    @Override
    public int icon() {
        return BuffIndicator.FROST;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0f, 0.75f, 1f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION - visualcooldown()) / DURATION);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            target.sprite.add(CharSprite.State.FROZEN);
            target.sprite.add(CharSprite.State.PARALYSED);
        } else {
            target.sprite.remove(CharSprite.State.FROZEN);
            if (target.paralysed <= 1) {
                target.sprite.remove(CharSprite.State.PARALYSED);
            }
        }
    }

    {
        //can't chill what's frozen!
        immunities.add(Chill.class);
    }

}
