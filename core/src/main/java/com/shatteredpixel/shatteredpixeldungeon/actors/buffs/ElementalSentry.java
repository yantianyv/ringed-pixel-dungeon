package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ElementalSentry extends Buff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    public static final float DURATION = 8f;

    private float left = 0f;
    private int power = 1;

    public void set(float duration) {
        left = duration;
    }

    public void setPower(int power) {
        this.power = power;
    }

    private static final ElementBuff.Element[] ELEMENTS = {
            ElementBuff.Element.PYRO,
            ElementBuff.Element.HYDRO,
            ElementBuff.Element.ANEMO,
            ElementBuff.Element.ELECTRO,
            ElementBuff.Element.CRYO,
            ElementBuff.Element.GEO,
            ElementBuff.Element.DENDRO
    };

    @Override
    public int icon() {
        return BuffIndicator.WAND;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.8f, 1f, 0.6f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int) Math.ceil(left));
    }

    @Override
    public boolean act() {
        Hero hero = (Hero) target;
        Char enemy = findClosestEnemy(hero);
        if (enemy != null) {
            ElementBuff.Element element = Random.element(ELEMENTS);
            CellEmitter.get(enemy.pos).burst(SparkParticle.FACTORY, 5);
            ElementBuff.apply(element, hero, enemy, 1f);
            enemy.damage(power, this);
        }
        left--;
        if (left < 0) {
            detach();
        } else {
            spend(TICK);
        }
        return true;
    }

    protected Char findClosestEnemy(Hero hero) {
        Char closest = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Char ch : Actor.chars()) {
            if (ch != hero && ch.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[ch.pos]) {
                int dist = Dungeon.level.distance(hero.pos, ch.pos);
                if (dist < bestDistance) {
                    bestDistance = dist;
                    closest = ch;
                }
            }
        }
        return closest;
    }

    private static final String LEFT = "left";
    private static final String POWER = "power";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, left);
        bundle.put(POWER, power);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        left = bundle.getFloat(LEFT);
        power = bundle.getInt(POWER);
    }
}
