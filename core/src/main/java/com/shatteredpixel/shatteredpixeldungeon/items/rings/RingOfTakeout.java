package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdBonus;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdBonus.AdType;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
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
            // 基本统计信息，使用与实际计算一致的公式
            // 实际计算使用 getBuffedBonus()，它返回所有 buff 的 buffedLvl() 之和
            // 对于单个戒指，就是 buffedLvl()
            int actualBonus = buffedLvl();
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100 * (1 - Math.pow(0.995, actualBonus)) * efficiency));
            
            //组合统计信息，当装备多个同类戒指时显示
            if (isEquipped(Dungeon.hero)) {
                // 计算实际装备的同类戒指数量
                int ringCount = 0;
                if (Dungeon.hero.belongings.ring1() != null && Dungeon.hero.belongings.ring1().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring2() != null && Dungeon.hero.belongings.ring2().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring3() != null && Dungeon.hero.belongings.ring3().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring4() != null && Dungeon.hero.belongings.ring4().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring5() != null && Dungeon.hero.belongings.ring5().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring6() != null && Dungeon.hero.belongings.ring6().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.misc() != null && Dungeon.hero.belongings.misc().getClass() == getClass()) ringCount++;
                
                // 如果有多个戒指，使用 takeoutChance 直接计算实际效果
                if (ringCount > 1) {
                    float combinedChance = takeoutChance(Dungeon.hero);
                    info += "\n\n" + Messages.get(this, "combined_stats",
                            Messages.decimalFormat("#.##", 100 * combinedChance));
                }
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

    @Override
    public void onAdClick() {
        // 添加AdBonus的buff并把type设置成TAKEOUT
        Buff.affect(Dungeon.hero, AdBonus.class).setType(AdType.TAKEOUT);
        BuffIndicator.refreshHero();
    }

    public static float takeoutChance(Char target) {// 触发进食的几率
        return (float) (1 - Math.pow(0.995, getBuffedBonus(target, Takeout.class))) * efficiency;
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
        return efficiency>1?1:efficiency; // 返回当前类别的共享效率
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
            if ((float)Math.random() < RingOfTakeout.takeoutChance(target) * (1f - takeout_cooldown) && RingOfTakeout.takeoutChance(target) > 0 && !Dungeon.level.locked) {
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
