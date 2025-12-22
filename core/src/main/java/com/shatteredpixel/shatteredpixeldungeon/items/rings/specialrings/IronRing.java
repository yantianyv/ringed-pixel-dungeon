package com.shatteredpixel.shatteredpixeldungeon.items.rings.specialrings;

import static java.lang.Math.pow;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class IronRing extends SpecialRing {

    // 这是一个戒指模板文件
    {
        buffClass = Ironring.class;// 戒指的buff类型
        image = ItemSpriteSheet.IRON_RING;
    }

    public String statsInfo() {
        // 依据是否鉴定返回不同信息
        if (isIdentified()) {
            // 基本统计信息，使用与实际计算一致的公式
            // 实际计算使用 getBuffedBonus()，它返回所有 buff 的 buffedLvl() 之和
            // 对于单个戒指，就是 buffedLvl()
            int actualBonus = buffedLvl();
            float soloBonus = (float) (pow(0.99, actualBonus)) / 10;
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", soloBonus * 100f));

            // 组合统计信息，当装备多个同类戒指时显示
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
                
                // 如果有多个戒指，计算组合效果
                if (ringCount > 1) {
                    // 使用 maxHurtRate 直接计算实际效果
                    float combinedBonus = maxHurtRate(Dungeon.hero);
                    info += "\n\n" + Messages.get(this, "combined_stats",
                            Messages.decimalFormat("#.##", combinedBonus * 100f));
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
        return new Ironring();
    }

    @Override
    public int soloBonus() {
        if (cursed) {
            return -this.level();
        } else {
            return this.level() + 1;
        }
    }

    @Override
    public int soloBuffedBonus() {
        if (cursed) {
            return -this.buffedLvl();
        } else {
            return this.buffedLvl() + 1;
        }
    }

    private static Item getEquippedRing(Char target, Class<? extends Ring> type) {
        if (target instanceof Hero) {
            for (Item item : ((Hero) target).belongings) {
                if (item.getClass() == type && item.isEquipped((Hero) target)) {
                    return item;
                }
            }
        }

        return null;
    }

    public static float maxHurtRate(Char target) {
        return (float) (pow(0.99, getBuffedBonus(target, Ironring.class))) / 10;
    }

    // 定义RingBuff类
    public class Ironring extends RingBuff {
    }
}
