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
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class ChaliceOfBlood extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_CHALICE1;

        levelCap = 10;

        charge = 0;

        chargeCap = 100;

        defaultAction = AC_PRAY;

        partialCharge = 0;

    }

    public static final String AC_PRICK = "PRICK";
    public static final String AC_PRAY = "PRAY";
    static int last_charge = 0;
    static int prick_cooldown = 0;

    @Override    // 添加按钮选项
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero)
                && !cursed
                && !hero.isInvulnerable(getClass())
                && hero.buff(MagicImmune.class) == null
                && prick_cooldown <= 0) {
            actions.add(AC_PRICK);
        }
        if (charge >= 10 && level() >= 1) {
            actions.add(AC_PRAY);
        }
        return actions;
    }

    @Override    // 执行按钮操作
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_PRICK)) {

            int damage = 5 + 3 * (level() * level());

            if (damage > hero.HP * 0.75 && level() < 10) {

                GameScene.show(
                        new WndOptions(new ItemSprite(this),
                                Messages.titleCase(name()),
                                Messages.get(this, "prick_warn"),
                                Messages.get(this, "yes"),
                                Messages.get(this, "no")) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            prick(Dungeon.hero);
                        }
                    }
                }
                );

            } else {
                prick(hero);
            }
        } else if (action.equals(AC_PRAY)) {
            pray(hero);
        }
    }

    // 血祭
    private void prick(Hero hero) {
        // 计算基础伤害
        int damage = 5 + 3 * (level() * level());
        // 计算吸收伤害
        Earthroot.Armor armor = hero.buff(Earthroot.Armor.class);
        if (armor != null) {
            damage = armor.absorb(damage);
        }
        // 计算护甲伤害
        WandOfLivingEarth.RockArmor rockArmor = hero.buff(WandOfLivingEarth.RockArmor.class);
        if (rockArmor != null) {
            damage = rockArmor.absorb(damage);
        }
        // 计算闪避伤害
        damage -= hero.drRoll();
        // 满级伤害替换

        hero.sprite.operate(hero.pos);
        hero.busy();
        hero.spend(3f);
        GLog.w(Messages.get(this, "onprick"));
        if (damage <= 0) {
            damage = 1;
        } else {
            Sample.INSTANCE.play(Assets.Sounds.CURSED);
            hero.sprite.emitter().burst(ShadowParticle.CURSE, 4 + (damage / 10));
        }
        if (level() >= 10) {
            damage = Dungeon.hero.HP / 2;
            prick_cooldown = damage > 100 ? 100 : damage;
        } else {
            prick_cooldown = 0;
        }
        hero.damage(damage, this);

        if (!hero.isAlive()) {
            Badges.validateDeathFromFriendlyMagic();
            Dungeon.fail(this);
            GLog.n(Messages.get(this, "ondeath"));

        } else {
            if (level() < levelCap) {
                upgrade();
            }
            Catalog.countUse(getClass());
            int newcharge = charge + damage / 3 + 1;
            charge = newcharge > chargeCap ? chargeCap : newcharge;
        }
    }

    // 新增主动技能 祈祷
    private void pray(Hero hero) {
        if (charge >= 10 && level() >= 1 && hero.buff(Healing.class) == null && hero.HP < hero.HT && !cursed) {
            int extra_level = level() - levelCap;
            extra_level = extra_level > 0 ? extra_level : 0;
            last_charge = charge;
            GLog.p(Messages.get(this, "onpray"));
            Buff.affect(hero, Healing.class).setHeal(((level() + 1) * (level() - extra_level) / 2 + extra_level * (hero.HT - hero.HP) / 300) * charge / 100 + 1, 0.1f, 0);
            charge = 0;
            status();
            updateQuickslot();
            Talent.onArtifactUsed(Dungeon.hero);
            hero.sprite.operate(hero.pos);

        } else {
            GLog.w(Messages.get(this, "pray_fail"));
        }

    }

    @Override
    public Item upgrade() {
        if (level() >= 6) {
            image = ItemSpriteSheet.ARTIFACT_CHALICE3;
        } else if (level() >= 2) {
            image = ItemSpriteSheet.ARTIFACT_CHALICE2;
        }
        return super.upgrade();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (level() >= 7) {
            image = ItemSpriteSheet.ARTIFACT_CHALICE3;
        } else if (level() >= 3) {
            image = ItemSpriteSheet.ARTIFACT_CHALICE2;
        }
    }

    @Override

    protected ArtifactBuff passiveBuff() {
        return new chaliceRegen();
    }

    // 定义充能动作
    @Override
    public void charge(Hero target, float amount) {
        if (cursed || target.buff(MagicImmune.class) != null) {
            return;
        }

        if (charge < chargeCap) {
            partialCharge += amount;
            while (partialCharge >= 1f) {
                charge++;
                partialCharge--;
            }
            if (charge >= chargeCap) {
                partialCharge = 0;
                charge = chargeCap;
            }
            updateQuickslot();
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            desc += "\n\n";
            if (cursed) {
                desc += Messages.get(this, "desc_cursed");
            } else if (level() == 0) {
                desc += Messages.get(this, "desc_1");
            } else if (level() < levelCap) {
                desc += Messages.get(this, "desc_2");
            } else {
                desc += Messages.get(this, "desc_3");
            }
        }

        return desc;
    }

    public static int reg_level() {
        int reg_level = (Dungeon.hero.buff(ChaliceOfBlood.chaliceRegen.class).itemLevel() * last_charge) / 100;
        reg_level = Math.min(reg_level, 10);
        return reg_level;
    }

    public class chaliceRegen extends ArtifactBuff {
        //see Regeneration.class for effect

        public void gainCharge(float chargeGain) {
            if (cursed || target.buff(MagicImmune.class) != null) {
                return;
            }

            //generates 2 energy every hero level, +1 energy per toolkit level
            //to a max of 12 energy per hero level
            //This means that energy absorbed into the kit is recovered in 5 hero levels
            chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
            partialCharge += chargeGain;
            //charge is in increments of 1 energy.
            while (partialCharge >= 1) {
                charge += 1;
                partialCharge -= 1;
                last_charge = charge;

                updateQuickslot();
                if (charge >= chargeCap) {
                    charge = chargeCap;
                    partialCharge = 0;
                    break;
                }
            }

            if (prick_cooldown > 0) {
                prick_cooldown--;
                if (prick_cooldown == 0) {
                    GLog.w(Messages.get(ChaliceOfBlood.this, "prick_ready"));
                }
            }
        }
    }

}
