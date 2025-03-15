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
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;

public class DangerIndicator extends Tag {

    public static final int COLOR = 0xC03838;

    private BitmapText number;
    private Image icon;

    private int enemyIndex = 0;

    private int lastNumber = -1;
    private boolean lastI = false;

    public static int HEIGHT = 16;

    public DangerIndicator() {
        super(COLOR);

        setSize(SIZE, HEIGHT);

        visible = false;
    }

    @Override
    public GameAction keyAction() {
        return SPDAction.CYCLE;
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        number = new BitmapText(PixelScene.pixelFont);
        add(number);

        icon = Icons.SKULL.get();
        add(icon);
    }

    @Override
    protected void layout() {
        super.layout();

        icon.x = right() - 10;
        icon.y = y + (height - icon.height) / 2;

        placeNumber();
    }

    private void placeNumber() {
        number.x = right() - 11 - number.width();
        number.y = y + (height - number.baseLine()) / 2f;
        PixelScene.align(number);
    }

    @Override
    public void update() {

        if (Dungeon.hero.isAlive()) {
            // 读取怪物数量
            int inv = Dungeon.hero.invisibilityEnemies();
            int v = Dungeon.hero.visibleEnemies() - inv;
            // 如果数量发生变化或存在隐形怪，更新显示
            if (v != lastNumber || inv != 0 != lastI) {
                // 更新最近数量
                lastNumber = v;
                lastI = inv != 0;
                // 存在可见怪
                if (visible = lastNumber > 0) {
                    // 输出可见怪数量
                    number.text(Integer.toString(lastNumber));
                    // 如果存在隐形怪，则用加号提示
                    if (inv > 0) {
                        number.text(number.text() + "+");
                    }
                    flash();
                }// 只有隐形怪，则用感叹号提示
                else if (visible = inv > 0) {
                    number.text(" ! ");
                    flash();
                    Dungeon.hero.sprite.showAlert();
                }
                // 更新显示
                number.measure();
                placeNumber();
            }
            if (inv == 0) {
                Dungeon.hero.sprite.hideAlert();
            }
        } else {
            visible = false;
        }

        super.update();
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (Dungeon.hero.visibleEnemies() > 0) {

            Mob target = Dungeon.hero.visibleEnemy(++enemyIndex);

            QuickSlotButton.target(target);
            if (Dungeon.hero.canAttack(target)) {
                AttackIndicator.target(target);
            }

            if (Dungeon.hero.curAction == null && target.sprite != null && target.buff(Invisibility.class) == null) {
                Camera.main.panFollow(target.sprite, 5f);
            } else {
                Hero hero = Dungeon.hero;
                Camera.main.panFollow(hero.sprite, 5f);
            }
        }
    }

    @Override
    protected String hoverText() {
        return Messages.titleCase(Messages.get(WndKeyBindings.class, "tag_danger"));
    }
}
