/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
* 
 * Ringed Pixel Dungeon
 * Copyright (C) 2025-2025 yantianyv
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
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Random;

public abstract class KindofMisc extends EquipableItem {

    @Override
    public boolean doEquip(final Hero hero) {

        boolean equipFull = false;
        if (this instanceof Artifact
                && hero.belongings.artifact != null
                && hero.belongings.misc != null) {
            //see if we can re-arrange items first
            // 把 misc 槽的戒指挪到第一个空闲戒指槽（含被转换的槽位）
            if (hero.belongings.misc instanceof Ring
                    && moveRingToFreeSlot(hero, (Ring) hero.belongings.misc, 1)) {
                hero.belongings.misc = null;
            } else {
                equipFull = true;
            }
        } else if (this instanceof Ring
                && hero.belongings.misc != null
                && hero.belongings.ring1 != null) {

            // 腾出 ring1：把其中的戒指移到第一个空闲戒指槽（转换槽可能装着神器，直接跳过）
            if (hero.belongings.ring1 instanceof Ring
                    && moveRingToFreeSlot(hero, (Ring) hero.belongings.ring1, 2)) {
                hero.belongings.ring1 = null;
            }

            //see if we can re-arrange items first
            if (hero.belongings.misc instanceof Artifact && hero.belongings.artifact == null && hero.belongings.ring1 != null) {
                hero.belongings.artifact = (Artifact) hero.belongings.misc;
                hero.belongings.misc = null;
            }
            if (hero.belongings.ring1 != null) {
                equipFull = true;
            }
        }

        // 有空闲槽位时不弹替换对话框：
        // 戒指可使用任何空闲戒指槽（ring2-6，含被转换的）；神器可使用空闲的转换槽
        if (equipFull) {
            Belongings b = hero.belongings;
            if (this instanceof Ring
                    && (b.ring2 == null || b.ring3 == null || b.ring4 == null
                    || b.ring5 == null || b.ring6 == null)) {
                equipFull = false;
            } else if (this instanceof Artifact
                    && b.dynamicMiscSlots() > 0
                    && ((b.isConvertedRingSlot(1) && b.ring1 == null)
                    || (b.isConvertedRingSlot(2) && b.ring2 == null)
                    || (b.isConvertedRingSlot(3) && b.ring3 == null))) {
                equipFull = false;
            }
        }

        if (equipFull) {

            final KindofMisc[] miscs = new KindofMisc[8];
            miscs[0] = hero.belongings.artifact;
            miscs[1] = hero.belongings.misc;
            miscs[2] = hero.belongings.ring1;
            miscs[3] = hero.belongings.ring2;
            miscs[4] = hero.belongings.ring3;
            miscs[5] = hero.belongings.ring4;
            miscs[6] = hero.belongings.ring5;
            miscs[7] = hero.belongings.ring6;
            final boolean[] enabled = new boolean[8];
            for (int i = 0; i <= 7; i++) {
                enabled[i] = miscs[i] != null;
            }

            //force swapping with the same type of item if 2x of that type is already present
            if (this instanceof Ring && hero.belongings.misc instanceof Ring) {
                enabled[0] = false; //disable artifact
            }

            if (this instanceof Artifact && hero.belongings.misc instanceof Artifact) {
                for (int i = 2; i <= 7; i++) {
                    // 动态类型：普通戒指槽不能放神器，禁用；转换槽能容纳神器，保持可选
                    boolean converted = (i == 2 && hero.belongings.isConvertedRingSlot(1))
                            || (i == 3 && hero.belongings.isConvertedRingSlot(2))
                            || (i == 4 && hero.belongings.isConvertedRingSlot(3));
                    if (!converted) enabled[i] = false;
                }
            }
            GameScene.show(
                    new WndOptions(new ItemSprite(this),
                            Messages.get(KindofMisc.class, "unequip_title"),
                            Messages.get(KindofMisc.class, "unequip_message"),
                            miscs[0] == null ? "---" : Messages.titleCase(miscs[0].title()),
                            miscs[1] == null ? "---" : Messages.titleCase(miscs[1].title()),
                            miscs[2] == null ? "---" : Messages.titleCase(miscs[2].title()),
                            miscs[3] == null ? "---" : Messages.titleCase(miscs[3].title()),
                            miscs[4] == null ? "---" : Messages.titleCase(miscs[4].title()),
                            miscs[5] == null ? "---" : Messages.titleCase(miscs[5].title()),
                            miscs[6] == null ? "---" : Messages.titleCase(miscs[6].title()),
                            miscs[7] == null ? "---" : Messages.titleCase(miscs[7].title())) {

                @Override
                protected void onSelect(int index) {
                    Dungeon.hero.belongings.backpack.items.remove(KindofMisc.this);
                    KindofMisc equipped = miscs[index];
                    int slot = Dungeon.quickslot.getSlot(KindofMisc.this);
                    slotOfUnequipped = -1;
                    if (equipped.doUnequip(hero, true, false)) {
                        //swap out equip in misc slot if needed
                        if (index == 0 && KindofMisc.this instanceof Ring) {// 用戒指替换神器槽
                            hero.belongings.artifact = (Artifact) hero.belongings.misc;
                            hero.belongings.misc = null;
                        } else if (index >= 2 && KindofMisc.this instanceof Artifact) {// 用神器替换戒指槽
                            // 动态类型：替换的是转换槽时无需链条平移（神器可直接放进该槽），
                            // 否则按原版逻辑平移（且要求 misc 槽是戒指，避免强转崩溃）
                            boolean converted = (index == 2 && hero.belongings.isConvertedRingSlot(1))
                                    || (index == 3 && hero.belongings.isConvertedRingSlot(2))
                                    || (index == 4 && hero.belongings.isConvertedRingSlot(3));
                            if (!converted) {
                                if (hero.belongings.ring5 instanceof Ring && hero.belongings.ring6 == null) {
                                    hero.belongings.ring6 = (Ring) hero.belongings.ring5;
                                    hero.belongings.ring5 = null;
                                }
                                if (hero.belongings.ring4 instanceof Ring && hero.belongings.ring5 == null) {
                                    hero.belongings.ring5 = (Ring) hero.belongings.ring4;
                                    hero.belongings.ring4 = null;
                                }
                                if (hero.belongings.ring3 instanceof Ring && hero.belongings.ring4 == null) {
                                    hero.belongings.ring4 = (Ring) hero.belongings.ring3;
                                    hero.belongings.ring3 = null;
                                }
                                if (hero.belongings.ring2 instanceof Ring && hero.belongings.ring3 == null) {
                                    hero.belongings.ring3 = (Ring) hero.belongings.ring2;
                                    hero.belongings.ring2 = null;
                                }
                                if (hero.belongings.ring1 instanceof Ring && hero.belongings.ring2 == null) {
                                    hero.belongings.ring2 = (Ring) hero.belongings.ring1;
                                    hero.belongings.ring1 = null;
                                }
                                hero.belongings.ring1 = (Ring) hero.belongings.misc;
                                hero.belongings.misc = null;
                            }
                        }
                        doEquip(hero);
                    } else {
                        Dungeon.hero.belongings.backpack.items.add(KindofMisc.this);
                    }
                    if (slot != -1) {
                        Dungeon.quickslot.setSlot(slot, KindofMisc.this);
                    } else if (slotOfUnequipped != -1 && defaultAction() != null) {
                        Dungeon.quickslot.setSlot(slotOfUnequipped, KindofMisc.this);
                    }
                    updateQuickslot();
                }

                @Override
                protected boolean enabled(int index) {
                    return enabled[index];
                }
            });

            return false;

        } else {

            // 15/25% chance
            if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION)
                    && cursed && !cursedKnown
                    && Random.Int(20) < 1 + 2 * hero.pointsInTalent(Talent.HOLY_INTUITION)) {
                cursedKnown = true;
                GLog.p(Messages.get(this, "curse_detected"));
                return false;
            }

