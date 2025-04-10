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

public class RingOfTakeout extends Ring {

    //这是一个戒指模板文件
    {
        icon = ItemSpriteSheet.Icons.RING_TAKEOUT;// 图标，在core\src\main\java\sprites\ItemSpriteSheet.java设置
        buffClass = Takeout.class;// 戒指的buff类型
    }

    protected static float basechance = 1f;

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

    public static float takeoutChance(Char target) {// 触发进食的几率
        return (float) (1 - Math.pow(0.995, getBuffedBonus(target, Takeout.class)));
    }

    public static void refresh() {
        basechance = 1f;
    }

    // 定义RingBuff类
    public class Takeout extends RingBuff {

        @Override
        public boolean act() {
            // 触发拼好饭之戒
            if (Math.random() < RingOfTakeout.takeoutChance(target) * basechance && RingOfTakeout.takeoutChance(target) > 0 && !Dungeon.level.locked) {
                basechance *= 0.99f;
                // 拼好饭戒指的进餐逻辑
                Buff.affect(Dungeon.hero, Hunger.class).satisfy(RingOfTakeout.eatEffectSatiety(target));
                Talent.onFoodEaten(hero, RingOfTakeout.eatEffectSatiety(target), null);
                spend(TICK);
                return true;
            }
            return super.act();
        }

    }

}
