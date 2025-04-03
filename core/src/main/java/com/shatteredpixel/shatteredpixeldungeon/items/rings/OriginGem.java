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
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.RingString;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

public class OriginGem extends Item {

    {
        image = ItemSpriteSheet.ORIGIN_GEM;

        stackable = true;

        defaultAction = AC_APPLY;

        bones = true;
    }

    private static final String AC_APPLY = "APPLY";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_APPLY);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_APPLY)) {

            curUser = hero;
            GameScene.selectItem(itemSelector);

        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 30 * quantity();
    }

    private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(OriginGem.class, "prompt");
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return RingString.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Ring || item instanceof OriginGem;
        }

        @Override
        public void onSelect(Item item) {
            if (item != null && (item instanceof Ring)) {
                Ring w = (Ring) item;
                // 对于未鉴定的戒指，花费1个鉴定
                if (w.isIdentified() == false) {
                    if (quantity() >= 1) {
                        w.identify();
                        quantity(quantity() - 1);
                        GLog.p(Messages.get(OriginGem.class, "identify"));
                        curUser.sprite.emitter().start(Speck.factory(Speck.QUESTION), 0.2f, 3);

                    } else {
                        GLog.w(Messages.get(OriginGem.class, "not_enough"));
                    }
                }// 对于已鉴定被诅咒的戒指，花费2个驱邪 
                else if (w.cursed == true) {
                    if (quantity() >= 2) {
                        ScrollOfRemoveCurse.uncurse(Dungeon.hero, w);
                        new Flare(6, 32).show(curUser.sprite, 2f);
                        quantity(quantity() - 2);
                        GLog.p(Messages.get(OriginGem.class, "remove_curse"));
                        curUser.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3);

                    } else {
                        GLog.w(Messages.get(OriginGem.class, "not_enough"));
                    }
                } else if (w.level() < 6) {
                    if (quantity() >= w.level() + 2) {
                        quantity(quantity() - (w.level() + 2));
                        w.level(w.level() + 1);
                        GLog.p(Messages.get(OriginGem.class, "upgrade"));
                        curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);

                    } else {
                        GLog.w(Messages.get(OriginGem.class, "not_enough"));
                    }

                } else {
                    GLog.w(Messages.get(OriginGem.class, "level_too_high"));

                }

                if (quantity() == 0) {
                    detachAll(Dungeon.hero.belongings.backpack);
                }

            } else if (item instanceof OriginGem) {
                if (quantity() >= 5) {
                    Item drop = Generator.random(Generator.Category.RING);
                    Dungeon.level.drop(drop, Dungeon.hero.pos);
                    quantity(quantity() - 5);
                    GLog.p(Messages.get(OriginGem.class, "identify"));
                }
            }
            Item.updateQuickslot();

            curUser.sprite.operate(curUser.pos);

            curUser.spendAndNext(Actor.TICK);
        }
    };

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            return ingredients.size() == 1
                    && ingredients.get(0) instanceof Ring
                    && ingredients.get(0).isIdentified()
                    && !ingredients.get(0).cursed;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 5;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Item result = sampleOutput(ingredients);

            ingredients.get(0).quantity(0);

            return result;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            Ring w = (Ring) ingredients.get(0);
            int level = w.level();

            Item output = new OriginGem().quantity(level + 2);

            return output;
        }
    }

}
