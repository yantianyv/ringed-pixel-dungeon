package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
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

    Heap goodHeap;
    Item good;
    static public boolean baned = false;

    public String statsInfo() {
        if (baned){
            return Messages.get(this, "ban");
        }
        if (isIdentified()) {
            int actualBonus = soloBuffedBonus();
            float discountMulti = (float) Math.pow(0.99, actualBonus);
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", discountMulti * 10));
            return info;
        } else {
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
        int maxLvl = 0;
        for (Discount buff : target.buffs(Discount.class)) {
            maxLvl = Math.max(maxLvl, buff.buffedLvl());
        }
        return (float) Math.pow(0.99, maxLvl);
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
        if (Dungeon.hero != null) {
            for (Ring r : Dungeon.hero.belongings.getEquippedRings()) {
                if (r instanceof RingOfDiscount) {
                    ((RingOfDiscount) r).rm_good();
                }
            }
        }
    }

    public static void unban() {
        baned = false;
    }

    private void rm_good() {
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

    @Override
    public float efficiency() {
        if (baned) {
            return 0;
        }
        return super.efficiency();
    }

    @Override
    public void efficiency(float x) {
        if (baned) {
            efficiency = 0;
        } else {
            super.efficiency(x);
        }
    }

    public void charge(float x) {
        efficiency(efficiency() + x);
    }

    @Override
    protected float tick() {
        float chance = (1 - (float) Math.pow(0.99, soloBuffedBonus())) / 10;
        if (Random.Float() < chance * efficiency()) {
            int pospointer = Dungeon.hero.pos;
            boolean success = false;
            for (int i = 0; i <= 8; i++) {
                pospointer = Dungeon.hero.pos + PathFinder.NEIGHBOURS9[i];
                if ((Terrain.flags[Dungeon.level.map[pospointer]] & Terrain.PASSABLE) == 0
                        || (Terrain.flags[Dungeon.level.map[pospointer]] & Terrain.SECRET) != 0
                        || Dungeon.level.map[pospointer] == Terrain.ENTRANCE
                        || Dungeon.level.map[pospointer] == Terrain.ENTRANCE_SP
                        || Dungeon.level.map[pospointer] == Terrain.EXIT
                        || (Dungeon.level.heaps.get(pospointer) != null)
                        ) {
                } else {
                    success = true;
                    break;
                }
            }
            if (Dungeon.level.locked || Dungeon.depth == 10) {
                efficiency *= 0.999;
                Item gold = new Gold();
                gold.quantity(1);
                Dungeon.hero.spend(-TIME_TO_PICK_UP);
                gold.doPickUp(Dungeon.hero);
                GLog.p(Messages.get(RingOfDiscount.class, "drop_gold"));
                return 30f;
            } else if (Dungeon.gold > 3000 && success) {
                efficiency *= 0.9;
                VisualShop visualshop = new VisualShop();
                rm_good();
                good = visualshop.chooseRandom();
                goodHeap = Dungeon.level.drop(good, pospointer);
                goodHeap.type = Heap.Type.FOR_SALE;
                goodHeap.sprite.hardlight(0xFFFF99);
                GLog.p(Messages.get(RingOfDiscount.class, "on_sale"));
                Dungeon.hero.resting = false;
                Dungeon.hero.interrupt();
                return 30f;
            } else {
                efficiency *= 0.95;
                Item gold = new Gold();
                gold.quantity(Dungeon.depth * 10);
                Dungeon.hero.spend(-TIME_TO_PICK_UP);
                gold.doPickUp(Dungeon.hero);
                GLog.p(Messages.get(RingOfDiscount.class, "drop_gold"));
                return Dungeon.depth;
            }
        }
        return Actor.TICK;
    }

    // 定义RingBuff类
    public class Discount extends RingBuff {
    }

}
