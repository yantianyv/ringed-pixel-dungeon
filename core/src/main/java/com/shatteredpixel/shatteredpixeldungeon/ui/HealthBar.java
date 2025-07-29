/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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
package com.shatteredpixel.shatteredpixeldungeon.ui;

import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

public class HealthBar extends Component {

    private static final int COLOR_BG = 0xFFCC0000;
    private static final int COLOR_HP = 0xFF00EE00;
    private static final int COLOR_SHLD = 0xFFFFFFFF;
    private static final int COLOR_DIVIDER = 0xFF888888;

    private static final int HEIGHT = 2;
    private static final int SEGMENT_SIZE = 10;
    private static final int MAX_DIVIDERS = 20; // 限制最大分隔线数量

    private ColorBlock Bg;
    private ColorBlock Shld;
    private ColorBlock Hp;
    private ArrayList<ColorBlock> dividers = new ArrayList<>();

    private float health;
    private float shield;
    private int maxHP;
    private int lastMaxHP = 0; // 缓存上一次的Max HP

    @Override
    protected void createChildren() {
        Bg = new ColorBlock(1, 1, COLOR_BG);
        add(Bg);

        Shld = new ColorBlock(1, 1, COLOR_SHLD);
        add(Shld);

        Hp = new ColorBlock(1, 1, COLOR_HP);
        add(Hp);

        height = HEIGHT;
    }

    @Override
    protected void layout() {
        // 更新血条背景和前景位置
        Bg.x = Shld.x = Hp.x = x;
        Bg.y = Shld.y = Hp.y = y;

        Bg.size(width, height);

        // 逻辑保持四舍五入到最近的像素
        float pixelWidth = width;
        if (camera() != null) {
            pixelWidth *= camera().zoom;
        }
        Shld.size(width * (float) Math.ceil(shield * pixelWidth) / pixelWidth, height);
        Hp.size(width * (float) Math.ceil(health * pixelWidth) / pixelWidth, height);

        // 无论Max HP是否变化，都要更新分隔线位置（因为血条可能移动）
        updateDividersPosition();

        // 仅当Max HP变化时重新计算分隔线数量
        if (maxHP != lastMaxHP) {
            updateDividersCount();
            lastMaxHP = maxHP;
        }
    }

// 仅更新分隔线位置（不增删对象）
    private void updateDividersPosition() {
        if (dividers.isEmpty()) {
            return;
        }

        int totalSegments = (int) Math.ceil((float) maxHP / SEGMENT_SIZE);
        totalSegments = Math.min(totalSegments, MAX_DIVIDERS + 1);
        float segmentWidth = width / (float) totalSegments;

        for (int i = 0; i < dividers.size(); i++) {
            ColorBlock divider = dividers.get(i);
            divider.x = x + (i + 1) * segmentWidth;
            divider.y = y;
        }
    }

// 仅更新分隔线数量（增删对象）
    private void updateDividersCount() {
        int totalSegments = (int) Math.ceil((float) maxHP / SEGMENT_SIZE);
        totalSegments = Math.min(totalSegments, MAX_DIVIDERS + 1);
        int requiredDividers = totalSegments - 1;

        // 移除多余的分隔线
        while (dividers.size() > requiredDividers) {
            ColorBlock removed = dividers.remove(dividers.size() - 1);
            remove(removed);
        }
        // 添加不足的分隔线
        while (dividers.size() < requiredDividers) {
            ColorBlock divider = new ColorBlock(0.3f, 0.5f * height, COLOR_DIVIDER);
            add(divider);
            dividers.add(divider);
        }

        // 新增的分隔线需要设置位置
        updateDividersPosition();
    }

    public void level(float value) {
        level(value, 0f);
    }

    public void level(float health, float shield) {
        this.health = health;
        this.shield = shield;
        layout();
    }

    public void level(Char c) {
        float health = c.HP;
        float shield = c.shielding();
        float max = Math.max(health + shield, c.HT);
        this.maxHP = (int) max;

        level(health / max, (health + shield) / max);
    }
}
