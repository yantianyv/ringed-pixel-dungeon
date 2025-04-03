/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
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
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Ring extends KindofMisc {

    protected Buff buff;
    protected Class<? extends RingBuff> buffClass;

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
        super.reset();
        levelsToID = 1;
        if (this.buffClass == Oldring.class) {
            image = ItemSpriteSheet.RING_VOID;
            gem = "void";
        } else if (handler != null && handler.contains(this)) {
            image = handler.image(this);
            gem = handler.label(this);
        } else {
            image = ItemSpriteSheet.RING_VOID;
            gem = "void";
        }
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

            //otherwise, check for item type note, rings can have either but not both
        } else if (Notes.findCustomRecord(customNoteID) == null) {
            Notes.CustomRecord note = Notes.findCustomRecord(getClass());
            if (note != null) {
                //we swap underscore(0x5F) with low macron(0x2CD) here to avoid highlighting in the item window
                desc = Messages.get(this, "custom_note", note.title().replace('_', 'ˍ')) + "\n\n" + super.info();
            } else {
                desc = super.info();
            }
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
            desc += "\n\n" + statsInfo();
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
        if (cursed == true) {
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

        } else {
            super.upgrade();

        }

        return this;
    }

    @Override
    public boolean isIdentified() {
        return super.isIdentified() && isKnown();
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
            cursed = true;
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
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        levelsToID = bundle.getFloat(LEVELS_TO_ID);
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
        if (hero.belongings.ring6() != null && hero.belongings.ring6().getClass() == getClass()) {
            bonus += hero.belongings.ring6().soloBonus();
        }
        if (hero.belongings.ring5() != null && hero.belongings.ring5().getClass() == getClass()) {
            bonus += hero.belongings.ring5().soloBonus();
        }
        if (hero.belongings.ring4() != null && hero.belongings.ring4().getClass() == getClass()) {
            bonus += hero.belongings.ring4().soloBonus();
        }
        if (hero.belongings.ring3() != null && hero.belongings.ring3().getClass() == getClass()) {
            bonus += hero.belongings.ring3().soloBonus();
        }
        if (hero.belongings.ring2() != null && hero.belongings.ring2().getClass() == getClass()) {
            bonus += hero.belongings.ring2().soloBonus();
        }
        if (hero.belongings.ring1() != null && hero.belongings.ring1().getClass() == getClass()) {
            bonus += hero.belongings.ring1().soloBonus();
        }
        if (hero.belongings.misc() != null && hero.belongings.misc().getClass() == getClass()) {
            bonus += ((Ring) hero.belongings.misc()).soloBonus();
        }
        return bonus;
    }

    //just used for ring descriptions
    public int combinedBuffedBonus(Hero hero) {
        int bonus = 0;
        if (hero.belongings.ring6() != null && hero.belongings.ring6().getClass() == getClass()) {
            bonus += hero.belongings.ring6().soloBuffedBonus();
        }
        if (hero.belongings.ring5() != null && hero.belongings.ring5().getClass() == getClass()) {
            bonus += hero.belongings.ring5().soloBuffedBonus();
        }
        if (hero.belongings.ring4() != null && hero.belongings.ring4().getClass() == getClass()) {
            bonus += hero.belongings.ring4().soloBuffedBonus();
        }
        if (hero.belongings.ring3() != null && hero.belongings.ring3().getClass() == getClass()) {
            bonus += hero.belongings.ring3().soloBuffedBonus();
        }
        if (hero.belongings.ring2() != null && hero.belongings.ring2().getClass() == getClass()) {
            bonus += hero.belongings.ring2().soloBuffedBonus();
        }
        if (hero.belongings.ring1() != null && hero.belongings.ring1().getClass() == getClass()) {
            bonus += hero.belongings.ring1().soloBuffedBonus();
        }
        if (hero.belongings.misc() != null && hero.belongings.misc().getClass() == getClass()) {
            bonus += ((Ring) hero.belongings.misc()).soloBuffedBonus();
        }
        return bonus;
    }

    @Override
    public boolean isUpgradable() {

        return cursedKnown && cursed;
    }

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
            spend(TICK);
            return true;
        }

        public int level() {
            return Ring.this.soloBonus();
        }

        public int buffedLvl() {
            return Ring.this.soloBuffedBonus();
        }

    }
}
