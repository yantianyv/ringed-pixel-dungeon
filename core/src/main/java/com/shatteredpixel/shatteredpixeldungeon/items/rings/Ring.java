/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator.Category;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.OldRing.Oldring;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public abstract class Ring extends KindofMisc {

    protected Buff buff;
    protected Class<? extends RingBuff> buffClass;

    protected float efficiency = 1.0f;

    private static final LinkedHashMap<String, Integer> gems = new LinkedHashMap<String, Integer>() {
        {
            put("garnet", ItemSpriteSheet.RING_GARNET);
            put("ruby", ItemSpriteSheet.RING_RUBY);
            put("topaz", ItemSpriteSheet.RING_TOPAZ);
            put("emerald", ItemSpriteSheet.RING_EMERALD);
            put("onyx", ItemSpriteSheet.RING_ONYX);
            put("opal", ItemSpriteSheet.RING_OPAL);
            put("tourmaline", ItemSpriteSheet.RING_TOURMALINE);
            put("sapphire", ItemSpriteSheet.RING_SAPPHIRE);
            put("amethyst", ItemSpriteSheet.RING_AMETHYST);
            put("quartz", ItemSpriteSheet.RING_QUARTZ);
            put("agate", ItemSpriteSheet.RING_AGATE);
            put("diamond", ItemSpriteSheet.RING_DIAMOND);
            put("jade", ItemSpriteSheet.RING_JADE);
            put("china", ItemSpriteSheet.RING_CHINA);
            put("crystal", ItemSpriteSheet.RING_CRYSTAL);
            put("glass", ItemSpriteSheet.RING_GLASS);

        }
    };

    private static ItemStatusHandler<Ring> handler;

    private String gem;

    //rings cannot be 'used' like other equipment, so they ID purely based on exp
    private float levelsToID = 1;

    @SuppressWarnings("unchecked")
    public static void initGems() {
        handler = new ItemStatusHandler<>((Class<? extends Ring>[]) Generator.Category.RING.classes, gems);
    }

    public static void clearGems() {
        handler = null;
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        handler.saveSelectively(bundle, items);
    }

    @SuppressWarnings("unchecked")
    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler<>((Class<? extends Ring>[]) Generator.Category.RING.classes, gems, bundle);
    }

    public Ring() {
        super();
        reset();

    }

    public static class PlaceHolder extends Ring {

        {
            image = ItemSpriteSheet.RING_HOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return item instanceof Ring;
        }

        @Override
        public String info() {
            return "";
        }
        // PlaceHolder inherits default efficiency from Ring
    }
    //anonymous rings are always IDed, do not affect ID status,
    //and their sprite is replaced by a placeholder if they are not known,
    //useful for items that appear in UIs, or which are only spawned for their effects
    protected boolean anonymous = false;

    public void anonymize() {
        if (!isKnown()) {
            image = ItemSpriteSheet.RING_HOLDER;
        }
        anonymous = true;
    }

    @Override
    public void reset() {
        // 调用父类的reset方法
        super.reset();
        levelsToID = 1;
        // 如果buffClass是Oldring类
        if (this.buffClass == Oldring.class) {
            // 设置image为ItemSpriteSheet.RING_VOID
            image = ItemSpriteSheet.RING_VOID;
            // 设置gem为"void"
            gem = "void";
            // 如果handler不为空且handler包含当前对象
        } else if (handler != null && handler.contains(this)) {
            // 设置image为handler.image(this)
            image = handler.image(this);
            // 设置gem为handler.label(this)
            gem = handler.label(this);
            // 否则
        } else {
            // 设置image为ItemSpriteSheet.RING_VOID
            image = ItemSpriteSheet.RING_VOID;
            // 设置gem为"void"
            gem = "void";
        }
        efficiency(1f);
    }

    @Override
    public void activate(Char ch) {
        if (buff != null) {
            buff.detach();
            buff = null;
        }
        buff = buff();
        buff.attachTo(ch);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {

            if (buff != null) {
                buff.detach();
                buff = null;
            }

            return true;

        } else {

            return false;

        }
    }

    public boolean isKnown() {
        return anonymous || (handler != null && handler.isKnown(this));
    }

    public void setKnown() {
        if (!anonymous) {
            if (!isKnown()) {
                handler.know(this);
            }

            if (Dungeon.hero.isAlive()) {
                Catalog.setSeen(getClass());
                Statistics.itemTypesDiscovered.add(getClass());
            }
        }
    }

    @Override
    public String name() {
        return isKnown() ? super.name() : Messages.get(Ring.class, gem);
    }

    @Override
    public String desc() {
        return isKnown() ? super.desc() : Messages.get(this, "unknown_desc");
    }

    @Override
    public String info() {

        //skip custom notes if anonymized and un-Ided
        String desc;
        if (anonymous && (handler == null || !handler.isKnown(this))) {
            desc = desc();

		} else {
			desc = super.info();
		}

        if (cursed && isEquipped(Dungeon.hero)) {
            desc += "\n\n" + Messages.get(Ring.class, "cursed_worn");

        } else if (cursed && cursedKnown) {
            desc += "\n\n" + Messages.get(Ring.class, "curse_known");

        } else if (!isIdentified() && cursedKnown) {
            desc += "\n\n" + Messages.get(Ring.class, "not_cursed");

        }

        if (isKnown()) {
            desc += "\n\n" + statsInfo() + "\n\n" + Messages.get(Ring.class, "efficiency", (int) (efficiency() * 100));
        }

        return desc;
    }

    protected String statsInfo() {
        return "";
    }

    public String upgradeStat1(int level) {
        return null;
    }

    public String upgradeStat2(int level) {
        return null;
    }

    public String upgradeStat3(int level) {
        return null;
    }

    @Override
    public Item upgrade() {
        // Note: isUpgradable() only returns true when the ring is cursed and curse is known.
        // Therefore this branch handles the "upgrade a cursed ring" case, which actually
        // removes the curse and may drop a bonus item, but does NOT increase the ring's level.
        if (cursed) {
            if (Random.Int(2) != 0) {
                if (Dungeon.level != null) {
                    Dungeon.level.drop(new StoneOfEnchantment(), Dungeon.hero.pos).sprite.drop();
                    Dungeon.level.drop(Generator.random(Category.STONE), Dungeon.hero.pos).sprite.drop();
                }
            } else {
                if (Dungeon.level != null) {
                    Dungeon.level.drop(Generator.random(Category.RING), Dungeon.hero.pos).sprite.drop();
                }
            }
            cursed = false;
            // Level is intentionally NOT increased here. The "upgrade" action on a cursed ring
            // acts as curse removal with a possible bonus drop.
        } else {
            super.upgrade();
        }
        return this;
    }

    @Override
    public boolean isIdentified() {
        if (this instanceof OldRing) {
            return true;
        }
        return super.isIdentified() && isKnown();
    }

    @Override
    public String status() {
        if (isIdentified() && efficiency() < 0.99) {
            return (int) (efficiency() * 100) + "%";
        } else {
            return null;
        }
    }

    @Override
    public Item identify(boolean byHero) {
        setKnown();
        levelsToID = 0;
        return super.identify(byHero);
    }

    public void setIDReady() {
        levelsToID = -1;
    }

    public boolean readyToIdentify() {
        return !isIdentified() && levelsToID <= 0;
    }

    // ————————生成的戒指等级————————
    @Override
    // 突破戒指等级上限，现在理论上可以获得任意等级的戒指，但概率递减
    public Item random() {
        int n = 0;
        while (Random.Int(n + 5) <= 3) {
            n++;
        }
        // n += 100;//为了方便调试留的，发布时应该删去
        level(n);

        //为了平衡，戒指被诅咒的概率受等级影响
        if (Random.Int(n + 5) <= n) {
            curse(true);
        }

        return this;
    }

    public static HashSet<Class<? extends Ring>> getKnown() {
        return handler.known();
    }

    public static HashSet<Class<? extends Ring>> getUnknown() {
        return handler.unknown();
    }

    public static boolean allKnown() {
        return handler != null && handler.known().size() == Generator.Category.RING.classes.length;
    }

    @Override
    public int value() {
        int price = 75;
        if (cursed && cursedKnown) {
            price /= 2;
        }
        if (levelKnown) {
            if (level() > 0) {
                price *= (level() + 1);
            } else if (level() < 0) {
                price /= (1 - level());
            }
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

    protected RingBuff buff() {
        return null;
    }

    private static final String LEVELS_TO_ID = "levels_to_ID";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVELS_TO_ID, levelsToID);
        bundle.put("efficiency", efficiency());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        levelsToID = bundle.getFloat(LEVELS_TO_ID);
        efficiency(bundle.getFloat("efficiency"));
    }

    @Override
    public void onHeroGainExp(float levelPercent, Hero hero) {
        if (isIdentified() || !isEquipped(hero)) {
            return;
        }
        levelPercent *= Talent.itemIDSpeedFactor(hero, this);
        //becomes IDed after 1 level
        levelsToID -= levelPercent;
        if (levelsToID <= 0) {
            if (ShardOfOblivion.passiveIDDisabled()) {
                if (levelsToID > -1) {
                    GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), name());
                }
                setIDReady();
            } else {
                identify();
                GLog.p(Messages.get(Ring.class, "identify"));
                Badges.validateItemLevelAquired(this);
            }
        }
    }

    @Override
    public int buffedLvl() {
        int lvl = super.buffedLvl();
        if (Dungeon.hero.buff(EnhancedRings.class) != null) {
            lvl++;
        }
        return lvl;
    }

    public static int getBonus(Char target, Class<? extends RingBuff> type) {
        if (target.buff(MagicImmune.class) != null) {
            return 0;
        }
        int bonus = 0;
        for (RingBuff buff : target.buffs(type)) {
            bonus += buff.level();
        }
        SpiritForm.SpiritFormBuff spiritForm = target.buff(SpiritForm.SpiritFormBuff.class);
        if (bonus == 0
                && spiritForm != null
                && spiritForm.ring() != null
                && spiritForm.ring().buffClass == type) {
            bonus += spiritForm.ring().soloBonus();
        }
        return bonus;
    }

    public static int getBuffedBonus(Char target, Class<? extends RingBuff> type) {
        if (target.buff(MagicImmune.class) != null) {
            return 0;
        }
        int bonus = 0;
        for (RingBuff buff : target.buffs(type)) {
            bonus += buff.buffedLvl();
        }
        if (bonus == 0
                && target.buff(SpiritForm.SpiritFormBuff.class) != null
                && target.buff(SpiritForm.SpiritFormBuff.class).ring() != null
                && target.buff(SpiritForm.SpiritFormBuff.class).ring().buffClass == type) {
            bonus += target.buff(SpiritForm.SpiritFormBuff.class).ring().soloBuffedBonus();
        }
        return bonus;
    }

    //just used for ring descriptions
    public int soloBonus() {
        if (cursed) {
            return Math.min(0, -Ring.this.level());
        } else {
            return Ring.this.level() + 1;
        }
    }

    //just used for ring descriptions
    public int soloBuffedBonus() {
        if (cursed) {
            return Math.min(0, -Ring.this.buffedLvl());
        } else {
            return Ring.this.buffedLvl() + 1;
        }
    }

    //just used for ring descriptions
    public int combinedBonus(Hero hero) {
        int bonus = 0;
        for (Ring r : hero.belongings.getEquippedRings()) {
            if (r.getClass() == getClass()) {
                bonus += r.soloBonus();
            }
        }
        return bonus;
    }

    //just used for ring descriptions
    public int combinedBuffedBonus(Hero hero) {
        int bonus = 0;
        for (Ring r : hero.belongings.getEquippedRings()) {
            if (r.getClass() == getClass()) {
                bonus += r.soloBuffedBonus();
            }
        }
        return bonus;
    }

    @Override
    public boolean isUpgradable() {

        return cursedKnown && cursed;
    }

    // ————————————————效率相关代码————————————————
    public float efficiency() {
        return efficiency;
    }

    public void efficiency(float value) {
        value = value > 1 ? 1 : value;
        value = value < 0 ? 0 : value;
        this.efficiency = value;
    }

    public void refresh() {
        efficiency(1.0f);
    }

    public void efficiency_multy(float value) {
        efficiency(efficiency() * value);
    }

    public static float getAverageEfficiency(Char target, Class<? extends RingBuff> buffClass) {
        float weightedTotal = 0;
        float totalWeight = 0;
        for (RingBuff buff : target.buffs(buffClass)) {
            float weight = Math.max(1, buff.buffedLvl());
            weightedTotal += buff.getRing().efficiency() * weight;
            totalWeight += weight;
        }
        return totalWeight > 0 ? weightedTotal / totalWeight : 1.0f;
    }

    public static int countEquippedRingsOfType(Hero hero, Class<? extends Ring> type) {
        int count = 0;
        for (Ring r : hero.belongings.getEquippedRings()) {
            if (r.getClass() == type) {
                count++;
            }
        }
        return count;
    }

    public static void refreshAllEquippedOfType(Hero hero, Class<? extends Ring> type) {
        for (Ring r : hero.belongings.getEquippedRings()) {
            if (r.getClass() == type) {
                r.refresh();
            }
        }
    }

    public static void multyEfficiencyAllEquippedOfType(Hero hero, Class<? extends Ring> type, float value) {
        for (Ring r : hero.belongings.getEquippedRings()) {
            if (r.getClass() == type) {
                r.efficiency_multy(value);
            }
        }
    }

    // Called by RingBuff.act() each tick. Only trigger-type rings (e.g. Takeout, Discount)
    // override this to perform per-tick logic. Return value is the time to spend.
    protected float tick() {
        return Actor.TICK;
    }

    // ———————————————————————————————————————————
    public class RingBuff extends Buff {

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                //if we're loading in and the hero has partially spent a turn, delay for 1 turn
                if (target instanceof Hero && Dungeon.hero == null && cooldown() == 0 && target.cooldown() > 0) {
                    spend(TICK);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean act() {
            float time = Ring.this.tick();
            spend(time);
            return true;
        }

        public int level() {
            return Ring.this.soloBonus();
        }

        public int buffedLvl() {
            return Ring.this.soloBuffedBonus();
        }

        public Ring getRing() {
            return Ring.this;
        }
    }
}
