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
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SupporterScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;

public class WndSupportPrompt extends Window {

    protected static final int WIDTH_P = 120;
    protected static final int WIDTH_L = 200;

    public WndSupportPrompt() {

        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        IconTitle title = new IconTitle(Icons.get(Icons.SHPX), Messages.get(WndSupportPrompt.class, "title"));
        title.setRect(0, 0, width, 0);
        add(title);

        String message = Messages.get(WndSupportPrompt.class, "intro");
        message += "\n\n" + Messages.get(SupporterScene.class, "patreon_msg");
        // if (Messages.lang() != Languages.ENGLISH) {
        //     message += "\n" + Messages.get(SupporterScene.class, "patreon_english");
        // }
        message += "\n- yantianyv";

        RenderedTextBlock text = PixelScene.renderTextBlock(6);
        text.text(message, width);
        text.setPos(title.left(), title.bottom() + 4);
        add(text);

        RedButton link = new RedButton(Messages.get(SupporterScene.class, "supporter_link")) {
            @Override
            protected void onClick() {
                super.onClick();
                String link = "https://www.patreon.com/ShatteredPixel";
                //tracking codes, so that the website knows where this pageview came from
                link += "?utm_source=shatteredpd";
                link += "&utm_medium=supporter_prompt";
                link += "&utm_campaign=ingame_link";
                ShatteredPixelDungeon.platform.openURI(link);
                SPDSettings.supportNagged(true);
                WndSupportPrompt.super.hide();
            }
        };
        link.setRect(0, text.bottom() + 4, width, 22);
        add(link);

        RedButton link_ringed = new RedButton(Messages.get(SupporterScene.class, "ringed_link")) {
            @Override
            protected void onClick() {
                super.onClick();
                String link_ringed = "https://qm.qq.com/q/MO8PYNvUyc";
                ShatteredPixelDungeon.platform.openURI(link_ringed);
                SPDSettings.supportNagged(true);
                WndSupportPrompt.super.hide();
            }
        };
        link_ringed.setRect(0, link.bottom() + 2, width, 22);
        add(link_ringed);

        RedButton link_ringed_ad = new RedButton(Messages.get(SupporterScene.class, "ringed_link_ad")) {
            @Override
            protected void onClick() {
                super.onClick();
                String link_ringed_ad = "https://www.123684.com/s/xyGhTd-j0GJH";
                ShatteredPixelDungeon.platform.openURI(link_ringed_ad);
                SPDSettings.supportNagged(true);
                WndSupportPrompt.super.hide();
            }
        };
        link_ringed_ad.setRect(0, link_ringed.bottom() + 2, width, 22);
        add(link_ringed_ad);

        resize(width, (int) link_ringed_ad.bottom());

    }

    @Override
    public void hide() {
        //do nothing, have to close via the close button
    }
}
