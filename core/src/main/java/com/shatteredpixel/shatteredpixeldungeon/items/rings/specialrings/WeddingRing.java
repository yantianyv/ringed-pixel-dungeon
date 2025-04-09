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
            // 基本统计信息，其中soloBuffedBonus()是当前戒指等级
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", soloBuffedBonus() * soloBuffedBonus()));
            //组合统计信息，其中combinedBuffedBonus(Dungeon.hero)是所有已装备同类戒指的等级之和
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", combinedBuffedBonus(Dungeon.hero) * 2));
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
