package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Random;

public class BurningElement extends ElementBuff {
    
    private int timer = 0;
    private int groundFireTimer = 0; // 用于限制点燃地面的频率
    
    {
        type = buffType.NEGATIVE;
        announced = true;
        element = Element.PYRO; // 明确设置火元素类型
    }
    
    @Override
    public boolean act() {
        // 水域转换逻辑
        if (Dungeon.level.water[target.pos] && !target.flying) {
            // 转换为普通火元素附着
            if (target.buff(Burning.class) == null) {
                fx(false); // 移除燃烧特效
            }
            detach();
            ElementBuff.apply(Element.PYRO, null, target, quantity);
            return true;
        }
        
        // 每0.1秒衰减1%（加快衰减速度）
        quantity *= 0.99f;
        
        // 每秒执行伤害（约10次调用）
        if (timer++ >= 10) {
            // 检查目标是否免疫燃烧伤害（如御火刻印）
            if (target.isImmune(Burning.class)) {
                // 如果免疫，不造成伤害，移除燃烧特效，但继续衰减
                fx(false);
                timer = 0;
            } else {
                int dmg = Random.NormalIntRange(1, 3 + Dungeon.scalingDepth()/4);
                if (target.isAlive()) {
                    target.damage(dmg, this);
                }
                timer = 0;
            }
        }
        
        // 限制点燃地面的频率：每秒只检查一次，且只在没有火焰时点燃
        groundFireTimer++;
        if (groundFireTimer >= 10 && quantity > 0 && Dungeon.level.flamable[target.pos]) {
            // 检查该位置是否已经有火焰
            if (Blob.volumeAt(target.pos, Fire.class) == 0) {
                GameScene.add(Blob.seed(target.pos, Math.min((int)quantity, 4), Fire.class));
            }
            groundFireTimer = 0;
        }
        
        // 检查移除条件
        if (quantity < 0.1f) {
            if (target.buff(Burning.class) == null) {
                fx(false); // 移除燃烧特效
            }
            detach();
            return true;
        }
        
        spend(0.1f);
        return true;
    }
    
    // 增强燃烧效果
    public void reignite(float amount) {
        quantity += amount;
        if (quantity > 100f) quantity = 100f;
        fx(true);
    }
    
    @Override
    public void fx(boolean on) {
        if (target.sprite != null) {
            if (on) target.sprite.add(CharSprite.State.BURNING);
            else target.sprite.remove(CharSprite.State.BURNING);
        }
    }
    
    @Override
    public int icon() {
        return BuffIndicator.FIRE;
    }
    
    @Override
    public String desc() {
        return Messages.get(this, "desc", (int)quantity);
    }
}
