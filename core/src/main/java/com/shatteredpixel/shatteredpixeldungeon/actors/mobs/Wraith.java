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
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ChallengeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class Wraith extends Mob {

    private static final float SPAWN_DELAY = 2f;

    protected int level;

    {
        spriteClass = WraithSprite.class;

        HP = HT = 1;
        EXP = 0;

        maxLvl = -2;

        flying = true;

        properties.add(Property.UNDEAD);
        properties.add(Property.INORGANIC);

        num_of_escape = 0;

    }

    private static final String LEVEL = "level";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getInt(LEVEL);
        adjustStats(level);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1 + level / 2, 2 + level);
    }

    @Override
    public int attackSkill(Char target) {
        return 10 + level;
    }

    /**
     * 进入隐身状态，持续2回合，并播放诅咒音效和粒子效果
     */
    private void becomeInvisible() {
        Buff.affect(this, Invisibility.class, 2f);
        
        // 播放诅咒音效和粒子效果
        if (sprite != null) {
            boolean visible = Dungeon.level.heroFOV[pos];
            if (visible) {
                Sample.INSTANCE.play(Assets.Sounds.CURSED);
            }
            sprite.emitter().burst(ShadowParticle.CURSE, 5);
        }
    }

    @Override
    public String defenseVerb() {
        // 被攻击但成功闪避时会调用此方法，用于提示“Miss”
        becomeInvisible();
        return super.defenseVerb();
    }

    @Override
    public void onAttackComplete() {
        // 保持隐身：不调用 invisibility(0)
        attack(enemy);
        spend(attackDelay());
        super.onAttackComplete();
        // 挑战：保留原逻辑
        if (Dungeon.isChallenged(Challenges.INVISIBLE_WAR)) {
            invisibility(1);
        }
    }

    public void adjustStats(int level) {
        this.level = level;
        defenseSkill = attackSkill(null) * 5;
        enemySeen = true;
    }

    @Override
    public float spawningWeight() {
        return 0f;
    }

    @Override
    public boolean reset() {
        state = WANDERING;
        return true;
    }

    public static void spawnAround(int pos) {
        spawnAround(pos, null);
    }

    public static void spawnAround(int pos, Class<? extends Wraith> wraithClass) {
        for (int n : PathFinder.NEIGHBOURS4) {
            spawnAt(pos + n, wraithClass, false);
        }
    }

    public static Wraith spawnAt(int pos) {
        return spawnAt(pos, null);
    }

    public static Wraith spawnAt(int pos, Class<? extends Wraith> wraithClass) {
        return spawnAt(pos, wraithClass, true);
    }

    private static Wraith spawnAt(int pos, Class<? extends Wraith> wraithClass, boolean allowAdjacent) {

        //if the position itself is blocked, try to place in an adjacent cell if allowed
        if (Dungeon.level.solid[pos] || Actor.findChar(pos) != null) {
            ArrayList<Integer> candidates = new ArrayList<>();

            for (int i : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.solid[pos + i] && Actor.findChar(pos + i) == null) {
                    candidates.add(pos + i);
                }
            }

            if (allowAdjacent && !candidates.isEmpty()) {
                pos = Random.element(candidates);
            } else {
                pos = -1;
            }

        }

        if (pos != -1) {

            Wraith w;
            //if no wraith type is specified, 1/100 chance for exotic, otherwise normal
            if (wraithClass == null) {
                float altChance = 1 / 100f * RatSkull.exoticChanceMultiplier();
                if (Random.Float() < altChance) {
                    w = new TormentedSpirit();
                } else {
                    w = new Wraith();
                }
            } else {
                w = Reflection.newInstance(wraithClass);
            }
            w.adjustStats(Dungeon.scalingDepth());
            w.pos = pos;
            w.state = w.HUNTING;
            GameScene.add(w, SPAWN_DELAY);
            Dungeon.level.occupyCell(w);

            w.sprite.alpha(0);
            w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

            if (w instanceof TormentedSpirit) {
                w.sprite.emitter().burst(ChallengeParticle.FACTORY, 10);
            } else {
                w.sprite.emitter().burst(ShadowParticle.CURSE, 5);
            }
            
            // 怨灵生成时进入隐身状态
            w.becomeInvisible();

            return w;
        } else {
            return null;
        }
    }

}
