package com.shatteredpixel.shatteredpixeldungeon.items.rings.specialrings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class WeddingRing extends SpecialRing {

    //这是一个戒指模板文件
    {
        buffClass = Weddingring.class;// 戒指的buff类型
        image = ItemSpriteSheet.WEDDING_RING;
    }

    public String statsInfo() {
        // 依据是否鉴定返回不同信息
        if (isIdentified()) {
            // 基本统计信息，使用与实际计算一致的公式
            // 显示的是 allyHealing，实际计算是 getBuffedBonus() * 2
            // 对于单个戒指，getBuffedBonus() = buffedLvl()
            int actualBonus = buffedLvl();
            int soloHealing = actualBonus * 2;
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", soloHealing));
            
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
                
                // 如果有多个戒指，显示组合效果
                if (ringCount > 1) {
                    int combinedHealing = allyHealing(Dungeon.hero);
                    info += "\n\n" + Messages.get(this, "combined_stats",
                            Messages.decimalFormat("#.##", combinedHealing));
                }
            }
            return info;
        } else {// 鉴定前的通用信息
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 1f));
        }
    }

    @Override
    // 返回戒指的buff对象
    protected RingBuff buff() {
        return new Weddingring();
    }

    public static int allyHealing(Char target) {
        return getBuffedBonus(target, Weddingring.class) * 2;
    }

    public static int extraHT(Char target) {
        return getBuffedBonus(target, Weddingring.class) * getBuffedBonus(target, Weddingring.class);
    }

    public static int extraStr(Char target) {
        return getBuffedBonus(target, Weddingring.class);
    }

    public static float ghostPower(Char target) {
        return 1f - (float) Math.pow(0.99, getBuffedBonus(target, Weddingring.class));
    }

    // 定义RingBuff类
    public class Weddingring extends RingBuff {
    }
}
