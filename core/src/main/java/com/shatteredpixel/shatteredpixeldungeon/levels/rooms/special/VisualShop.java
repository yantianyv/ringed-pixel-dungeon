package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.OriginGem;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.watabou.utils.Random;

public class VisualShop extends ShopRoom {

    protected static ArrayList<Item> generateItems() {

        ArrayList<Item> itemsToSpawn = new ArrayList<>();
        MeleeWeapon w;
        switch (Random.Int(4)) {
            case 0:
            default:
                w = (MeleeWeapon) Generator.random(Generator.wepTiers[1]);
                itemsToSpawn.add(Generator.random(Generator.misTiers[1]).quantity(2).identify(false));
                itemsToSpawn.add(new LeatherArmor().identify(false));
                break;

            case 1:
                w = (MeleeWeapon) Generator.random(Generator.wepTiers[2]);
                itemsToSpawn.add(Generator.random(Generator.misTiers[2]).quantity(2).identify(false));
                itemsToSpawn.add(new MailArmor().identify(false));
                break;

            case 2:
                w = (MeleeWeapon) Generator.random(Generator.wepTiers[3]);
                itemsToSpawn.add(Generator.random(Generator.misTiers[3]).quantity(2).identify(false));
                itemsToSpawn.add(new ScaleArmor().identify(false));
                break;

            case 3:
                w = (MeleeWeapon) Generator.random(Generator.wepTiers[4]);
                itemsToSpawn.add(Generator.random(Generator.misTiers[4]).quantity(2).identify(false));
                itemsToSpawn.add(new PlateArmor().identify(false));
                itemsToSpawn.add(new Torch());
                itemsToSpawn.add(new Torch());
                itemsToSpawn.add(new Torch());
                break;
        }
        w.enchant(null);
        w.cursed = false;
        w.level(0);
        w.identify(false);
        itemsToSpawn.add(w);
        itemsToSpawn.add(Generator.random(Generator.Category.RING));
        itemsToSpawn.add(new OriginGem().quantity(3));
        itemsToSpawn.add(new OriginGem().quantity(2));
        itemsToSpawn.add(new OriginGem().quantity(1));
        itemsToSpawn.add(TippedDart.randomTipped(2));

        itemsToSpawn.add(new Alchemize().quantity(Random.IntRange(2, 3)));

        itemsToSpawn.add(new PotionOfHealing());
        itemsToSpawn.add(Generator.randomUsingDefaults(Generator.Category.POTION));
        itemsToSpawn.add(Generator.randomUsingDefaults(Generator.Category.POTION));

        itemsToSpawn.add(new ScrollOfIdentify());
        itemsToSpawn.add(new ScrollOfRemoveCurse());
        itemsToSpawn.add(new ScrollOfMagicMapping());

        for (int i = 0; i < 2; i++) {
            itemsToSpawn.add(Random.Int(2) == 0
                    ? Generator.randomUsingDefaults(Generator.Category.POTION)
                    : Generator.randomUsingDefaults(Generator.Category.SCROLL));
        }

        itemsToSpawn.add(new SmallRation());
        itemsToSpawn.add(new SmallRation());

        switch (Random.Int(4)) {
            case 0:
                itemsToSpawn.add(new Bomb());
                break;
            case 1:
            case 2:
                itemsToSpawn.add(new Bomb.DoubleBomb());
                break;
            case 3:
                itemsToSpawn.add(new Honeypot());
                break;
        }

        // 检查玩家背包中是否已经存在 Ankh
        int ankh_quantity = Dungeon.hero.belongings.getAllSimilar(new Ankh()).size();
        if (Random.Float(1) < Math.pow(0.2, ankh_quantity)) {
            itemsToSpawn.add(new Ankh());
        }
        itemsToSpawn.add(new StoneOfAugmentation());

        TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
        if (hourglass != null && hourglass.isIdentified() && !hourglass.cursed) {
            //creates the given float percent of the remaining bags to be dropped.
            //this way players who get the hourglass late can still max it, usually.

            itemsToSpawn.add(new TimekeepersHourglass.sandBag());
            hourglass.sandBags++;
        }

        Item rare;
        switch (Random.Int(10)) {
            case 0:
                rare = Generator.random(Generator.Category.WAND);
                rare.level(0);
                break;
            case 1:
                rare = Generator.random(Generator.Category.RING);
                rare.level(0);
                break;
            default:
                rare = new Stylus();
        }
        rare.cursed = false;
        rare.cursedKnown = true;
        itemsToSpawn.add(rare);

        //use a new generator here to prevent items in shop stock affecting levelgen RNG (e.g. sandbags)
        //we can use a random long for the seed as it will be the same long every time
        Random.pushGenerator(Random.Long());
        Random.shuffle(itemsToSpawn);
        Random.popGenerator();

        return itemsToSpawn;
    }

    public Item chooseRandom() {
        ArrayList<Item> goods = generateItems();
        return goods.get(Random.Int(goods.size()));
    }
}
