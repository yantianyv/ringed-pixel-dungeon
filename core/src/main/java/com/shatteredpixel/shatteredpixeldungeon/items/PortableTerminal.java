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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostElement;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hacked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Overclock;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

// 便携终端：骇客的独特物品，以「温度」衡量状态。
// 初始 20℃，每回合自然降温：降温量为（当前温度 - 20）的 1%（低于 20 时缓慢回升）。
// 超过 120℃ 时终端过热禁用；超过 80℃ 闪烁橙色附魔光效，超过 100℃ 显示红色附魔光效。
// 主动使用：选择视野内目标进行骇入，固定消耗 1 回合。
//   - 敌人：升温 5℃，叠加主动骇入层数（受零日漏洞/木马大师加成/子网广播）
//   - 友军（不含自身）：升温 20℃，隐身 20 回合
//   - 自己（有超频运算天赋）：升温 20℃，获得超频（2 倍命中、3 倍攻速，直到过热）
//   - 宝箱/门：升温 50℃ + 对应钥匙，远程打开
//   - 陷阱：升温 100℃，使其失效
// 协同骇入（物理攻击命中时自动触发）：升温 1℃。
public class PortableTerminal extends Item {

    public static final String AC_HACK = "HACK";

    // 温度阈值与参数
    public static final float TEMPERATURE_IDLE = 20f;      // 环境温度（自然降温的目标）
    public static final float TEMPERATURE_WARN = 80f;      // 橙色闪烁阈值
    public static final float TEMPERATURE_DANGER = 100f;   // 红色光效阈值
    public static final float TEMPERATURE_OVERHEAT = 120f; // 过热禁用阈值
    public static final float COOL_RATE = 0.01f;           // 每回合降温（当前温度 - 20）的 1%
    public static final float LIQUID_COOL_BASE = 10f;      // 液冷散热的目标温度

    // 寒冷 / 冰冻 / 元素冻结的降温目标与速率（替代被动降温）
    public static final float CHILL_TARGET = 0f;           // 寒冷：向 0℃ 收敛
    public static final float CHILL_COOL_RATE = 0.10f;     // 每回合降（当前温度 - 0）的 10%
    public static final float FROST_TARGET = -20f;         // 冰冻/元素冻结：向 -20℃ 收敛
    public static final float FROST_COOL_RATE = 0.15f;     // 每回合降（当前温度 - (-20)）的 15%

    // 各类骇入的升温量（由原充能消耗 1:1 映射）
    public static final float ACTIVE_HACK_HEAT = 5f;
    public static final float COOP_HACK_HEAT = 1f;
    public static final float ALLY_INVIS_HEAT = 20f;
    public static final float OVERCLOCK_HEAT = 20f;
    public static final float CHEST_DOOR_HEAT = 50f;
    public static final float TRAP_HEAT = 100f;

    private float temperature = TEMPERATURE_IDLE;

    {
        image = ItemSpriteSheet.PORTABLE_TERMINAL;
        defaultAction = AC_HACK;
        // 启用快捷栏目标锁定机制（像法杖/投武一样：记忆目标、准星锁定、二次点击自动命中）
        usesTargeting = true;
        unique = true;
        keptThoughLostInvent = true;
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
            if (isOverheated()) {
                GLog.w(Messages.get(this, "overheated"));
                return;
            }

            ensureCharger(hero);

            // 像法杖一样打开瞄准器；若已通过快捷栏锁定记忆目标，再次点击快捷栏可直接命中
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer cell) {
                    if (cell == null) {
                        return; // 取消
                    }
                    // 无效目标：视野/感知外——不触发并提示
                    if (!Dungeon.level.heroFOV[cell]) {
                        GLog.w(Messages.get(PortableTerminal.class, "no_target"));
                        return;
                    }

                    Char ch = Actor.findChar(cell);
                    Heap heap = Dungeon.level.heaps.get(cell);
                    Trap trap = Dungeon.level.traps.get(cell);
                    int terrain = Dungeon.level.map[cell];

                    // 敌人（含宝箱怪）
                    if (ch != null && ch.isAlive()
                            && (ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic)) {
                        if (activeHack(hero, ch, activeHackLayers(hero))) {
                            hero.spend(1f);
                            hero.busy();
                            Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1f, 1.2f);
                            hero.sprite.operate(hero.pos);
                            hero.next();
                            QuickSlotButton.target(ch);
                        }
                        return;
                    }

