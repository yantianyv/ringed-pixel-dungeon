package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class AdBonus extends Buff {

    public enum AdType {
        DEFAULT,
        TAKEOUT
    }

    private AdType type = AdType.DEFAULT;

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
        // 不调用detach()实现不自然消失
        spend(TICK);
        return true;
    }

    public void setType(AdType type) {
        this.type = type;
    }

    public AdType getType() {
        return type;
    }

    private static final String TYPE = "type";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TYPE, type);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        type = bundle.getEnum(TYPE, AdType.class);
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
