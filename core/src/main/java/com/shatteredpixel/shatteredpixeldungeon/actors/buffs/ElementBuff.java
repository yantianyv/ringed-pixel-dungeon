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
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff.Element;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfNahida;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave.BlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass.Health;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/**
 * 元素附着基类，处理游戏中的元素反应系统
 */
public class ElementBuff extends Buff implements Hero.Doom {

    public ElementBuff() {
        super();
    }

    // 元素附着量
    protected float quantity = 0f;
    // 元素类型
    protected Element element;

    {
        // 设为负面buff（对目标不利）
        type = buffType.NEUTRAL;
        // buff需要公告（显示在游戏日志中）
        announced = true;
    }

    // 定义图标
    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

    @Override
    public boolean act() {
        fx(true);
        this.quantity *= 0.99f; // 改为固定值衰减
        if (quantity < 0.5f && target instanceof Hero) {
            quantity = 0f;
        }
        if (quantity <= 0.1f) {
            fx(false);
            detach();
        }
        spend(0.1f);
        return true;
    }

    // 元素类型枚举
    public enum Element {
        ANEMO, // 风元素
        GEO, // 岩元素
        ELECTRO, // 雷元素
        DENDRO, // 草元素
        HYDRO, // 水元素
        PYRO, // 火元素
        CRYO     // 冰元素
    }

    public static Element randomElement() {
        switch (Random.Int(7)) {
            case 0:
                return Element.ANEMO; // 风元素
            case 1:
                return Element.GEO; // 岩元素
            case 2:
                return Element.ELECTRO; // 雷元素
            case 3:
                return Element.DENDRO; // 草元素
            case 4:
                return Element.HYDRO; // 水元素
            case 5:
                return Element.PYRO; // 火元素
            case 6:
                return Element.CRYO; // 冰元素
            default:
                return null;
        }
    }

    /**
     * 应用元素附着到目标
     *
     * @param element 元素类型
     * @param attacker 施加者
     * @param defender 目标
     * @param quantity 附着量
     */
    public static float apply(Element element, Char attacker, Char defender, float quantity) {
        Buff buff = null;

        // 根据元素类型创建对应的buff实例
        switch (element) {
            case ANEMO:
                buff = new AnemoElement();
                break;
            case GEO:
                buff = new GeoElement();
                break;
            case ELECTRO:
                buff = new ElectroElement();
                break;
            case DENDRO:
                buff = new DendroElement();
                break;
            case HYDRO:
                buff = new HydroElement();
                break;
            case PYRO:
                buff = new PyroElement();
                break;
            case CRYO:
                buff = new CryoElement();
                break;
        }
        if (Dungeon.hero != null && Dungeon.hero.buff(RingOfNahida.Nahida.class) != null) {
            if (defender instanceof Hero) {
                quantity /= RingOfNahida.elementalMastery(Dungeon.hero);
            } else {
                quantity *= RingOfNahida.elementalMastery(Dungeon.hero);
            }
        }

        // 如果目标不存在同类buff就添加一个新buff
        if (defender.buff(buff.getClass()) == null) {
            buff.attachTo(defender);
            ((ElementBuff) buff).element = element;
            ((ElementBuff) buff).quantity = quantity;
        } else {
            // 否则就使用目标身上已有的buff
            buff = defender.buff(buff.getClass());
            // 增加元素附着量并触发反应
            ((ElementBuff) buff).quantity += quantity;
            buff.fx(true);
        }
        return ((ElementBuff) buff).reaction(defender);
    }

    public static void detach(Char target, Element element) {
        for (Buff buff : target.buffs(ElementBuff.class)) {
            if (((ElementBuff) buff).element == element) {
                buff.detach();
                break;
            }
        }
    }

    /**
     * 处理元素反应（统一反应处理逻辑）
     *
     * @param ch 目标角色
     * @return 反应消耗的元素量
     */
    public float reaction(Char ch) {
        return reactWith(ch, this);
    }

