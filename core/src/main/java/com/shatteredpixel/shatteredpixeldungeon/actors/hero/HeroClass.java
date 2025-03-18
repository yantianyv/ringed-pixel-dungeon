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
package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import java.util.List;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.Trinity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.RingString;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfDefender;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfKungfu;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTimetraveler;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMagicshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTakeout;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDeveloper;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPassage;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cudgel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant.Seed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.watabou.utils.DeviceCompat;

public enum HeroClass {

    WARRIOR(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR),
    MAGE(HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK),
    ROGUE(HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER),
    HUNTRESS(HeroSubClass.SNIPER, HeroSubClass.WARDEN),
    DUELIST(HeroSubClass.CHAMPION, HeroSubClass.MONK),
    CLERIC(HeroSubClass.PRIEST, HeroSubClass.PALADIN);

    private HeroSubClass[] subClasses;
    public boolean develop_mode = false;

    HeroClass(HeroSubClass... subClasses) {
        this.subClasses = subClasses;
    }

    public void initHero(Hero hero) {

        hero.heroClass = this;
        Talent.initClassTalents(hero);

        Item i = new ClothArmor().identify();
        if (!Challenges.isItemBlocked(i)) {
            hero.belongings.armor = (ClothArmor) i;
        }

        i = new Food();
        if (!Challenges.isItemBlocked(i)) {
            i.collect();
        }

        new VelvetPouch().collect();
        Dungeon.LimitedDrops.VELVET_POUCH.drop();

        Waterskin waterskin = new Waterskin();
        waterskin.collect();

        new ScrollOfIdentify().identify();

        switch (this) {
            case WARRIOR:
                initWarrior(hero);
                break;

            case MAGE:
                initMage(hero);
                break;

            case ROGUE:
                initRogue(hero);
                break;

            case HUNTRESS:
                initHuntress(hero);
                break;

            case DUELIST:
                initDuelist(hero);
                break;

            case CLERIC:
                initCleric(hero);
                break;
        }
        if (Dungeon.is_developer_mode()) {
            develop_mode(); // 初始化开发者模式
        }
        if (SPDSettings.quickslotWaterskin()) {
            for (int s = 0; s < QuickSlot.SIZE; s++) {
                if (Dungeon.quickslot.getItem(s) == null) {
                    Dungeon.quickslot.setSlot(s, waterskin);
                    break;
                }
            }
        }

    }

    public Badges.Badge masteryBadge() {
        switch (this) {
            case WARRIOR:
                return Badges.Badge.MASTERY_WARRIOR;
            case MAGE:
                return Badges.Badge.MASTERY_MAGE;
            case ROGUE:
                return Badges.Badge.MASTERY_ROGUE;
            case HUNTRESS:
                return Badges.Badge.MASTERY_HUNTRESS;
            case DUELIST:
                return Badges.Badge.MASTERY_DUELIST;
            case CLERIC:
                return Badges.Badge.MASTERY_CLERIC;
        }
        return null;
    }
    // ————————————————角色初始化————————————————

    private static void initWarrior(Hero hero) {    // 战士
        // 武器
        (hero.belongings.weapon = new WornShortsword()).identify();
        // 护符
        if (hero.belongings.armor != null) {
            hero.belongings.armor.affixSeal(new BrokenSeal());
            Catalog.setSeen(BrokenSeal.class); //as it's not added to the inventory
        }
        // 投武
        ThrowingStone stones = new ThrowingStone();
        stones.quantity(3).collect();
        // 戒指
        (hero.belongings.ring1 = new RingOfTakeout()).identify();
        // 装进包里
        Dungeon.quickslot.setSlot(0, stones);
        // 鉴定
        new PotionOfHealing().identify();
        new ScrollOfRage().identify();
    }

    private static void initMage(Hero hero) {   //法师
        // 武器
        MagesStaff staff;
        staff = new MagesStaff(new WandOfMagicMissile());
        (hero.belongings.weapon = staff).identify();
        hero.belongings.weapon.activate(hero);
        // 戒指
        (hero.belongings.ring1 = new RingOfEnergy()).identify();
        // 装进包里
        Dungeon.quickslot.setSlot(0, staff);
        // 鉴定
        new ScrollOfUpgrade().identify();
        new PotionOfLiquidFlame().identify();

    }

    private static void initRogue(Hero hero) {  //  盗贼
        // 武器
        (hero.belongings.weapon = new Dagger()).identify();
        // 神器
        CloakOfShadows cloak = new CloakOfShadows();
        (hero.belongings.artifact = cloak).identify();
        hero.belongings.artifact.activate(hero);
        // 投武
        ThrowingKnife knives = new ThrowingKnife();
        knives.quantity(3).collect();
        // 戒指
        (hero.belongings.ring1 = new RingOfTimetraveler()).identify();
        // 装进包里
        Dungeon.quickslot.setSlot(0, cloak);
        Dungeon.quickslot.setSlot(1, knives);
        // 鉴定
        new ScrollOfMagicMapping().identify();
        new PotionOfInvisibility().identify();

    }

