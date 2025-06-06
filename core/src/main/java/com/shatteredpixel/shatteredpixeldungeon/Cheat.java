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
package com.shatteredpixel.shatteredpixeldungeon;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth.DropsToEquipTracker;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth.TriesToDropTracker;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth.Wealth;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Cheat {

    // Define the cheat ID
    public static final int UNLOCK_CHEAT = 4096;

    // Add this cheat to the list of challenges
    public static final int MAX_VALUE = 6143;

    public static final String[] NAME_IDS = {
        "champion_enemies",
        "stronger_bosses",
        "no_food",
        "no_armor",
        "no_healing",
        "no_herbalism",
        "swarm_intelligence",
        "darkness",
        "no_scrolls",
        "invisible_war",
        "unlock_cheat"
    };

    public static final int[] MASKS = {
        Challenges.CHAMPION_ENEMIES, Challenges.STRONGER_BOSSES, Challenges.NO_FOOD, Challenges.NO_ARMOR, Challenges.NO_HEALING, Challenges.NO_HERBALISM, Challenges.SWARM_INTELLIGENCE, Challenges.DARKNESS, Challenges.NO_SCROLLS, Challenges.INVISIBLE_WAR, UNLOCK_CHEAT
    };

    public static boolean isCheatUnlocked() {
        return (Dungeon.challenges & UNLOCK_CHEAT) != 0;
    }

    public static void applyCheat(Char target) {
        if (isCheatUnlocked()) {
            // Apply cheat logic here
            // For example, unlock all items, give unlimited experience, etc.
            // Placeholder logic for demonstration
            GLog.i(Messages.get(Cheat.class, "cheat_applied"));
        }
    }

    public static void applyCheatToAllMobs() {
        if (isCheatUnlocked()) {
            for (Mob mob : Dungeon.level.mobs) {
                applyCheat(mob);
            }
        }
    }

    public static void applyCheatToHero() {
        if (isCheatUnlocked()) {
            Hero hero = Dungeon.hero;
            applyCheat(hero);
            applyXPDungeon(hero);
        }
    }

    public static void applyCheatToLoot(Item loot) {
        if (isCheatUnlocked()) {
            // Modify loot logic here
            // Placeholder logic for demonstration
            GLog.i(Messages.get(Cheat.class, "cheat_applied_loot"));
        }
    }

    public static void applyCheatToRingOfWealth(Hero hero) {
        if (isCheatUnlocked()) {
            // Apply cheat logic specifically to RingOfWealth
            // Placeholder logic for demonstration
            GLog.i(Messages.get(Cheat.class, "cheat_applied_ring_of_wealth"));

            // Example: Increase the number of tries to drop and drops to rare
            TriesToDropTracker triesToDrop = hero.buff(TriesToDropTracker.class);
            if (triesToDrop == null) {
                triesToDrop = Buff.affect(hero, TriesToDropTracker.class);
            }
            triesToDrop.countUp(100); // Increase tries to drop significantly

            DropsToEquipTracker dropsToEquip = hero.buff(DropsToEquipTracker.class);
            if (dropsToEquip == null) {
                dropsToEquip = Buff.affect(hero, DropsToEquipTracker.class);
            }
            dropsToEquip.countUp(100); // Increase drops to equip significantly

            // Example: Increase the bonus drop chance multiplier
            int bonus = RingOfWealth.getBuffedBonus(hero, Wealth.class);
            if (bonus > 0) {
                ArrayList<Item> bonusDrops = RingOfWealth.tryForBonusDrop(hero, 100); // Increase rolls for bonus drops
                if (bonusDrops != null && !bonusDrops.isEmpty()) {
                    for (Item b : bonusDrops) {
                        Dungeon.level.drop(b, hero.pos).sprite.drop();
                    }
                    RingOfWealth.showFlareForBonusDrop(hero.sprite);
                }
            }
        }
    }

    public static void applyXPDungeon(Hero hero) {
        if (isCheatUnlocked()) {
            // Apply xp_dungeon logic here
            // For example, give the hero unlimited experience
            // Placeholder logic for demonstration
            GLog.i(Messages.get(Cheat.class, "xp_dungeon_applied"));

            // Example: Give the hero a large amount of experience
            hero.earnExp(1000000, Cheat.class);
        }
    }
}