    /**
     * 统一元素反应处理方法
     */
    protected float reactWith(Char ch, ElementBuff thisBuff) {
        float dmg_multi = 1;

        // 遍历目标的所有buff
        for (Buff b : ch.buffs()) {
            if (b instanceof ElementBuff && b != thisBuff) {
                ElementBuff other = (ElementBuff) b;

                // 根据当前元素和另一个元素的组合调用对应的反应
                switch (this.element) {
                    case ANEMO:
                        if (other.element != Element.GEO) { // 风不与岩反应
                            dmg_multi *= Spread(thisBuff, other, ch);
                        }
                        break;
                    case GEO:
                        if (other.element != Element.ANEMO) { // 岩不与风反应
                            dmg_multi *= Crystalize(thisBuff, other, ch);
                        }
                        break;
                    case ELECTRO:
                        switch (other.element) {
                            case PYRO:
                                dmg_multi *= Overload(other, thisBuff, ch);
                                break;
                            case CRYO:
                                dmg_multi *= Superconduct(thisBuff, other, ch);
                                break;
                            case HYDRO:
                                dmg_multi *= Shock(thisBuff, other, ch);
                                break;
                            case DENDRO:
                                dmg_multi *= Activate(thisBuff, other, ch);
                                break;
                        }
                        break;
                    case DENDRO:
                        switch (other.element) {
                            case PYRO:
                                dmg_multi *= Burn(other, thisBuff, ch);
                                break;
                            case HYDRO:
                                dmg_multi *= Bloom(other, thisBuff, ch);
                                break;
                            case ELECTRO:
                                dmg_multi *= Activate(other, thisBuff, ch);
                                break;
                        }
                        break;
                    case HYDRO:
                        switch (other.element) {
                            case PYRO:
                                dmg_multi *= Vaporize(other, thisBuff, ch);
                                break;
                            case CRYO:
                                dmg_multi *= Freeze(other, thisBuff, ch);
                                break;
                            case ELECTRO:
                                dmg_multi *= Shock(other, thisBuff, ch);
                                break;
                            case DENDRO:
                                dmg_multi *= Bloom(thisBuff, other, ch);
                                break;
                            case GEO:
                                dmg_multi *= Crystalize(other, thisBuff, ch);
                                break;
                        }
                        break;
                    case PYRO:
                        switch (other.element) {
                            case HYDRO:
                                dmg_multi *= Vaporize(thisBuff, other, ch);
                                break;
                            case CRYO:
                                dmg_multi *= Melt(thisBuff, other, ch);
                                break;
                            case ELECTRO:
                                dmg_multi *= Overload(thisBuff, other, ch);
                                break;
                            case DENDRO:
                                dmg_multi *= Burn(thisBuff, other, ch);
                                break;
                            case GEO:
                                dmg_multi *= Crystalize(other, thisBuff, ch);
                                break;
                        }
                        break;
                    case CRYO:
                        switch (other.element) {
                            case PYRO:
                                dmg_multi *= Melt(other, thisBuff, ch);
                                break;
                            case HYDRO:
                                dmg_multi *= Freeze(thisBuff, other, ch);
                                break;
                            case ELECTRO:
                                dmg_multi *= Superconduct(other, thisBuff, ch);
                                break;
                            case GEO:
                                dmg_multi *= Crystalize(other, thisBuff, ch);
                                break;
                        }
                        break;
                }
            }
        }

        // 如果元素量耗尽则移除buff
        if (quantity <= 0) {
            detach();
        }

        return dmg_multi;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        // 使用更明确的键名避免冲突
        bundle.put("element_quantity", quantity);
        bundle.put("element_type", element.name());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        // 添加空检查和安全转换
        if (bundle.contains("element_quantity")) {
            quantity = bundle.getFloat("element_quantity");
        }
        if (bundle.contains("element_type")) {
            element = Element.valueOf(bundle.getString("element_type"));
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", (int) (quantity * 100) / 100f);
    }

    @Override
    public void onDeath() {
        // Badges.validateDeathFromElement();

        Dungeon.fail(this);
        GLog.n(Messages.get(this, "on_death"));
    }

    // ====================== 元素反应方法 ======================
    /**
     * 超载反应（火+雷）
     *
     * @return 消耗的元素量
     */
    static float Overload(ElementBuff pyro, ElementBuff electro, Char ch) {
        float consume = Math.min(pyro.quantity, electro.quantity);
        if (consume > 0) {
            // 在怪物的图标上显示超载文本
            CharSprite cs = ch.sprite;
            cs.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "overload"));
        } else {
            consume = 0;
        }
        // 消耗元素
        pyro.quantity -= consume;
        electro.quantity -= consume;
        // 对目标自身造成伤害
        ch.damage(Math.round(5 * consume), new ElementBuff());
        // 对周围造成伤害
        for (int i : PathFinder.NEIGHBOURS8) {
            Char target = Actor.findChar(ch.pos + i);

            if (target != null) {
                if (target.alignment == ch.alignment) {
                    // 对目标友方造成伤害
                    target.damage(Math.round(3 * consume), new ElementBuff());
                }//  对目标敌方造成伤害
                else {
                    target.damage(Math.round(1 * consume), new ElementBuff());
                }
                // 造成冲击
                BlastWave.blast(ch.pos, 1 + (float) Math.pow(consume, 0.5));
            }
        }
        return 1f;
    }

    /**
     * 感电反应（雷+水）
     *
     * @return 消耗的元素量
     */
    static float Shock(ElementBuff electro, ElementBuff hydro, Char ch) {
        float consume = Math.min(electro.quantity, hydro.quantity);
        if (consume > 0) {
            // 在怪物的图标上显示感电文本
            CharSprite cs = ch.sprite;
            cs.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "shock"));
        } else {
            consume = 0;
        }
        electro.quantity -= consume;
        hydro.quantity -= consume;

        PathFinder.buildDistanceMap(ch.pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < (1 + Math.sqrt(consume))) {
                Char n = Actor.findChar(i);
                if (n != null) {
                    n.damage((int) (1 + consume), new ElementBuff());
                    if (consume >= 1) {
                        ElementBuff.apply(Element.ELECTRO, n, n, consume / 2);
                    }
                }
            }
        }

        CellEmitter.center(ch.pos).burst(SparkParticle.FACTORY, 3);

        return 1f;
    }

    /**
     * 冻结反应（冰+水）
     *
     * @return 消耗的元素量
     */
    static float Freeze(ElementBuff cryo, ElementBuff hydro, Char ch) {
        float consume = Math.min(cryo.quantity, hydro.quantity);
        if (consume > 0) {
            // 在怪物的图标上显示冻结文本
            CharSprite cs = ch.sprite;
            cs.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "freeze"));
        } else {
            consume = 0;
        }
        cryo.quantity -= consume;
        hydro.quantity -= consume;

        // 计算冻结伤害
        int dmg = (int) (ch.HP * ((consume) / (consume + 5)));
        if (Dungeon.level.locked || ch instanceof Hero) {
            dmg /= 5;
        }

        if (consume > 1) {
            if (ch instanceof Hero) {
                Buff.affect(ch, Paralysis.class, consume / 2);
                ch.damage(dmg, new ElementBuff());
            } else {
                Frost buff = Buff.affect(ch, Frost.class, consume);
                buff.frost_damage = dmg;
            }
        } else {
            ch.damage(dmg, new ElementBuff());
        }
        return 1f;
    }

    /**
     * 蒸发反应（火+水）
     *
     * @return 消耗的元素量
     */
    static float Vaporize(ElementBuff pyro, ElementBuff hydro, Char ch) {
        float consume = Math.min(pyro.quantity, hydro.quantity);
        if (consume > 0) {
            // 在怪物的图标上显示蒸发文本
            CharSprite cs = ch.sprite;
            cs.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "vaporize"));
        } else {
            consume = 0;
        }
        pyro.quantity -= consume;
        hydro.quantity -= consume;
        return 1.5f + consume * 0.1f;
    }

    /**
     * 融化反应（火+冰）
     *
     * @return 消耗的元素量
     */
    static float Melt(ElementBuff pyro, ElementBuff cryo, Char ch) {
        float consume = Math.min(pyro.quantity, cryo.quantity);
        if (consume > 0) {
            // 在怪物的图标上显示融化文本
            CharSprite cs = ch.sprite;
            cs.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "melt"));
        } else {
            consume = 0;
        }
        pyro.quantity -= consume;
        cryo.quantity -= consume;
        return 1.5f + consume * 0.1f;
    }

    /**
     * 扩散反应（风+其他元素） - 缩小范围版
     *
     * @return 消耗的元素量
     */
    static float Spread(ElementBuff anemo, ElementBuff other, Char ch) {
        float consume = Math.min(anemo.quantity, other.quantity);
        if (consume > 0) {
            // 在怪物的图标上显示扩散文本
            CharSprite cs = ch.sprite;
            cs.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "spread"));
        } else {
            consume = 0;
        }
        anemo.quantity -= consume;
        other.quantity -= consume;

        // 计算扩散强度（使用NEIGHBOURS8，范围固定为1圈）
        float strength = consume / 2f; // 扩散强度为消耗量的一半

        if (strength > 0) {
            // 对周围8格敌人施加相同的元素
            for (int i : PathFinder.NEIGHBOURS8) {
                Char target = Actor.findChar(ch.pos + i);
                if (target != null && target != ch) {
                    // 对周围敌人施加相同元素
                    apply(other.element, ch, target, strength);

                    // 如果是火/水/雷/冰元素，额外造成一次伤害
                    switch (other.element) {
                        case PYRO:
                        case HYDRO:
                        case ELECTRO:
                        case CRYO:
                            target.damage(Math.round(3 * strength), new ElementBuff());
                            break;
                    }
                }
            }

            // 造成一次小范围的冲击波效果（范围1）
            BlastWave.blast(ch.pos, 1);

            // 如果扩散的是火元素，点燃周围的可燃物
            if (other.element == Element.PYRO) {
                for (int i : PathFinder.NEIGHBOURS8) {
                    int cell = ch.pos + i;
                    if (Dungeon.level.flamable[cell]) {
                        GameScene.add(Blob.seed(cell, 2, Fire.class));
                    }
                }
            }
        }

        return 1f;
    }

    /**
     * 结晶反应（岩+其他元素）
     *
     * @return 消耗的元素量
     */
    static float Crystalize(ElementBuff geo, ElementBuff other, Char ch) {
        float consume = Math.min(geo.quantity, other.quantity);
        if (consume > 0) {
            // 在怪物的图标上显示扩散文本
            CharSprite cs = ch.sprite;
            cs.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "crystalize"));
        } else {
            consume = 0;
        }
        geo.quantity -= consume;
        other.quantity -= consume;
        // 给英雄添加护盾
        int amount = (int) (consume + Dungeon.hero.HT * consume / 50);
        Barrier b = Buff.affect(Dungeon.hero, Barrier.class);
        if (b.shielding() <= amount) {
            b.setShield(amount);
            b.delay(Math.max(10 - b.cooldown(), 0));
        }
        return 1f;
    }

    /**
     * 超导反应（雷+冰）
     *
     * @return 消耗的元素量
     */
    static float Superconduct(ElementBuff electro, ElementBuff cryo, Char ch) {
        float consume = Math.min(electro.quantity, cryo.quantity);
        if (consume > 0) {
            // 在怪物的图标上显示超导文本
            CharSprite cs = ch.sprite;
            cs.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "superconduct"));
        } else {
            consume = 0;
        }
        electro.quantity -= consume;
        cryo.quantity -= consume;

        // 基础物理伤害
        ch.damage(Math.round(3 * consume), new ElementBuff());

        // 施加物理易伤效果，持续时间和强度基于消耗量
        float duration = 3f + consume; // 基础3秒 + 每点消耗量增加1秒

        // 应用易伤效果
        Buff.affect(ch, Vulnerable.class, duration);

        // 对周围敌人造成范围伤害和易伤
        for (int i : PathFinder.NEIGHBOURS8) {
            Char target = Actor.findChar(ch.pos + i);
            if (target != null && target != ch) {
                // 范围物理伤害
                target.damage(Math.round(2 * consume), new ElementBuff());

                // 范围易伤效果(强度减半)
                Buff.affect(target, Vulnerable.class, duration * 0.7f);
            }
        }

        // // 视觉特效 - 冰雾扩散
        // GameScene.add(Blob.seed(ch.pos, (int) (consume * 5), Freezing.class));
        return 1f;
    }

    /**
     * 燃烧反应（火+草）
     *
     * @return 消耗的元素量
     */
    static float Burn(ElementBuff pyro, ElementBuff dendro, Char ch) {
        float consume = Math.min(pyro.quantity, dendro.quantity);
        if (consume > 0) {
            // 在怪物的图标上显示燃烧文本
            CharSprite cs = ch.sprite;
            cs.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "burn"));
        } else {
            consume = 0;
        }
        pyro.quantity -= consume;
        dendro.quantity -= consume;
        Buff.affect(ch, Burning.class).reignite(ch, consume);
        return 1f;
    }

    /**
     * 激化反应（雷+草）
     *
     * @return 消耗的元素量
     */
    static float Activate(ElementBuff electro, ElementBuff dendro, Char ch) {
        float consume = Math.min(electro.quantity, dendro.quantity);
        electro.quantity -= consume;
        dendro.quantity -= consume;
        // TODO: 实现激化反应的具体效果
        GLog.p("激化（未实现）");
        return 1f;
    }

    /**
     * 绽放反应（水+草）
     *
     * @return 消耗的元素量
     */
    static float Bloom(ElementBuff hydro, ElementBuff dendro, Char ch) {
        float consume = Math.min(hydro.quantity, dendro.quantity);

        // 在周围8格寻找有效位置
        ArrayList<Integer> validPositions = new ArrayList<>();
        for (int i = 0; i <= 8; i++) {
            int cell = ch.pos + PathFinder.NEIGHBOURS9[i];
            int terr = Dungeon.level.map[cell];
            if (terr == Terrain.GRASS || terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO) {
                validPositions.add(cell);
            }
        }
        int target_pos = 0;
        if (!validPositions.isEmpty()) {
            int randomIndex = Random.Int(validPositions.size());
            target_pos = validPositions.get(randomIndex);
            if (consume > 3) {
                // 生成高草
                Level.set(target_pos, Terrain.HIGH_GRASS);
            } else {
                // 生成犁过的草地
                Level.set(target_pos, Terrain.FURROWED_GRASS);
            }
        } else {
            target_pos = ch.pos;
        }

        for (int i = 0; i <= 8; i++) {
            // 对范围内的目标造成伤害
            Char target = Actor.findChar(ch.pos + PathFinder.NEIGHBOURS9[i]);
            if (target != null) {
                int dmg = Math.round(consume * (1f + (float) Dungeon.depth / 3f));
                if (target instanceof Hero) {
                    dmg /= 5;
                    if (((Hero) target).heroClass == HeroClass.HUNTRESS) {
                        dmg /= 2;
                        if (((Hero) target).subClass == HeroSubClass.WARDEN) {
                            Buff.affect(target, Health.class).boost((int) consume);
                        }
                    }
                }

                target.damage(Math.round(dmg), new ElementBuff());
            }
        }

        // 显示反应文本
        ch.sprite.showStatus(CharSprite.NEUTRAL, Messages.get(ElementBuff.class, "bloom"));

        // 消耗元素
        hydro.quantity -= consume;
        dendro.quantity -= consume;
        return 1f;
    }

    // ====================== 元素Buff实现 ======================
    public static class AnemoElement extends ElementBuff {

        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x76EEC6);
        } // 风元素-青绿

        @Override
        public void fx(boolean on) {
            if (target != null && target.sprite != null) {
                if (on) {
                    target.sprite.tint(0x76EEC6);
                } else {
                    target.sprite.resetColor();
                }
            }
        }

        @Override
        public boolean act() {
            this.detach();
            return super.act();
        }
    }

    public static class GeoElement extends ElementBuff {

        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xFFD700);
        } // 岩元素-金黄

        @Override
        public void fx(boolean on) {
            if (target != null && target.sprite != null) {
                if (on) {
                    target.sprite.tint(0xFFD700);
                } else {
                    target.sprite.resetColor();
                }
            }
        }
    }

    public static class ElectroElement extends ElementBuff {

        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xBF3EFF);
        } // 雷元素-紫

        @Override
        public void fx(boolean on) {
            if (target != null && target.sprite != null) {
                if (on) {
                    target.sprite.tint(0xBF3EFF);
                } else {
                    target.sprite.resetColor();
                }
            }
        }
    }

    public static class DendroElement extends ElementBuff {

        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x7CFC00);
        } // 草元素-浅绿

        @Override
        public void fx(boolean on) {
            if (target != null && target.sprite != null) {
                if (on) {
                    target.sprite.tint(0x7CFC00);
                } else {
                    target.sprite.resetColor();
                }
            }
        }
    }

    public static class HydroElement extends ElementBuff {

        public HydroElement() {
            super();
        }

        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x00BFFF);
        } // 水元素-蓝

        @Override
        public void fx(boolean on) {
            if (target != null && target.sprite != null) {
                if (on) {
                    target.sprite.tint(0x00BFFF);
                } else {
                    target.sprite.resetColor();
                }
            }
        }
    }

    public static class PyroElement extends ElementBuff {

        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xFF4500);
        } // 火元素-橙红

        @Override
        public void fx(boolean on) {
            if (target != null && target.sprite != null) {
                if (on) {
                    target.sprite.tint(0xFF4500);
                } else {
                    target.sprite.resetColor();
                }
            }
        }
    }

    public static class CryoElement extends ElementBuff {

        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xADD8E6);
        } // 冰元素-浅蓝

        @Override
        public void fx(boolean on) {
            if (target != null && target.sprite != null) {
                if (on) {
                    target.sprite.tint(0xADD8E6);
                } else {
                    target.sprite.resetColor();
                }
            }
        }
    }
}
