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
        
        // 每0.1秒衰减0.1%
        quantity *= 0.999f;
        
        // 每秒执行伤害（约10次调用）
        if (timer++ >= 10) {
            int dmg = Random.NormalIntRange(1, 3 + Dungeon.scalingDepth()/4);
            if (target.isAlive()) {
                target.damage(dmg, this);
            }
            timer = 0;
        }
        
        // 点燃地面
        if (quantity > 0 && Dungeon.level.flamable[target.pos]) {
            GameScene.add(Blob.seed(target.pos, (int)quantity, Fire.class));
        }
        
        // 检查移除条件
        if (quantity < 0.1f) {
            fx(false);
            detach();
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
