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
package com.shatteredpixel.shatteredpixeldungeon.items;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AliceThresholdBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Gold extends Item {

    {
        image = ItemSpriteSheet.GOLD;
        stackable = true;
    }

    public Gold() {
        this(1);
    }

    public Gold(int value) {
        this.quantity = value;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        return new ArrayList<>();
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {

        Catalog.setSeen(getClass());
        Statistics.itemTypesDiscovered.add(getClass());

        // 斩杀线挑战：金币不足1000时按比例削减
        int goldAmount = quantity;
        if (Dungeon.isChallenged(Challenges.ALICE_THRESHOLD) && Dungeon.gold < 1000) {
            float ratio = (float) Dungeon.gold / 1000f;
            goldAmount = (int) Math.ceil(quantity * ratio);
        }

        Dungeon.gold += goldAmount;
        Statistics.goldCollected += goldAmount;
        Badges.validateGoldCollected();

        GameScene.pickUp(this, pos);
        hero.sprite.showStatusWithIcon(CharSprite.NEUTRAL, Integer.toString(goldAmount), FloatingText.GOLD);
        hero.spendAndNext(TIME_TO_PICK_UP);

        Sample.INSTANCE.play(Assets.Sounds.GOLD, 1, 1, Random.Float(0.9f, 1.1f));
        updateQuickslot();

        // 斩杀线挑战：检查金币是否<=0
        if (Dungeon.isChallenged(Challenges.ALICE_THRESHOLD)) {
            AliceThresholdBuff debtBuff = hero.buff(AliceThresholdBuff.class);
            if (debtBuff != null) {
                debtBuff.checkDeath();
            }
        }

        return true;
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
    public Item random() {
        quantity = Random.IntRange(30 + Dungeon.depth * 10, 60 + Dungeon.depth * 20);
        return this;
    }

}
