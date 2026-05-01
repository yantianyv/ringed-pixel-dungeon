package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdBonus;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdBonus.AdType;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class RingOfTakeout extends Ring {

    //这是一个戒指模板文件
    {
        icon = ItemSpriteSheet.Icons.RING_TAKEOUT;// 图标，在core\src\main\java\sprites\ItemSpriteSheet.java设置
        buffClass = Takeout.class;// 戒指的buff类型
        ad_url = "eleme://miniapp?bc_fl_src=locallife_wtzt_0-0-ADGROUPID-__REQID__-2&url=https%3A%2F%2Fm.duanqu.com%3F_ariver_appid%3D2021004131606232%26page%3Dpages%252Ftaoke-guide%252Findex%253Fscene%253D04414e0a382543b1b031996af9d5720d%2526o2i_1st_clk%253D__CLICK_ID__";
    }

    // 返回物品描述
    public String statsInfo() {
        // 依据是否鉴定返回不同信息
        if (isIdentified()) {
            float interval = expectedTriggerInterval();
            if (interval > 99999f) {
                return Messages.get(this, "stats", "∞");
            }
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", interval));
            return info;
        } else {// 鉴定前的通用信息
            float typicalP = (float) (1 - Math.pow(0.995, 2));
            float typicalInterval = expectedTriggerInterval(typicalP, 1.0f);
            return Messages.get(this, "typical_stats",
                    Messages.decimalFormat("#.##", typicalInterval));
        }
    }

    private float expectedTriggerInterval() {
        int actualBonus = soloBuffedBonus();
        float p = (float) (1 - Math.pow(0.995, actualBonus)) * efficiency();
        return expectedTriggerInterval(p, 1.0f);
    }

    private static float expectedTriggerInterval(float p, float startCooldown) {
        if (p <= 0) return Float.MAX_VALUE;
        float[] S = new float[21];
        S[0] = 1f / p;
        for (int i = 1; i <= 20; i++) {
            float c = i * 0.05f;
            float triggerProb = p * (1f - c);
            S[i] = 1f + (1f - triggerProb) * S[i - 1];
        }
        int idx = Math.round(startCooldown / 0.05f);
        idx = Math.max(0, Math.min(20, idx));
        return S[idx];
    }

    public static int eatEffectSatiety(Char target) {
        int maxLvl = 0;
        for (Takeout buff : target.buffs(Takeout.class)) {
            maxLvl = Math.max(maxLvl, buff.buffedLvl());
        }
        if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
            maxLvl /= 3;
        }
        return maxLvl;
    }

    @Override
    // 返回戒指的buff对象
    protected RingBuff buff() {
        return new Takeout();
    }

    @Override
    public String desc() {
        String ascension = "";
        return (isKnown() ? super.desc() : Messages.get(this, "unknown_desc")) + ascension;
    }

    @Override
    public void onAdClick() {
        // 添加AdBonus的buff并把type设置成TAKEOUT
        Buff.affect(Dungeon.hero, AdBonus.class).setType(AdType.TAKEOUT);
        BuffIndicator.refreshHero();
    }

    public static float takeoutChance(Char target) {// 触发进食的几率
        int maxLvl = 0;
        float eff = 1.0f;
        for (Takeout buff : target.buffs(Takeout.class)) {
            int lvl = buff.buffedLvl();
            if (lvl > maxLvl) {
                maxLvl = lvl;
                eff = buff.getRing().efficiency();
            }
        }
        return (float) (1 - Math.pow(0.995, maxLvl)) * eff;
    }

    public void charge(float x) {
        float charge = x / 3000;
        efficiency += (charge + (1f - efficiency()) / 5f);
        efficiency = efficiency > 1 ? 1 : efficiency;
    }

    @Override
    protected float tick() {
        if (buff == null || !(buff instanceof Takeout)) return Actor.TICK;
        Takeout t = (Takeout) buff;
        float chance = (float) (1 - Math.pow(0.995, soloBuffedBonus()));
        if ((float)Math.random() < chance * efficiency() * (1f - t.cooldown) && chance > 0 && !Dungeon.level.locked) {
            efficiency(efficiency() * 0.95f);
            t.cooldown = 1f;
            int satiety = soloBuffedBonus();
            if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
                satiety /= 3;
            }
            if (Dungeon.hero.hasTalent(Talent.FOOD_HUNTING) && Dungeon.hero.pointsInTalent(Talent.FOOD_HUNTING) >= 3) {
                Buff.affect(Dungeon.hero, Hunger.class).satisfy(15 + 1.5f * satiety);
            } else {
                Buff.affect(Dungeon.hero, Hunger.class).satisfy(10 + 1f * satiety);
            }
            boolean hadHealth = hero.buff(Sungrass.Health.class) != null;
            Talent.onFoodEaten(hero, satiety, null);
            Sungrass.Health h = hero.buff(Sungrass.Health.class);
            if (h != null && !hadHealth && hero.resting) {
                h.setAutoTriggered(true);
            }
        } else {
            t.cooldown -= 0.05f;
            t.cooldown = t.cooldown < 0 ? 0 : t.cooldown;
        }
        return Actor.TICK;
    }

    // 定义RingBuff类
    public class Takeout extends RingBuff {

        private float cooldown = 0f;

        private static final String COOLDOWN = "cooldown";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(COOLDOWN, cooldown);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            cooldown = bundle.getFloat(COOLDOWN);
        }
    }

}