                    // 超频运算：骇客对自己使用骇入 → 获得超频（2 倍命中、3 倍攻速，直到过热）
                    if (ch == hero && hero.hasTalent(Talent.OVERCLOCKING)) {
                        if (addHeat(hero, OVERCLOCK_HEAT, true)) {
                            hero.spend(1f);
                            hero.busy();
                            Sample.INSTANCE.play(Assets.Sounds.MELD, 1f, 1.2f);
                            Buff.affect(hero, Overclock.class);
                            GLog.i(Messages.get(Talent.class, "overclocked"));
                            hero.sprite.operate(hero.pos);
                            hero.next();
                        }
                        return;
                    }

                    // 友军隐身（不含自身）
                    if (ch != null && ch != hero && ch.isAlive() && ch.alignment == Char.Alignment.ALLY) {
                        if (addHeat(hero, ALLY_INVIS_HEAT, true)) {
                            hero.spend(1f);
                            hero.busy();
                            Sample.INSTANCE.play(Assets.Sounds.MELD, 1f, 1.2f);
                            Buff.affect(ch, Invisibility.class, 20f);
                            GLog.i(Messages.get(PortableTerminal.class, "ally_invisible", ch.name()));
                            hero.sprite.operate(hero.pos);
                            hero.next();
                        }
                        return;
                    }

