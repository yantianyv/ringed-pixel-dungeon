package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FrostElement extends ElementBuff {

    private static final float TICK = 1f; // 每回合时间
    public int shatterDamage = 0; // 存储破冰伤害值
    
    {
        // 设置为冰元素
        element = Element.CRYO;
        // 设为负面buff
        type = buffType.NEGATIVE;
        // 需要公告
        announced = true;
        
        // 免疫寒冷效果
        immunities.add(Chill.class);
    }
    
    @Override
    public boolean act() {
        // 每回合减少剩余持续时间
        quantity -= 1;
        
        // 当持续时间结束时解除效果
        if (quantity <= 0) {
            detach();
            return true;
        }
        
        // 设置下一回合
        spend(TICK);
        return true;
    }
    
    @Override
    public void detach() {
        // 在解除效果时造成破冰伤害
        if (shatterDamage > 0) {
            target.damage(shatterDamage, this);
        }
        super.detach();
        // 减少麻痹计数器
        if (target.paralysed > 0) {
            target.paralysed--;
        }
        // 如果目标在水中，添加寒冷效果
        if (Dungeon.level.water[target.pos]) {
            Buff.prolong(target, Chill.class, Chill.DURATION / 2f);
        }
    }

    @Override
    public boolean attachTo(Char target) {
        // 移除燃烧和寒冷效果
        Buff.detach(target, Burning.class);
        Buff.detach(target, Chill.class);

        if (super.attachTo(target)) {
            // 增加麻痹计数器
            target.paralysed++;
            
            // 启动计时器（首次执行act）
            spend(TICK);
            

            return true;
        } else {
            return false;
        }
    }


    @Override
    public int icon() {
        return BuffIndicator.FROST;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0f, 0.75f, 1f); // 冰蓝色调
    }

    @Override
    public void fx(boolean on) {
        if (target != null && target.sprite != null) {
            if (on) {
                target.sprite.add(CharSprite.State.FROZEN);
                target.sprite.add(CharSprite.State.PARALYSED);
            } else {
                target.sprite.remove(CharSprite.State.FROZEN);
                if (target.paralysed <= 1) {
                    target.sprite.remove(CharSprite.State.PARALYSED);
                }
            }
        }
    }
}
