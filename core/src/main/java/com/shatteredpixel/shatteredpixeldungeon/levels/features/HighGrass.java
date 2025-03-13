package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Viper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Berry;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfNahida;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.PetrifiedSeed;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class HighGrass {

    //prevents items dropped from grass, from trampling that same grass.
    //yes this is a bit ugly, oh well.
    private static boolean freezeTrample = false;

    public static void trample(Level level, int pos) {

        if (freezeTrample) {
            return;
        }

        Char ch = Actor.findChar(pos);

        if (level.map[pos] == Terrain.FURROWED_GRASS) {
            if (ch instanceof Hero && ((Hero) ch).heroClass == HeroClass.HUNTRESS) {
                //Do nothing
                freezeTrample = true;
            } else {
                Level.set(pos, Terrain.GRASS);
            }

        } else {
            if (ch instanceof Hero && ((Hero) ch).heroClass == HeroClass.HUNTRESS) {
                Level.set(pos, Terrain.FURROWED_GRASS);
                freezeTrample = true;
            } else {
                Level.set(pos, Terrain.GRASS);
            }

            int naturalismLevel = 0;

            if (ch != null) {
                SandalsOfNature.Naturalism naturalism = ch.buff(SandalsOfNature.Naturalism.class);
                if (naturalism != null) {
                    if (!naturalism.isCursed()) {
                        naturalismLevel = naturalism.itemLevel() + 1;
                        naturalism.charge();
                    } else {
                        naturalismLevel = -1;
                    }
                }

                //berries try to drop on floors 2/3/4/6/7/8, to a max of 4/6
                if (ch instanceof Hero && ((Hero) ch).hasTalent(Talent.NATURES_BOUNTY)) {
                    int berriesAvailable = 2 + 2 * ((Hero) ch).pointsInTalent(Talent.NATURES_BOUNTY);

                    Talent.NatureBerriesDropped dropped = Buff.affect(ch, Talent.NatureBerriesDropped.class);
                    berriesAvailable -= dropped.count();

                    if (berriesAvailable > 0) {
                        int targetFloor = 2 + 2 * ((Hero) ch).pointsInTalent(Talent.NATURES_BOUNTY);
                        targetFloor -= berriesAvailable;
                        targetFloor += (targetFloor >= 5) ? 3 : 2;

                        //If we're behind: 1/10, if we're on page: 1/30, if we're ahead: 1/90
                        boolean droppingBerry = false;
                        if (Dungeon.depth > targetFloor) {
                            droppingBerry = Random.Int(10) == 0;
                        } else if (Dungeon.depth == targetFloor) {
                            droppingBerry = Random.Int(30) == 0;
                        } else if (Dungeon.depth < targetFloor) {
                            droppingBerry = Random.Int(90) == 0;
                        }

                        if (droppingBerry) {
                            dropped.countUp(1);
                            level.drop(new Berry(), pos).sprite.drop();
                        }
                    }

                }
            }

            //grass gives 1/3 the normal amount of loot in fungi level
            if (Dungeon.level instanceof MiningLevel
                    && Blacksmith.Quest.Type() == Blacksmith.Quest.FUNGI
                    && Random.Int(3) != 0) {
                naturalismLevel = -1;
            }

            if (naturalismLevel >= 0) {
                // Seed, scales from 1/25 to 1/9
                float lootChance = 1 / (25f - naturalismLevel * 4f);

                // absolute max drop rate is ~1/6.5 with footwear of nature, ~1/18 without
                lootChance *= PetrifiedSeed.grassLootMultiplier();

                if (Random.Float() < lootChance) {
                    if (Random.Float() < PetrifiedSeed.stoneInsteadOfSeedChance()) {
                        level.drop(Generator.randomUsingDefaults(Generator.Category.STONE), pos).sprite.drop();
                    } else {
                        level.drop(Generator.random(Generator.Category.SEED), pos).sprite.drop();
                    }
                }

                // Dew, scales from 1/6 to 1/4
                lootChance = 1 / (6f - naturalismLevel / 2f);

                //grassy levels spawn half as much dew
                if (Dungeon.level != null && Dungeon.level.feeling == Level.Feeling.GRASS) {
                    lootChance /= 2;
                }

                if (Random.Float() < lootChance) {
                    level.drop(new Dewdrop(), pos).sprite.drop();

                    if (Random.Float() < lootChance) {  // 掉落露水并判定纳西妲之戒的效果
                        level.drop(new Dewdrop(), pos).sprite.drop();
                        if (ch != null) {
                            Hero hero = Dungeon.hero;
                            if (hero.buff(RingOfNahida.Nahida.class) != null) {	// 触发纳西妲之戒
                                if (Random.Float() < RingOfNahida.grassBonusChance(hero)) {
                                    // 触发掉落
                                    Item i = new Gold().random();
                                    switch (Random.Int(4)) {
                                        case 0:
                                        default:
                                            i.quantity(i.quantity() / 2);
                                            Dungeon.level.drop(i, pos);
                                            break;
                                        case 1:
                                            i = Generator.randomUsingDefaults(Generator.Category.STONE);
                                            break;
                                        case 2:
                                            i = Generator.randomUsingDefaults(Generator.Category.POTION);
                                            break;
                                        case 3:
                                            i = Generator.randomUsingDefaults(Generator.Category.SCROLL);
                                            break;
                                        case 4:
                                            i = Generator.randomUsingDefaults(Generator.Category.SEED);
                                            break;
                                        case 5:
                                            i = Generator.randomUsingDefaults(Generator.Category.MISSILE);
                                    }
                                    Dungeon.level.drop(i, pos);
                                    // 播放特效
                                    new Flare(6, 20).color(0x00FF00, true).show(hero.sprite, 3f);
                                    // 触发鉴定
                                    if (Random.Int(2) == 0) {
                                        hero.belongings.randomUnequipped().identify();
                                    } else {
                                        hero.belongings.observe();
                                    }
                                } else if (Random.Float() > (-RingOfNahida.grassBonusChance(hero))  && RingOfNahida.grassBonusChance(hero ) <0) {
                                    // 触发刷怪惩罚
                                    Sample.INSTANCE.play(Assets.Sounds.CURSED);
                                    // Mob mob = Dungeon.level.createMob();
                                    // ScrollOfTeleportation.appear(mob, pos);
                                    int snake_pose = 0;
                                    for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                                        snake_pose = pos + PathFinder.NEIGHBOURS8[i];
                                        if (Actor.findChar(snake_pose) == null && (Dungeon.level.passable[snake_pose] || Dungeon.level.avoid[snake_pose])) {
                                            break;
                                        }
                                    }
                                    Mob snake = new Viper();
                                    snake.state = snake.HUNTING;
                                    snake.HT = Dungeon.depth;
                                    snake.HP = snake.HT;
                                    snake.yell("!!!!!!");
                                    snake.flying = true;
                                    snake.pos = snake_pose;
                                    GameScene.add(snake);
                                    ScrollOfTeleportation.appear(snake, snake_pose);
                                    Dungeon.level.occupyCell(snake);
                                }
                            }
                        }
                    }
                }
            }

            if (ch != null) {
                Camouflage.activate(ch, ch.glyphLevel(Camouflage.class));
            }

        }

        freezeTrample = false;

        if (ShatteredPixelDungeon.scene() instanceof GameScene) {
            GameScene.updateMap(pos);

            CellEmitter.get(pos).burst(LeafParticle.LEVEL_SPECIFIC, 4);
            if (Dungeon.level.heroFOV[pos]) {
                Dungeon.observe();
            }
        }
    }
}
