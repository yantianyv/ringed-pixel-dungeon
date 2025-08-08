package com.shatteredpixel.shatteredpixeldungeon.items.rings.specialrings;

import static java.lang.Math.pow;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class YogRing extends SpecialRing {

    // 这是一个戒指模板文件
    {
        buffClass = Yogring.class;// 戒指的buff类型
        image = ItemSpriteSheet.YOG_RING;
    }

    public String statsInfo() {
        // 依据是否鉴定返回不同信息
        if (isIdentified()) {
            // 基本统计信息，其中soloBuffedBonus()是当前戒指等级
            float soloBonus = (float) (1 - (pow(0.9, soloBuffedBonus()))) / 3;
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", soloBonus * 100f));

            // 组合统计信息，其中combinedBuffedBonus(Dungeon.hero)是所有已装备同类戒指的等级之和
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                float combinedBonus = (float) (1 - (pow(0.9, combinedBuffedBonus(Dungeon.hero)))) / 3;
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", combinedBonus * 100f));
            }
            return info;
        } else {// 鉴定前的通用信息
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 1f));
        }
    }

    @Override
    // 返回戒指的buff对象
    protected RingBuff buff() {
        return new Yogring();
    }

    // 戒指的核心特色：等级负负得正
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

    @Override
    public Item curse(boolean x) {
        if (x == false || this.cursed) {
            level(level() - 1);
        }
        return super.curse(x);
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

    public static float killThreshold(Char target) {
        return (float) (1 - (pow(0.95, getBuffedBonus(target, Yogring.class)))) / 3;
    }

    // 腐化概率：仅在戒指被诅咒时生效
    public static float corruptionChance(Char target) {
        YogRing ring = (YogRing) getEquippedRing(target, YogRing.class);
        if (ring == null || !ring.cursed) {
            return 0; // 无戒指或未诅咒时返回0
        }
        return (float) (1 - (pow(0.95, getBuffedBonus(target, Yogring.class)))) / 2;
    }

    // 定义RingBuff类
    public class Yogring extends RingBuff {
        @Override
        public boolean act() {
            float threshold = killThreshold(Dungeon.hero);
            if (threshold > 0) {
                // 遍历场上的所有敌人
                for (Char ch : Dungeon.level.mobs) {
                    if (ch.alignment == Alignment.ENEMY && (float) (ch.HP / ch.HT) < threshold) {
                        if (Random.Float(1) < YogRing.corruptionChance(Dungeon.hero)
                                && !ch.isImmune(Corruption.class)) {
                            Corruption.corruptionHeal(ch);
                            AllyBuff.affectAndLoot((Mob) ch, Dungeon.hero, Corruption.class);
                        } else {
                            // GLog.p(YogRing.corruptionChance(this) + "");
                            ch.damage(ch.HT, this);
                            // 添加伏击的视觉效果
                            Wound.hit(ch);
                        }
                        spend(5f * TICK);
                        return true;
                    }
                }
            }
            spend(0.1f*TICK);
            return true;
        }
    }
}