    private static void initHuntress(Hero hero) {   // 女猎
        // 武器
        (hero.belongings.weapon = new Gloves()).identify();
        // 灵能弓
        SpiritBow bow = new SpiritBow();
        bow.identify().collect();
        // 戒指
        (hero.belongings.ring1 = new RingOfMagicshooting()).identify();
        // 装进包里
        Dungeon.quickslot.setSlot(0, bow);
        // 鉴定
        new PotionOfMindVision().identify();
        new ScrollOfLullaby().identify();
    }

    private static void initDuelist(Hero hero) {    //  决斗家
        // 武器
        (hero.belongings.weapon = new Rapier()).identify();
        hero.belongings.weapon.activate(hero);
        // 投武
        ThrowingSpike spikes = new ThrowingSpike();
        spikes.quantity(2).collect();
        // 戒指
        (hero.belongings.ring1 = new RingOfKungfu()).identify();
        // 装进包里
        Dungeon.quickslot.setSlot(0, hero.belongings.weapon);
        Dungeon.quickslot.setSlot(1, spikes);
        //  鉴定
        new PotionOfStrength().identify();
        new ScrollOfMirrorImage().identify();
    }

    private static void initCleric(Hero hero) {    // 牧师
        // 武器
        (hero.belongings.weapon = new Cudgel()).identify();
        hero.belongings.weapon.activate(hero);
        // 神器
        HolyTome tome = new HolyTome();
        (hero.belongings.artifact = tome).identify();
        hero.belongings.artifact.activate(hero);
        // 戒指
        (hero.belongings.ring1 = new RingOfDefender()).identify();
        // 装进包里
        Dungeon.quickslot.setSlot(0, tome);
        // 鉴定
        new PotionOfPurity().identify();
        new ScrollOfRemoveCurse().identify();

    }
    // ——————————————————————————————————————————

    public String title() {
        return Messages.get(HeroClass.class, name());
    }

    public String desc() {
        return Messages.get(HeroClass.class, name() + "_desc");
    }

    public String shortDesc() {
        return Messages.get(HeroClass.class, name() + "_desc_short");
    }

    public HeroSubClass[] subClasses() {
        return subClasses;
    }

    public ArmorAbility[] armorAbilities() {
        switch (this) {
            case WARRIOR:
            default:
                return new ArmorAbility[]{new HeroicLeap(), new Shockwave(), new Endure()};
            case MAGE:
                return new ArmorAbility[]{new ElementalBlast(), new WildMagic(), new WarpBeacon()};
            case ROGUE:
                return new ArmorAbility[]{new SmokeBomb(), new DeathMark(), new ShadowClone()};
            case HUNTRESS:
                return new ArmorAbility[]{new SpectralBlades(), new NaturesPower(), new SpiritHawk()};
            case DUELIST:
                return new ArmorAbility[]{new Challenge(), new ElementalStrike(), new Feint()};
            case CLERIC:
                return new ArmorAbility[]{new AscendedForm(), new Trinity(), new PowerOfMany()};
        }
    }

    public String spritesheet() {
        switch (this) {
            case WARRIOR:
            default:
                return Assets.Sprites.WARRIOR;
            case MAGE:
                return Assets.Sprites.MAGE;
            case ROGUE:
                return Assets.Sprites.ROGUE;
            case HUNTRESS:
                return Assets.Sprites.HUNTRESS;
            case DUELIST:
                return Assets.Sprites.DUELIST;
            case CLERIC: //TODO CLERIC finish sprite sheet
                return Assets.Sprites.CLERIC;
        }
    }

    public String splashArt() {
        switch (this) {
            case WARRIOR:
            default:
                return Assets.Splashes.WARRIOR;
            case MAGE:
                return Assets.Splashes.MAGE;
            case ROGUE:
                return Assets.Splashes.ROGUE;
            case HUNTRESS:
                return Assets.Splashes.HUNTRESS;
            case DUELIST:
                return Assets.Splashes.DUELIST;
            case CLERIC: //TODO CLERIC finish cleric splash
                return Assets.Splashes.CLERIC;
        }
    }

    public boolean isUnlocked() {
        //always unlock on debug builds
        if (DeviceCompat.isDebug()) {
            return true;
        }

        switch (this) {
            case WARRIOR:
            default:
                return true;
            case MAGE:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
            case ROGUE:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
            case HUNTRESS:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
            case DUELIST:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_DUELIST);
            case CLERIC:
                return Badges.isUnlocked(Badges.Badge.UNLOCK_CLERIC);
        }
    }

    public String unlockMsg() {
        return shortDesc() + "\n\n" + Messages.get(HeroClass.class, name() + "_unlock");
    }

