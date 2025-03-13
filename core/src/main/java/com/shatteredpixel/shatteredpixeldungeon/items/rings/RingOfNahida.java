package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfNahida extends Ring {

    //这是一个戒指模板文件
    {
        icon = ItemSpriteSheet.Icons.RING_NAHIDA;// 图标，在core\src\main\java\sprites\ItemSpriteSheet.java设置
        buffClass = Nahida.class;// 戒指的buff类型
    }

    // 返回物品描述
    public String statsInfo() {
        // 依据是否鉴定返回不同信息
        if (isIdentified()) {
            // 基本统计信息，其中soloBuffedBonus()是当前戒指等级
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100 * (1 - Math.pow(0.95, soloBuffedBonus()))));
            //组合统计信息，其中combinedBuffedBonus(Dungeon.hero)是所有已装备同类戒指的等级之和
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100 * (1 - Math.pow(0.95, combinedBuffedBonus(Dungeon.hero)))));
            }
            return info;
        } else {// 鉴定前的通用信息
            return Messages.get(this, "typical_stats", 5);
        }
    }

    public static int eatEffectSatiety(Char target) {
        int satiety = getBuffedBonus(target, Nahida.class);
        if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
            satiety /= 3;
        }
        return satiety;

    }

    @Override
    // 返回戒指的buff对象
    protected RingBuff buff() {
        return new Nahida();
    }

    public static float grassBonusChance(Char target) {// 触发进食的几率
        return (float) (1 - Math.pow(0.95, getBuffedBonus(target, Nahida.class)));
    }

    // 定义RingBuff类
    public class Nahida extends RingBuff {
    }

}
