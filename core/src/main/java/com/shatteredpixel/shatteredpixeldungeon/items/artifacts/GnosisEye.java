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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.TravelerSpells;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

import java.util.ArrayList;

public class GnosisEye extends Artifact {

    public static final float MAX_ENERGY = 100f;

    public static final String AC_ELEMENTAL_BURST = "ELEMENTAL_BURST";

    private float energy = 0f;

    {
        image = ItemSpriteSheet.ARTIFACT_HOURGLASS; // 占位图标

        levelCap = 0;
        charge = 0;
        chargeCap = 0;

        defaultAction = AC_ELEMENTAL_BURST;

        unique = true;
        bones = false;
    }

    @Override
    public boolean doEquip(Hero hero) {
        if (hero.subClass != HeroSubClass.TRAVELER) {
            GLog.w(Messages.get(this, "not_traveler"));
            return false;
        }
        return super.doEquip(hero);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ELEMENTAL_BURST);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_ELEMENTAL_BURST)) {
            if (!isFull()) {
                GLog.w(Messages.get(this, "not_full"));
                return;
            }
            Wand wand = getStaffWand(hero);
            if (wand == null) {
                GLog.w(Messages.get(this, "no_wand"));
                return;
            }
            if (burstNeedsTarget(wand)) {
                promptTarget(hero, wand);
            } else {
                castBurst(hero, wand, hero.pos);
            }
        }
    }

    private void promptTarget(final Hero hero, final Wand wand) {
        curUser = hero;
        GameScene.selectCell(new CellSelector.Listener() {
            @Override
            public void onSelect(Integer target) {
                if (target == null) return;
                if (!isFull()) return;
                castBurst(hero, wand, target);
            }

            @Override
            public String prompt() {
                return Messages.get(GnosisEye.class, "burst_prompt");
            }
        });
    }

    private void castBurst(Hero hero, Wand wand, int target) {
        consumeAll();
        TravelerSpells.castBurst(wand, hero, target);
        hero.spendAndNext(Actor.TICK);
        updateQuickslot();
    }

    private static boolean burstNeedsTarget(Wand wand) {
        return wand instanceof WandOfBlastWave
                || wand instanceof WandOfCorrosion
                || wand instanceof WandOfCorruption
                || wand instanceof WandOfLivingEarth
                || wand instanceof WandOfTransfusion;
    }

    public static Wand getStaffWand(Hero hero) {
        if (hero.belongings.weapon instanceof MagesStaff) {
            return ((MagesStaff) hero.belongings.weapon).wand();
        }
        return null;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new EnergyTracker();
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
    }

    public float energy() {
        return energy;
    }

    public void setEnergy(float value) {
        energy = GameMath.gate(0f, value, MAX_ENERGY);
        updateQuickslot();
        BuffIndicator.refreshHero();
    }

    public void gainEnergy(float amount) {
        setEnergy(energy + amount);
    }

    public void consumeAll() {
        setEnergy(0f);
    }

    public boolean isFull() {
        return energy >= MAX_ENERGY;
    }

    @Override
    public String status() {
        return Messages.decimalFormat("#", energy) + "%";
    }

    @Override
    public String desc() {
        String desc = super.desc();
        desc += "\n\n" + Messages.get(this, "energy_desc", Messages.decimalFormat("#.0", energy));
        return desc;
    }

    @Override
    public int value() {
        return 0;
    }

    private static final String ENERGY = "energy";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ENERGY, energy);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        energy = bundle.getFloat(ENERGY);
    }

    public static GnosisEye getHeroGnosisEye(Hero hero) {
        if (hero == null) {
            return null;
        }
        for (Item item : hero.belongings) {
            if (item instanceof GnosisEye) {
                return (GnosisEye) item;
            }
        }
        return null;
    }

    public class EnergyTracker extends ArtifactBuff {

        @Override
        public int icon() {
            return BuffIndicator.GNOSIS_EYE;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.5f, 0.8f, 1f);
        }

        @Override
        public String iconTextDisplay() {
            GnosisEye eye = getHeroGnosisEye((Hero) target);
            if (eye != null) {
                return Messages.decimalFormat("#", eye.energy());
            }
            return super.iconTextDisplay();
        }

        @Override
        public boolean act() {
            spend(TICK);
            return true;
        }
    }
}
