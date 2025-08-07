package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfNahida extends Ring {

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
                    Messages.decimalFormat("#.##", 100 * (Math.pow(1.10, soloBuffedBonus()))));
            //组合统计信息，其中combinedBuffedBonus(Dungeon.hero)是所有已装备同类戒指的等级之和
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100 * (Math.pow(1.10, combinedBuffedBonus(Dungeon.hero)))));
            }
            return info;
        } else {// 鉴定前的通用信息
            return Messages.get(this, "typical_stats", 5);
        }
    }

    @Override
    // 返回戒指的buff对象
    protected RingBuff buff() {
        return new Nahida();
    }

    // 元素精通
    public static float elementalMastery(Char target) {
        return (float) Math.pow(1.10f, getBuffedBonus(target, Nahida.class)) * efficiency;
    }

    // 附魔加强
    public static float enchantPowerMultiplier(Char target) {
        return (float) Math.pow(1.10f, getBuffedBonus(target, Nahida.class));
    }
    // ————————————————戒指效率————————————————
    private static float efficiency = 1.0f;

    @Override
    public float efficiency() {
        return efficiency;
    }

    @Override
    public void efficiency(float x) {
        efficiency = x;
    }



    public void refresh(float x) {
        efficiency += 0.1;
        efficiency = efficiency > 1 ? 1 : efficiency;
    }

    // ————————————————————————————————————————
    // 定义RingBuff类
    public class Nahida extends RingBuff {

        @Override
        public boolean act() {
            efficiency *= 0.999;
            spend(TICK);
            return true;
        }
    }

}