            if (this instanceof Artifact) {
                if (hero.belongings.artifact == null) {
                    hero.belongings.artifact = (Artifact) this;
                } else if (hero.belongings.misc == null) {
                    hero.belongings.misc = (Artifact) this;
                } else if (hero.belongings.isConvertedRingSlot(1) && hero.belongings.ring1 == null) {
                    hero.belongings.ring1 = this; // 动态类型：转换槽
                } else if (hero.belongings.isConvertedRingSlot(2) && hero.belongings.ring2 == null) {
                    hero.belongings.ring2 = this;
                } else if (hero.belongings.isConvertedRingSlot(3) && hero.belongings.ring3 == null) {
                    hero.belongings.ring3 = this;
                } else {
                    hero.belongings.misc = (Artifact) this;
                }
            } else if (this instanceof Ring) {
                if (hero.belongings.ring1 == null) {
                    hero.belongings.ring1 = (Ring) this;
                } else if (hero.belongings.misc == null) {
                    hero.belongings.misc = (Ring) this;
                } else if (!moveRingToFreeSlot(hero, (Ring) this, 2)) {
                    // 动态类型：第一个空闲戒指槽（含被转换的）；理论不可达，兜底放 misc
                    hero.belongings.misc = (Ring) this;
                }
            }

            detach(hero.belongings.backpack);

