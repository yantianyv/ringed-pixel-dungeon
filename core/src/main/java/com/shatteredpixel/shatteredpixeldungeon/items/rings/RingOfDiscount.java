package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.VisualShop;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class RingOfDiscount extends Ring {

    //这是一个戒指模板文件
    {
        icon = ItemSpriteSheet.Icons.RING_DISCOUNT;// 图标，在core\src\main\java\prites\ItemSpriteSheet.java设置
        buffClass = Discount.class;// 戒指的buff类型
    }

    static Heap goodHeap;

    // 返回物品描述
    public String statsInfo() {
        // 依据是否鉴定返回不同信息
        if (isIdentified()) {
            // 基本统计信息，其中soloBuffedBonus()是当前戒指等级
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 10 * Math.pow(0.99, soloBuffedBonus())));
            //组合统计信息，其中combinedBuffedBonus(Dungeon.hero)是所有已装备同类戒指的等级之和
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 10 * Math.pow(0.99, combinedBuffedBonus(Dungeon.hero))));
            }
            return info;
        } else {// 鉴定前的通用信息
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 9.9f));
        }
    }

    @Override
    // 返回戒指的buff对象
    protected RingBuff buff() {
        return new Discount();
    }

    public static float discountMultiplier(Char target) {
        return (float) Math.pow(0.99, getBuffedBonus(target, Discount.class));
    }

    // 定义RingBuff类
    public class Discount extends RingBuff {

        @Override
        public boolean act() {
            float chance = (1 - (float) Math.pow(0.99, getBuffedBonus(target, Discount.class))) / 10;
            if (Random.Float() < chance) {
                int pos = Dungeon.hero.pos;
                Heap heap = Dungeon.level.heaps.get(pos);
                if (Dungeon.level.locked || Dungeon.depth == 10) {
                    // 特殊情况只有弱化的掉落
                    Item gold = new Gold();
                    gold.quantity(1);
                    // Dungeon.level.drop(gold, pos);
                    gold.doPickUp(Dungeon.hero);
                    GLog.p(Messages.get(RingOfDiscount.class, "drop_gold"));
                    spend(30);
                } else if (Dungeon.gold > 3000) {
                    // 触发百亿补贴
                    VisualShop visualshop = new VisualShop();
                    Item good = visualshop.chooseRandom();
                    if (goodHeap != null) {
                        goodHeap.destroy();
                    }
                    goodHeap = Dungeon.level.drop(good, pos);
                    goodHeap.type = Heap.Type.FOR_SALE;
                    goodHeap.sprite.hardlight(0xFFFF99);
                    GLog.p(Messages.get(RingOfDiscount.class, "on_sale"));
                    spend(30);
                } else {
                    // 触发红包到账
                    Item gold = new Gold();
                    gold.quantity(Dungeon.depth * 10);
                    // Dungeon.level.drop(gold, pos);
                    gold.doPickUp(Dungeon.hero);
                    GLog.p(Messages.get(RingOfDiscount.class, "drop_gold"));
                    spend(Dungeon.depth);
                }
                // 显示光效
                new Flare(8, 32).color(0x00FFFF, true).show(Dungeon.hero.sprite, 2f);
            }
            return super.act();
        }
    }

// 保存游戏时，将商品堆保存到Bundle中，并摧毁商品堆
    @Override
    public void storeInBundle(Bundle bundle) {
        if (goodHeap != null) {
            goodHeap.destroy();
        }
        super.storeInBundle(bundle);
    }
}
