package com.shatteredpixel.shatteredpixeldungeon.items.rings.specialrings;

import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public abstract class SpecialRing extends Ring {

    {
        anonymize(); // 更推荐调用方法而非直接设字段
        image = ItemSpriteSheet.RING_VOID;
        unique = true;
    }

    // 3. 完全跳过宝石系统的 reset 逻辑
    @Override
    public void reset() {
        // 不调用 super.reset()，避免父类重置宝石关联字段
        cursed = false;
        cursedKnown = false;
        levelKnown = false;
    }

    // 4. 强制使用本地化名称（不依赖宝石键）
    @Override
    public String name() {
        return Messages.get(this, "name");
    }

    // 5. 确保始终识别（即使未调用 setKnown）
    @Override
    public boolean isKnown() {
        return true;
    }

    // 6. 禁用宝石系统的识别逻辑
    @Override
    public void setKnown() {
        // 空实现，避免被添加到 handler.known()
    }

    // 为了避免有无聊的人给特殊戒指加诅咒，重写等级判定
    @Override
    public int soloBonus() {
        return this.level() + 1;
    }

    @Override
    public int soloBuffedBonus() {
        return this.buffedLvl() + 1;
    }

    @Override
    public boolean isUpgradable() {
        return level() > 5;
    }

}