            Talent.onItemEquipped(hero, this);
            activate(hero);

            cursedKnown = true;
            if (cursed) {
                equipCursed(hero);
                GLog.n(Messages.get(this, "equip_cursed", this));
            }

            hero.spendAndNext(timeToEquip(hero));
            return true;

        }

    }

    // 把戒指移到第一个空闲的戒指槽（从第 from 个槽到 ring6，含被动态类型转换的槽位），成功返回 true
    private static boolean moveRingToFreeSlot(Hero hero, Ring ring, int from) {
        Belongings b = hero.belongings;
        if (from <= 1 && b.ring1 == null) { b.ring1 = ring; return true; }
        if (from <= 2 && b.ring2 == null) { b.ring2 = ring; return true; }
        if (from <= 3 && b.ring3 == null) { b.ring3 = ring; return true; }
        if (from <= 4 && b.ring4 == null) { b.ring4 = ring; return true; }
        if (from <= 5 && b.ring5 == null) { b.ring5 = ring; return true; }
        if (b.ring6 == null) { b.ring6 = ring; return true; }
        return false;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single
    ) {
        if (super.doUnequip(hero, collect, single)) {

            if (hero.belongings.artifact == this) {
                hero.belongings.artifact = null;
            } else if (hero.belongings.misc == this) {
                hero.belongings.misc = null;
            } else if (hero.belongings.ring6 == this) {
                hero.belongings.ring6 = null;
            } else if (hero.belongings.ring5 == this) {
                hero.belongings.ring5 = null;
            } else if (hero.belongings.ring4 == this) {
                hero.belongings.ring4 = null;
            } else if (hero.belongings.ring3 == this) {
                hero.belongings.ring3 = null;
            } else if (hero.belongings.ring2 == this) {
                hero.belongings.ring2 = null;
            } else if (hero.belongings.ring1 == this) {
                hero.belongings.ring1 = null;
            }

            return true;

        } else {

            return false;

        }
    }

    @Override
    public boolean isEquipped(Hero hero
    ) {
        return hero != null && (hero.belongings.artifact() == this
                || hero.belongings.misc() == this
                || hero.belongings.ring6() == this
                || hero.belongings.ring5() == this
                || hero.belongings.ring4() == this
                || hero.belongings.ring3() == this
                || hero.belongings.ring2() == this
                || hero.belongings.ring1() == this);
    }

}
