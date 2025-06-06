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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndCheat extends Window {

    private static final int WIDTH = 120;
    private static final int TTL_HEIGHT = 16;
    private static final int BTN_HEIGHT = 16;
    private static final int GAP = 1;

    private CheckBox checkBox;

    public WndCheat() {

        super();

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 12);
        title.hardlight(TITLE_COLOR);
        title.setPos(
                (WIDTH - title.width()) / 2,
                (TTL_HEIGHT - title.height()) / 2
        );
        PixelScene.align(title);
        add(title);

        checkBox = new CheckBox(Messages.titleCase(Messages.get(Challenges.class, "unlock_cheat"))) {
            @Override
            protected void onClick() {
                super.onClick();
                int value = SPDSettings.challenges();
                if (checked()) {
                    value |= Challenges.UNLOCK_CHEAT;
                } else {
                    value &= ~Challenges.UNLOCK_CHEAT;
                }
                SPDSettings.challenges(value);
            }
        };
        checkBox.checked((SPDSettings.challenges() & Challenges.UNLOCK_CHEAT) != 0);
        checkBox.setRect(0, TTL_HEIGHT + GAP, WIDTH - 16, BTN_HEIGHT);
        add(checkBox);

        resize(WIDTH, (int) checkBox.bottom());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
