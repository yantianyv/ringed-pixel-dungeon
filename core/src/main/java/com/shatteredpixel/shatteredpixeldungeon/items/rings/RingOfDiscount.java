package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.VisualShop;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RingOfDiscount extends Ring {

    //这是一个戒指模板文件
    {
        icon = ItemSpriteSheet.Icons.RING_DISCOUNT;// 图标，在core\src\main\java\prites\ItemSpriteSheet.java设置
        buffClass = Discount.class;// 戒指的buff类型
        ad_url = "https://mobile.yangkeduo.com/duo_collection.html?__page=dynamic&pid=36256075_308324886&duoduo_type=3&launch_pdd=1&campaign=ddjb&cid=launch_";
    }

    static Heap goodHeap;
    static Item good;
    static public boolean baned = false;

    // 返回物品描述
    public String statsInfo() {
        // 依据是否鉴定返回不同信息
        if (baned){
            return Messages.get(this, "ban");

        }
        if (isIdentified()) {
            // 基本统计信息，使用与实际计算一致的公式
            // 实际计算使用 getBuffedBonus()，它返回所有 buff 的 buffedLvl() 之和
            // 对于单个戒指，就是 buffedLvl()
            // 显示的是折扣后的价格比例（乘以10是为了显示百分比）
            int actualBonus = buffedLvl();
            float discountMulti = (float) Math.pow(0.99, actualBonus);
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", discountMulti * 10));
            
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
                
                // 如果有多个戒指，使用 discountMultiplier 直接计算实际效果
                if (ringCount > 1) {
                    float combinedMulti = discountMultiplier(Dungeon.hero);
                    info += "\n\n" + Messages.get(this, "combined_stats",
                            Messages.decimalFormat("#.##", combinedMulti * 10));
                }
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
        if (baned){
            return 1f;
        }
        return (float) Math.pow(0.99, getBuffedBonus(target, Discount.class));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        if (goodHeap != null) {
            goodHeap.remove(good);
            if (goodHeap != null) {
                goodHeap.type = Heap.Type.HEAP;
            }
            goodHeap = null;
        }
        super.storeInBundle(bundle);
        bundle.put("baned", baned);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        baned = bundle.getBoolean("baned");
    }

    public static void ban() {
        baned = true;
        rm_good();
    }

    public static void unban() {
        baned = false;
    }

    private static void rm_good() {
        if (goodHeap != null && goodHeap.type == Heap.Type.FOR_SALE) {
            // GLog.p("移除商品" + good);
            if (goodHeap.items.remove(good) && goodHeap.items.isEmpty()) {
                goodHeap.destroy();
            } else {
                goodHeap.type = Heap.Type.HEAP;
                goodHeap = null;
            }
        }
    }
    // ————————————————戒指效率————————————————
    private static float efficiency = 1.0f;

    @Override
    public float efficiency() {
        if (baned) {
            return 0;
        }
        return efficiency; // 返回当前类别的共享效率
    }

    @Override
    public void efficiency(float x) {
        if (baned) {
            efficiency = 0;
        } else {
            x = x > 1 ? 1 : x;
            x = x < 0 ? 0 : x;
            efficiency = x;
        }
    }

    public void charge(float x) {
        efficiency(efficiency + x);
    }

    // ————————————————————————————————————————
    // 定义RingBuff类
    public class Discount extends RingBuff {

        @Override
        public boolean act() {
            float chance = (1 - (float) Math.pow(0.99, getBuffedBonus(target, Discount.class))) / 10;
            if (Random.Float() < chance * efficiency) {
                int pospointer = Dungeon.hero.pos;
                boolean success = false;
                for (int i = 0; i <= 8; i++) {
                    pospointer = Dungeon.hero.pos + PathFinder.NEIGHBOURS9[i];
                    if ((Terrain.flags[Dungeon.level.map[pospointer]] & Terrain.PASSABLE) == 0 // 不可通过
                            || (Terrain.flags[Dungeon.level.map[pospointer]] & Terrain.SECRET) != 0 // 存在隐藏物体
                            || Dungeon.level.map[pospointer] == Terrain.ENTRANCE // 下楼
                            || Dungeon.level.map[pospointer] == Terrain.ENTRANCE_SP // 特殊下楼
                            || Dungeon.level.map[pospointer] == Terrain.EXIT // 上楼
                            || (Dungeon.level.heaps.get(pospointer) != null) // 存在掉落物
                            ) {
                    } else {
                        success = true;
                        break;
                    }
                }
                if (Dungeon.level.locked || Dungeon.depth == 10) {
                    efficiency *= 0.999;
                    // 特殊情况只有弱化的掉落
                    Item gold = new Gold();
                    gold.quantity(1);
                    // Dungeon.level.drop(gold, pos);
                    Dungeon.hero.spend(-TIME_TO_PICK_UP);
                    gold.doPickUp(Dungeon.hero);
                    GLog.p(Messages.get(RingOfDiscount.class, "drop_gold"));
                    spend(30f);
                } else if (Dungeon.gold > 3000 && success) {
                    efficiency *= 0.9;
                    // 触发百亿补贴
                    VisualShop visualshop = new VisualShop();
                    rm_good();
                    good = visualshop.chooseRandom();
                    goodHeap = Dungeon.level.drop(good, pospointer);
                    goodHeap.type = Heap.Type.FOR_SALE;
                    goodHeap.sprite.hardlight(0xFFFF99);
                    GLog.p(Messages.get(RingOfDiscount.class, "on_sale"));
                    Dungeon.hero.resting = false;
                    Dungeon.hero.interrupt();
                    spend(30f);
                } else {
                    efficiency *= 0.95;
                    // 触发红包到账
                    Item gold = new Gold();
                    gold.quantity(Dungeon.depth * 10);
                    // Dungeon.level.drop(gold, pos);
                    Dungeon.hero.spend(-TIME_TO_PICK_UP);
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

}