                    // 宝箱（普通/上锁/水晶）
                    if (heap != null && isChest(heap)) {
                        if (heap.type == Heap.Type.LOCKED_CHEST
                                && Notes.keyCount(new GoldenKey(Dungeon.depth)) < 1) {
                            GLog.w(Messages.get(PortableTerminal.class, "no_key"));
                            return;
                        }
                        if (heap.type == Heap.Type.CRYSTAL_CHEST
                                && Notes.keyCount(new CrystalKey(Dungeon.depth)) < 1) {
                            GLog.w(Messages.get(PortableTerminal.class, "no_key"));
                            return;
                        }
                        if (addHeat(hero, CHEST_DOOR_HEAT, true)) {
                            hero.spend(1f);
                            hero.busy();
                            Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 1f, 1.2f);
                            if (heap.type == Heap.Type.LOCKED_CHEST) {
                                Notes.remove(new GoldenKey(Dungeon.depth));
                            } else if (heap.type == Heap.Type.CRYSTAL_CHEST) {
                                Notes.remove(new CrystalKey(Dungeon.depth));
                            }
                            heap.open(hero);
                            GLog.i(Messages.get(PortableTerminal.class, "chest_opened"));
                            GameScene.updateKeyDisplay();
                            hero.sprite.operate(hero.pos);
                            hero.next();
                        }
                        return;
                    }

                    // 上锁门/水晶门
                    if (terrain == Terrain.LOCKED_DOOR || terrain == Terrain.CRYSTAL_DOOR) {
                        if (terrain == Terrain.LOCKED_DOOR
                                && Notes.keyCount(new IronKey(Dungeon.depth)) < 1) {
                            GLog.w(Messages.get(PortableTerminal.class, "no_key"));
                            return;
                        }
                        if (terrain == Terrain.CRYSTAL_DOOR
                                && Notes.keyCount(new CrystalKey(Dungeon.depth)) < 1) {
                            GLog.w(Messages.get(PortableTerminal.class, "no_key"));
                            return;
                        }
                        if (addHeat(hero, CHEST_DOOR_HEAT, true)) {
                            hero.spend(1f);
                            hero.busy();
                            if (terrain == Terrain.LOCKED_DOOR) {
                                Notes.remove(new IronKey(Dungeon.depth));
                                Level.set(cell, Terrain.DOOR);
                                Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 1f, 1.2f);
                            } else {
                                Notes.remove(new CrystalKey(Dungeon.depth));
                                Level.set(cell, Terrain.EMPTY);
                                Sample.INSTANCE.play(Assets.Sounds.TELEPORT, 1f, 1.2f);
                                CellEmitter.get(cell).start(Speck.factory(Speck.DISCOVER), 0.025f, 20);
                            }
                            GameScene.updateMap(cell);
                            GameScene.updateKeyDisplay();
                            GLog.i(Messages.get(PortableTerminal.class, "door_unlocked"));
                            hero.sprite.operate(hero.pos);
                            hero.next();
                        }
                        return;
                    }

                    // 陷阱
                    if (trap != null && trap.active) {
                        if (addHeat(hero, TRAP_HEAT, true)) {
                            hero.spend(1f);
                            hero.busy();
                            Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1f, 1.2f);
                            trap.disarm();
                            GLog.i(Messages.get(PortableTerminal.class, "trap_disarmed"));
                            hero.sprite.operate(hero.pos);
                            hero.next();
                        }
                        return;
                    }

                    GLog.w(Messages.get(PortableTerminal.class, "no_target"));
                }

                @Override
                public String prompt() {
                    return Messages.get(PortableTerminal.class, "prompt");
                }
            });
        }
    }

    private static boolean isChest(Heap heap) {
        return heap.type == Heap.Type.CHEST
                || heap.type == Heap.Type.LOCKED_CHEST
                || heap.type == Heap.Type.CRYSTAL_CHEST;
    }

    // 骇入没有弹道，自动瞄准时直接锁定目标所在格
    @Override
    public int targetingPos(Hero user, int dst) {
        return dst;
    }

    // ———————— 温度系统 ————————

    public float temperature() {
        return temperature;
    }

    public boolean isOverheated() {
        return temperature > TEMPERATURE_OVERHEAT;
    }

    // 温度向 target 收敛，按 rate 比例降温（或回升）
    public void coolDown(float target, float rate) {
        if (temperature != target) {
            temperature -= (temperature - target) * rate;
            updateQuickslot();
        }
    }

    // 每回合散热：被寒冷/冰冻/元素冻结时使用对应的强降温，否则被动降温
    public void tickCooling(Hero hero) {
        if (hero.buff(Frost.class) != null || hero.buff(FrostElement.class) != null) {
            coolDown(FROST_TARGET, FROST_COOL_RATE);
        } else if (hero.buff(Chill.class) != null) {
            coolDown(CHILL_TARGET, CHILL_COOL_RATE);
        } else {
            coolDown(TEMPERATURE_IDLE, COOL_RATE);
        }
    }

    // 液冷散热：降温（当前温度 - 10）的 rate 比例
    public void liquidCool(float rate) {
        if (temperature > LIQUID_COOL_BASE) {
            temperature -= (temperature - LIQUID_COOL_BASE) * rate;
            updateQuickslot();
        }
    }

    // 升高温度；若超过阈值则过热（禁用 + 结束超频）
    public boolean addHeat(float amount, boolean warnIfOverheated) {
        if (isOverheated()) {
            if (warnIfOverheated) {
                GLog.w(Messages.get(PortableTerminal.class, "overheated"));
            }
            return false;
        }
        temperature += amount;
        if (isOverheated()) {
            // 过热：结束超频并提示
            if (Dungeon.hero != null) {
                Buff.detach(Dungeon.hero, Overclock.class);
            }
            GLog.w(Messages.get(PortableTerminal.class, "overheated"));
        }
        updateQuickslot();
        return true;
    }

    public static boolean addHeat(Hero hero, float amount, boolean warnIfOverheated) {
        PortableTerminal terminal = hero.belongings.getItem(PortableTerminal.class);
        if (terminal == null) return false;
        return terminal.addHeat(amount, warnIfOverheated);
    }

    public static void ensureCharger(Hero hero) {
        if (hero.buff(TerminalCharger.class) == null) {
            Buff.affect(hero, TerminalCharger.class);
        }
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
    public static boolean activeHack(Hero hero, Char target, int layers) {
        if (!addHeat(hero, ACTIVE_HACK_HEAT, true)) {
            return false;
        }
        hackTarget(hero, target, layers, 0f);
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
                int c = target.pos + offset;
                if (!Dungeon.level.insideMap(c)) continue;
                Char ch = Actor.findChar(c);
                if (ch != null && ch != target
                        && (ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic)) {
                    hackTarget(hero, ch, layers, 0f);
                }
            }
        }
        return true;
    }

    // 对目标施加骇入（用于主动骇入、协同骇入、广播风暴等）
    // 默认升温 = 1℃（协同骇入），heat = 0 时不升温（已由上层预付）
    public static void hackTarget(Hero hero, Char target, int layers) {
        hackTarget(hero, target, layers, COOP_HACK_HEAT);
    }

    public static void hackTarget(Hero hero, Char target, int layers, float heat) {
        if (!target.isAlive() || !(target.alignment == Char.Alignment.ENEMY || target instanceof Mimic)) {
            return;
        }
        ensureCharger(hero);
        if (heat > 0 && !addHeat(hero, heat, false)) {
            return;
        }
        Hacked hacked = Buff.affect(target, Hacked.class);
        if (hacked != null) {
            hacked.addLayers(layers);
            // 设备提权：+1 仅对机械类敌人触发（1% × √层数）；
            // +2 替换原先“概率翻倍”的效果：非机械类敌人也能触发，但概率为 0.1% × √层数（机械类概率的 10%）
            // 触发后目标转化为友军；不能被转化的（BOSS）降级为 1 回合麻痹——机械类与非机械类一致
            int escalation = hero.pointsInTalent(Talent.PRIVILEGE_ESCALATION);
            boolean mechanical = isMechanical(target);
            if (escalation > 0 && (mechanical || escalation >= 2)) {
                float chance = 0.01f * (float) Math.sqrt(hacked.layers); // 机械类：1% × √层数（+2 不翻倍）
                if (!mechanical) {
                    chance *= 0.1f; // 非机械类：0.1% × √层数
                }
                if (Random.Float() < chance) {
                    if (target.properties().contains(Char.Property.BOSS)) {
                        // 不能被转化（BOSS）：降级为 1 回合麻痹
                        Buff.prolong(target, Paralysis.class, 1f);
                    } else if (target instanceof Mob) {
                        // 转化为友军（机械类与非机械类一致）
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
    public boolean collect(Bag container) {
        boolean result = super.collect(container);
        if (result && Dungeon.hero != null) {
            ensureCharger(Dungeon.hero);
        }
        return result;
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

    @Override
    public String status() {
        return Math.round(temperature) + "'C";
    }

    // 附魔光效：超过 80℃ 闪烁橙色，超过 100℃ 显示红色
    @Override
    public ItemSprite.Glowing glowing() {
        if (temperature > TEMPERATURE_DANGER) {
            return new ItemSprite.Glowing(0xFF2200, 0.8f); // 红色
        } else if (temperature > TEMPERATURE_WARN) {
            return new ItemSprite.Glowing(0xFF8000, 0.4f); // 橙色闪烁
        }
        return null;
    }

    private static final String TEMPERATURE = "temperature";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TEMPERATURE, temperature);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        // 旧存档中的 charge 字段不再读取（无迁移，旧存档回落到初始温度）
        if (bundle.contains(TEMPERATURE)) {
            temperature = bundle.getFloat(TEMPERATURE);
        } else {
            temperature = TEMPERATURE_IDLE;
        }
    }

    // 终端散热器：作为 Buff 挂在英雄身上，每回合为终端降温。
    // 它本身不保存任何数据，所有温度数据都保存在 PortableTerminal 中。
    public static class TerminalCharger extends Buff {

        {
            type = buffType.POSITIVE;
            announced = false;
            revivePersists = true;
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                if (target instanceof Hero && Dungeon.hero == null && target.cooldown() > 0) {
                    // 读档加载时若英雄已经有部分冷却，延迟一回合再降温
                    spend(TICK);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean act() {
            spend(TICK);

            if (!(target instanceof Hero)) {
                detach();
                return true;
            }

            Hero hero = (Hero) target;
            PortableTerminal terminal = hero.belongings.getItem(PortableTerminal.class);
            if (terminal != null) {
                terminal.tickCooling(hero);
            } else {
                // 终端已丢失，散热器没有存在意义
                detach();
            }

            return true;
        }
    }
}
