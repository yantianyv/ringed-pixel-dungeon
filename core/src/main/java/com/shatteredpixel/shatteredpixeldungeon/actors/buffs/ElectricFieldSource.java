package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class ElectricFieldSource extends Buff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    public static final float DURATION = 10f;
    private static final float TICK_RATE = 0.5f;
    private static final int DAMAGE_PER_TICK = 1;
    private static final float QUANTITY_PER_TICK = 0.5f;

    private float left = 0f;

    public void set(float duration) {
        left = duration;
    }

    @Override
    public int icon() {
        return BuffIndicator.LIGHT;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.8f, 0.9f, 1f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int) Math.ceil(left));
    }

    @Override
    public boolean act() {
        if (target.sprite != null) {
            target.sprite.centerEmitter().burst(SparkParticle.FACTORY, 1);
        }
        for (int i = 0; i < Dungeon.level.length(); i++) {
            if (Dungeon.level.heroFOV[i] && Dungeon.level.distance(target.pos, i) <= 2) {
                Char ch = Actor.findChar(i);
                if (ch != null && ch.alignment != Char.Alignment.ALLY && ch != target) {
                    ch.damage(DAMAGE_PER_TICK, this);
                    if (ch.isAlive()) {
                        ElementBuff.apply(ElementBuff.Element.ELECTRO, target, ch, QUANTITY_PER_TICK);
                    }
                }
            }
        }
        left -= TICK_RATE;
        if (left <= 0) {
            detach();
        } else {
            spend(TICK_RATE);
        }
        return true;
    }

    private static final String LEFT = "left";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        left = bundle.getFloat(LEFT);
    }
}