// 开发者模式
    private void develop_mode() {
        // 获得所有背包
        Item ScrollHolder = new ScrollHolder();
        ScrollHolder.collect();
        Item PotionBandolier = new PotionBandolier();
        PotionBandolier.collect();
        Item MagicalHolster = new MagicalHolster();
        MagicalHolster.collect();
        Item RingString = new RingString();
        RingString.collect();
        // 获得所有戒指
        List rings = Generator.getItemList(Generator.Category.RING);
        for (int i = 0; i < rings.size(); i++) {
            Item ring = (Item) rings.get(i);
            if (ring instanceof Ring) {
                ring.upgrade(100);
                ring.identify();
                ring.collect();
                ring.cursed = true;
            }
        }
        // 获得所有戒指的诅咒版
        List cursedRings = Generator.getItemList(Generator.Category.RING);
        for (int i = 0; i < cursedRings.size(); i++) {
            Item cursedRing = (Item) cursedRings.get(i);
            if (cursedRing instanceof Ring) {
                cursedRing.upgrade(10);
                cursedRing.identify();
                cursedRing.collect();
                cursedRing.cursed = true;
            }
        }
        // 获得每种神器各一个
        List artifacts = Generator.getItemList(Generator.Category.ARTIFACT);
        for (int i = 0; i < artifacts.size(); i++) {
            Item artifact = (Item) artifacts.get(i);
            if (artifact instanceof Artifact) {
                artifact.identify();
                artifact.cursed = true;
                artifact.collect();
            }
        }
        //获得每种法杖100级
        List wands = Generator.getItemList(Generator.Category.WAND);
        for (int i = 0; i < wands.size(); i++) {
            Item wand = (Item) wands.get(i);
            if (wand instanceof Wand) {
                wand.upgrade(100);
                wand.identify();
                wand.collect();
            }
        }
        // 获得每种药水
        for (int j = 0; j < 6666; j++) {
            List<Item> potions = Generator.getItemList(Generator.Category.POTION);
            for (int i = 0; i < potions.size(); i++) {
                Item potion = (Item) potions.get(i);
                if (potion instanceof Potion) {
                    potion.identify();
                    potion.collect();
                }
            }
        }
        // 获得每种卷轴
        for (int j = 0; j < 6666; j++) {
            List<Item> scrolls = Generator.getItemList(Generator.Category.SCROLL);
            for (int i = 0; i < scrolls.size(); i++) {
                Item scroll = (Item) scrolls.get(i);
                if (scroll instanceof Scroll) {
                    scroll.identify();
                    scroll.collect();
                }
            }
        }
        // 获得每种种子
        for (int j = 0; j < 6666; j++) {
            List<Item> seeds = Generator.getItemList(Generator.Category.SEED);
            for (int i = 0; i < seeds.size(); i++) {
                Item the_seed = (Item) seeds.get(i);
                if (the_seed instanceof Seed) {
                    the_seed.collect();
                }
            }
        }
        // 获得每种TRINKET
        List trinkets = Generator.getItemList(Generator.Category.TRINKET);
        for (int i = 0; i < trinkets.size(); i++) {
            Item trinket = (Item) trinkets.get(i);
            trinket.identify();
            trinket.collect();
        }
        // 获得每种食物10000个           
        for (int j = 0; j < 6666; j++) {
            List foods = Generator.getItemList(Generator.Category.FOOD);
            for (int i = 0; i < foods.size(); i++) {
                Item food = (Item) foods.get(i);
                if (food instanceof Food) {
                    food.collect();
                }
            }
        }
        // 获得升级卷轴10000个
        for (int i = 0; i < (114514 - 6666); i++) {
            ScrollOfUpgrade scroll1 = new ScrollOfUpgrade();
            scroll1.collect();
        }
        // 获得归反密卷10000个
        for (int i = 0; i < (100); i++) {
            ScrollOfPassage scroll2 = new ScrollOfPassage();
            scroll2.collect();
        }
        // 获得开发者秘卷10000个
        for (int i = 0; i < (100); i++) {
            ScrollOfDeveloper scroll3 = new ScrollOfDeveloper();
            scroll3.collect();
        }

        // 把一个升级卷轴放入第一个快捷栏
        Item slot0 = Dungeon.hero.belongings.getItem(ScrollOfUpgrade.class);
        if (slot0 != null) {
            Dungeon.quickslot.setSlot(0, slot0);
        }
        // 把一个驱邪卷轴放入第二个快捷栏
        Item slot1 = Dungeon.hero.belongings.getItem(ScrollOfRemoveCurse.class);
        if (slot1 != null) {
            Dungeon.quickslot.setSlot(1, slot1);
        }
        // 把一个经验药水放入第三个快捷栏
        Item slot2 = Dungeon.hero.belongings.getItem(PotionOfExperience.class);
        if (slot2 != null) {
            Dungeon.quickslot.setSlot(2, slot2);
        }
        // 把一个力量药水放入第四个快捷栏
        Item slot3 = Dungeon.hero.belongings.getItem(PotionOfStrength.class);
        if (slot3 != null) {
            Dungeon.quickslot.setSlot(3, slot3);
        }
        // 把一个断肠苔种子放入第五个快捷栏
        Item slot4 = Dungeon.hero.belongings.getItem(Sorrowmoss.Seed.class);
        if (slot4 != null) {
            Dungeon.quickslot.setSlot(4, slot4);
        }
        // 把开发者密卷放入第六个快捷栏
        Item slot5 = Dungeon.hero.belongings.getItem(ScrollOfDeveloper.class);
        if (slot5 != null) {
            Dungeon.quickslot.setSlot(5, slot5);
        }

    }
}
