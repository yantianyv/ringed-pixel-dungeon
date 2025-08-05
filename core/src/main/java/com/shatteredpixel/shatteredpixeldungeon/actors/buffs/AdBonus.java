package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class AdBonus extends Buff {

    public enum AdType {
        COOLDOWN, 
        TAKEOUT
    }

    public AdBonus() {} // Add default constructor
    
    private AdType type;
    private int cooldownTurns = 0;

    @Override
    public int icon() {
        return BuffIndicator.BLESS;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc_" + type.name().toLowerCase());
    }

    @Override
    public boolean act() {
        if (type == AdType.COOLDOWN) {
            cooldownTurns++;
            if (cooldownTurns >= 1000) {
                detach();
                return false;
            }
        }
        spend(TICK);
        return true;
    }

    @Override
    public void detach() {
        if (type != AdType.COOLDOWN) {
            type = AdType.COOLDOWN;
            cooldownTurns = 0;
            return;
        }
        super.detach();
    }

    public void setType(AdType type) {
        if (this.type == AdType.COOLDOWN) return;
        this.type = type;
    }

    public AdType getType() {
        return type;
    }

    private static final String TYPE = "type";
    private static final String COOLDOWN_TURNS = "cooldown_turns";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TYPE, type);
        bundle.put(COOLDOWN_TURNS, cooldownTurns);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        type = bundle.getEnum(TYPE, AdType.class);
        cooldownTurns = bundle.getInt(COOLDOWN_TURNS);
    }

    public static AdBonus getExistBuff(com.shatteredpixel.shatteredpixeldungeon.actors.Char target) {
        for (Buff buff : target.buffs()) {
            if (buff instanceof AdBonus) {
                return (AdBonus) buff;
            }
        }
        return null;
    }
}
