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

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class AliceThresholdBuff extends Buff implements Hero.Doom {

    private static final float DEBT_PERIOD = 10f; // 每10回合
    private static final int INITIAL_DEBT = 1000; // 初始贷款1000金币
    private static final int LEVEL_REPAY = 100; // 每次升级偿还100金币
    private static final int MAX_LEVEL_REPAY = 30; // 30级后不再扣除

    private float turnCounter = 0; // 回合计数器

    private static final String TURN_COUNTER = "turn_counter";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TURN_COUNTER, turnCounter);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        turnCounter = bundle.getFloat(TURN_COUNTER);
    }

    @Override
    public boolean act() {
        if (target.isAlive() && target instanceof Hero) {
            // 每回合增加计数器
            turnCounter++;

            // 每10回合减少1金币
            if (turnCounter >= DEBT_PERIOD) {
                turnCounter = 0;
                Dungeon.gold--;

                // 检查是否死亡
                checkDeath();
            }

            spend(TICK);
        } else {
            diactivate();
        }

        return true;
    }

    // 升级时调用此方法偿还金币
    public void onLevelUp(int level) {
        if (level <= MAX_LEVEL_REPAY) {
            int repayAmount = Math.min(LEVEL_REPAY, Dungeon.gold);
            Dungeon.gold -= repayAmount;

            GLog.w(Messages.get(this, "repay", repayAmount));

            // 检查是否死亡
            checkDeath();
        }
    }

    // 检查金币是否<=0，如果是则杀死英雄
    public void checkDeath() {
        if (Dungeon.gold <= 0) {
            Dungeon.gold = 0;
            target.damage(target.HT + 1, this);
        }
    }

    // 获取金币收入比例
    public static float getGoldRatio() {
        if (!Dungeon.isChallenged(Challenges.ALICE_THRESHOLD)) {
            return 1.0f;
        }
        return Math.max(0, (float) Dungeon.gold / INITIAL_DEBT);
    }

    @Override
    public int icon() {
        return BuffIndicator.NONE;
    }

    @Override
    public String name() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc",
                Dungeon.gold,
                INITIAL_DEBT,
                (int) (DEBT_PERIOD - turnCounter));
    }

    @Override
    public void onDeath() {
        Dungeon.fail(this);
        GLog.n(Messages.get(this, "ondeath"));
    }
}
