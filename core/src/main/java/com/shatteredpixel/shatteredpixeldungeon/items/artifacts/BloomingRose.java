package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BloomingRose extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_ROSE3;
        // 仅用于规避上限显示逻辑，实际不可升级
        levelCap = 100;
        unique = true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int visiblyUpgraded() {
        return levelKnown ? level() : 0;
    }

    private static final Glowing PINK = new Glowing(0xFFCCCC);

    @Override
    public Glowing glowing() {
        return PINK;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new BloomingBlessing();
    }

    public static int bonus(Char ch) {
        BloomingBlessing b = ch.buff(BloomingBlessing.class);
        return b == null ? 0 : b.itemLevel() + 1;
    }

    // 友军回复，与婚戒一致
    public static int allyHealing(Char ch) {
        return bonus(ch) * 2;
    }

    // 以下三项是婚戒给予幽妹的效果，折半后给予英雄
    public static int strBonus(Char ch) {
        return bonus(ch) / 2;
    }

    public static int htBoost(Char ch) {
        int b = bonus(ch);
        return b * b / 2;
    }

    private static float halvedPower(int bonus) {
        return (1f - (float) Math.pow(0.99, bonus)) / 2f;
    }

    public static float lifesteal(Char ch) {
        return halvedPower(bonus(ch));
    }

    public static float reflect(Char ch) {
        return halvedPower(bonus(ch));
    }

    @Override
    public String desc() {
        String desc = super.desc();
        int b = level() + 1;
        String power = Messages.decimalFormat("#.##", 100f * halvedPower(b));
        desc += "\n\n" + Messages.get(this, "stats", b / 2, b * b / 2, power, power, b * 2);
        return desc;
    }

    public class BloomingBlessing extends ArtifactBuff {
        @Override
        public boolean act() {
            spend(TICK);
            return true;
        }
    }
}
