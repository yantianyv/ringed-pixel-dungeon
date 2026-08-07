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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hacked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTimetraveler;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;


import java.util.ArrayList;

// 便携终端：骇客的独特物品。
// 主动使用：对视野内的一个敌人发起主动骇入，叠加两层骇入效果（受零日漏洞/木马大师加成）。
// 协同骇入（物理攻击命中时自动触发）见 Talent.onAttackProc。
public class PortableTerminal extends Item {

    public static final String AC_HACK = "HACK";

    {
        image = ItemSpriteSheet.PORTABLE_TERMINAL;
        defaultAction = AC_HACK;
        // 启用快捷栏目标锁定机制（像法杖/投武一样：记忆目标、准星锁定、二次点击自动命中）
        usesTargeting = true;
        unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hero.buff(MagicImmune.class) == null) {
            actions.add(AC_HACK);
        }
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_HACK)) {
            if (hero.buff(MagicImmune.class) != null) {
                GLog.w(Messages.get(this, "magic_immune"));
                return;
            }

            // 像法杖一样打开瞄准器；若已通过快捷栏锁定记忆目标，再次点击快捷栏可直接命中
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer cell) {
                    if (cell == null) {
                        return; // 取消
                    }
                    // 无效目标：视野外/超距/空格/非敌人（自己、NPC、队友等）——不触发并提示
                    if (!Dungeon.level.heroFOV[cell] || Dungeon.level.distance(cell, hero.pos) > 8) {
                        GLog.w(Messages.get(PortableTerminal.class, "no_target"));
                        return;
                    }
                    Char ch = Actor.findChar(cell);
                    if (ch == null) {
                        GLog.w(Messages.get(PortableTerminal.class, "no_target"));
                        return;
                    }
                    if (ch.alignment != Char.Alignment.ENEMY) {
                        GLog.w(Messages.get(PortableTerminal.class, "invalid_target"));
                        return;
                    }
                    hero.spend(hackTime(hero));
                    hero.busy();
                    Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1f, 1.2f);
                    activeHack(hero, ch, activeHackLayers(hero));
                    hero.sprite.operate(hero.pos);
                    hero.next();
                    // 记忆目标，供下次快捷栏锁定（像法杖/投武一样）
                    QuickSlotButton.target(ch);
                }

                @Override
                public String prompt() {
                    return Messages.get(PortableTerminal.class, "prompt");
                }
            });
        }
    }

    // 骇入没有弹道，自动瞄准时直接锁定目标所在格
    @Override
    public int targetingPos(Hero user, int dst) {
        return dst;
    }

    // 终端的攻击耗时：1 + 1/神器充能速率
    public static float hackTime(Hero hero) {
        return 1f + 1f / RingOfEnergy.artifactChargeMultiplier(hero);
    }

    // 主动骇入层数：基础 2 层 + 零日漏洞加成
    public static int activeHackLayers(Hero hero) {
        int layers = 2;
        if (hero.hasTalent(Talent.ZERO_DAY)) {
            switch (hero.pointsInTalent(Talent.ZERO_DAY)) {
                case 1:
                    layers += 1;
                    break;
                case 2:
                    layers += 1;
                    break;
                case 3:
                    layers += 2;
                    break;
            }
        }
        return TrojanMasterMultiplier(hero, layers);
    }

    // 协同骇入层数：基础 1 层 + 零日漏洞加成
    public static int coopHackLayers(Hero hero) {
        int layers = 1;
        if (hero.hasTalent(Talent.ZERO_DAY)) {
            switch (hero.pointsInTalent(Talent.ZERO_DAY)) {
                case 2:
                    layers += 1;
                    break;
                case 3:
                    layers += 1;
                    break;
            }
        }
        return TrojanMasterMultiplier(hero, layers);
    }

    // 木马大师：骇入层数翻倍（独立乘区，与其它加成乘算）
    public static int TrojanMasterMultiplier(Hero hero, int layers) {
        if (hero.subClass == HeroSubClass.TROJAN_MASTER) {
            layers *= 2;
        }
        return layers;
    }

    // 主动骇入：对目标叠加层数，并受子网广播影响扩散到周围
    public static void activeHack(Hero hero, Char target, int layers) {
        // 主动骇入同样会降低已装备时光行者之戒的效率（与物理伤害一致，防止无损消耗）
        RingOfTimetraveler.reduceEfficiency(hero);
        hackTarget(hero, target, layers);
        // 子网广播：+1 对 3*3、+2 对 5*5 圆形、+3 对 5*5 方形范围造成相同效果
        if (hero.hasTalent(Talent.SUBNET_BROADCAST)) {
            int points = hero.pointsInTalent(Talent.SUBNET_BROADCAST);
            int w = Dungeon.level.width();
            // +1：3*3；+2：5*5 圆形（欧氏距离 <= 2，去掉四角）；+3：5*5 方形
            ArrayList<Integer> cells = new ArrayList<>();
            int r = (points == 1) ? 1 : 2;
            for (int dy = -r; dy <= r; dy++) {
                for (int dx = -r; dx <= r; dx++) {
                    if (dx == 0 && dy == 0) continue;
                    if (points == 2 && dx * dx + dy * dy > 4) continue;
                    cells.add(dy * w + dx);
                }
            }
            for (int offset : cells) {
                int cell = target.pos + offset;
                if (!Dungeon.level.insideMap(cell)) continue;
                Char ch = Actor.findChar(cell);
                if (ch != null && ch != target && ch.alignment == Char.Alignment.ENEMY) {
                    hackTarget(hero, ch, layers);
                }
            }
        }
    }

    // 对目标施加骇入（用于主动骇入、协同骇入、广播风暴）
    public static void hackTarget(Hero hero, Char target, int layers) {
        if (!target.isAlive() || target.alignment != Char.Alignment.ENEMY) {
            return;
        }
        Hacked hacked = Buff.affect(target, Hacked.class);
        if (hacked != null) {
            hacked.addLayers(layers);
            // 设备提权：骇入机械类敌人时，有几率令其沉沦（BOSS 改为 1 回合麻痹）
            if (hero.hasTalent(Talent.PRIVILEGE_ESCALATION) && isMechanical(target)) {
                float chance = 0.01f * hero.pointsInTalent(Talent.PRIVILEGE_ESCALATION) * hacked.layers;
                if (Random.Float() < chance) {
                    if (target.properties().contains(Char.Property.BOSS)) {
                        Buff.prolong(target, Paralysis.class, 1f);
                    } else if (target instanceof Mob) {
                        ScrollOfSirensSong.Enthralled.affectAndLoot((Mob) target, hero, ScrollOfSirensSong.Enthralled.class);
                    }
                }
            }
        }
    }

    // 机械类敌人：DM-100 / DM-200（含 DM-201）/ DM-300 / 魔像
    private static boolean isMechanical(Char ch) {
        return ch instanceof DM100
                || ch instanceof DM200
                || ch instanceof DM300
                || ch instanceof Golem;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public String info() {
        return Messages.get(this, "desc");
    }
}
