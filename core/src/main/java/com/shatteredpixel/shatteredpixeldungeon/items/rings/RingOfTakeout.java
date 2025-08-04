package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class RingOfTakeout extends Ring {

    //这是一个戒指模板文件
    {
        icon = ItemSpriteSheet.Icons.RING_TAKEOUT;// 图标，在core\src\main\java\sprites\ItemSpriteSheet.java设置
        buffClass = Takeout.class;// 戒指的buff类型
        ad_url = "eleme://miniapp?bc_fl_src=locallife_wtzt_0-0-ADGROUPID-__REQID__-2&url=https%3A%2F%2Fm.duanqu.com%3F_ariver_appid%3D2021004131606232%26page%3Dpages%252Ftaoke-guide%252Findex%253Fscene%253D653e610136b546a9a4443837466d0021%2526o2i_1st_clk%253D__CLICK_ID__";
    }

    // 返回物品描述
    public String statsInfo() {
        // 依据是否鉴定返回不同信息
        if (isIdentified()) {
            // 基本统计信息，其中soloBuffedBonus()是当前戒指等级
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100 * (1 - Math.pow(0.995, soloBuffedBonus()))));
            //组合统计信息，其中combinedBuffedBonus(Dungeon.hero)是所有已装备同类戒指的等级之和
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100 * (1 - Math.pow(0.995, combinedBuffedBonus(Dungeon.hero)))));
            }
            return info;
        } else {// 鉴定前的通用信息
            return Messages.get(this, "typical_stats", 0.5);
        }
    }

    public static int eatEffectSatiety(Char target) {
        int satiety = getBuffedBonus(target, Takeout.class);
        if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
            satiety /= 3;
        }
        return satiety;

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

    public static float takeoutChance(Char target) {// 触发进食的几率
        return (float) (1 - Math.pow(0.995, getBuffedBonus(target, Takeout.class)));
    }

    protected static float takeout_cooldown = 0f;// 冷却

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("takeout_cooldown", takeout_cooldown);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
    }

    // ————————————————戒指效率————————————————
    private static float efficiency = 1.0f;

    @Override
    public float efficiency() {
        return efficiency; // 返回当前类别的共享效率
    }

    @Override
    public void efficiency(float x) {
        x = x > 1 ? 1 : x;
        x = x < 0 ? 0 : x;
        efficiency = x;
    }

    public void charge(float x) {
        float charge = x / 3000;
        efficiency += (charge + (1f - efficiency) / 5f);
        efficiency = efficiency > 1 ? 1 : efficiency;
    }
    // ————————————————————————————————————————

    // 定义RingBuff类
    public class Takeout extends RingBuff {

        @Override
        public boolean act() {
            // 触发膨胀神券之戒
            if (Math.random() < RingOfTakeout.takeoutChance(target) * efficiency() * (1f - takeout_cooldown) && RingOfTakeout.takeoutChance(target) > 0 && !Dungeon.level.locked) {
                efficiency(0.95f * efficiency);
                takeout_cooldown = 1f;
                // 膨胀神券戒指的进餐逻辑
                if (Dungeon.hero.hasTalent(Talent.FOOD_HUNTING) && Dungeon.hero.pointsInTalent(Talent.FOOD_HUNTING) >= 3) {
                    Buff.affect(Dungeon.hero, Hunger.class).satisfy(15 + 1.5f * RingOfTakeout.eatEffectSatiety(target));
                } else {
                    Buff.affect(Dungeon.hero, Hunger.class).satisfy(10 + 1f * RingOfTakeout.eatEffectSatiety(target));
                }
                Talent.onFoodEaten(hero, RingOfTakeout.eatEffectSatiety(target), null);
                spend(TICK);
                return true;
            } else {
                takeout_cooldown -= 0.05f;
                takeout_cooldown = takeout_cooldown < 0 ? 0 : takeout_cooldown;
            }
            return super.act();
        }

    }

}
