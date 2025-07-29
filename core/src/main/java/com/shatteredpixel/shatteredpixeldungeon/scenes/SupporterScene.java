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
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

public class SupporterScene extends PixelScene {

    private static final int BTN_HEIGHT = 22;
    private static final int GAP = 2;

    @Override
    public void create() {
        super.create();

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;

        int elementWidth = PixelScene.landscape() ? 202 : 120;

        Archs archs = new Archs();
        archs.setSize(w, h);
        add(archs);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(w - btnExit.width(), 0);
        add(btnExit);

        IconTitle title = new IconTitle(Icons.GOLD.get(), Messages.get(this, "title"));
        title.setSize(200, 0);
        title.setPos(
                (w - title.reqWidth()) / 2f,
                (20 - title.height()) / 2f
        );
        align(title);
        add(title);

        SupporterMessage msg = new SupporterMessage();
        msg.setSize(elementWidth, 0);
        add(msg);
        // 破碎的支持页面
        StyledButton link = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "supporter_link")) {
            @Override
            protected void onClick() {
                super.onClick();
                String link = "https://www.patreon.com/ShatteredPixel";
                //tracking codes, so that the website knows where this pageview came from
                link += "?utm_source=shatteredpd";
                link += "&utm_medium=supporter_page";
                link += "&utm_campaign=ingame_link";
                ShatteredPixelDungeon.platform.openURI(link);
            }
        };
        link.icon(Icons.get(Icons.GOLD));
        link.textColor(Window.TITLE_COLOR);
        link.setSize(elementWidth, BTN_HEIGHT);
        add(link);

        // 戒指地牢群
        StyledButton link_ringed = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "ringed_link")) {
            @Override
            protected void onClick() {
                super.onClick();
                String link_ringed = "https://qm.qq.com/q/MO8PYNvUyc";
                ShatteredPixelDungeon.platform.openURI(link_ringed);
            }
        };
        link_ringed.icon(Icons.get(Icons.RING_STRING));
        link_ringed.textColor(Window.TITLE_COLOR);
        link_ringed.setSize(elementWidth, BTN_HEIGHT);
        add(link_ringed);

        // 支持戒指地牢
        StyledButton link_ringed_ad = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "ringed_link_ad")) {
            @Override
            protected void onClick() {
                super.onClick();
                String link_ringed_ad = "https://www.123684.com/s/xyGhTd-j0GJH";
                ShatteredPixelDungeon.platform.openURI(link_ringed_ad);
            }
        };
        link_ringed_ad.icon(Icons.get(Icons.CHALLENGE_COLOR));
        link_ringed_ad.textColor(Window.TITLE_COLOR);
        link_ringed_ad.setSize(elementWidth, BTN_HEIGHT);
        add(link_ringed_ad);

        float elementHeight = msg.height() + 3 * BTN_HEIGHT + 3 * GAP;

        float top = 16 + (h - 16 - elementHeight) / 2f;
        float left = (w - elementWidth) / 2f;

        msg.setPos(left, top);
        align(msg);

        link.setPos(left, msg.bottom() + GAP);
        align(link);

        link_ringed.setPos(left, link.bottom() + GAP);
        align(link_ringed);

        link_ringed_ad.setPos(left, link_ringed.bottom() + GAP);
        align(link_ringed_ad);

    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene.class);
    }

    private static class SupporterMessage extends Component {

        NinePatch bg;
        RenderedTextBlock text;
        Image icon;

        @Override
        protected void createChildren() {
            bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
            add(bg);

            String message = Messages.get(SupporterScene.class, "intro");
            message += "\n\n" + Messages.get(SupporterScene.class, "patreon_msg");

            message += "\n\n- yantianyv";

            text = PixelScene.renderTextBlock(message, 6);
            add(text);

            icon = Icons.get(Icons.RING_STRING);
            add(icon);

        }

        @Override
        protected void layout() {
            bg.x = x;
            bg.y = y;

            text.maxWidth((int) width - bg.marginHor());
            text.setPos(x + bg.marginLeft(), y + bg.marginTop() + 1);

            icon.y = text.bottom() - icon.height() + 4;
            icon.x = x + 40;

            height = (text.bottom() + 3) - y;

            height += bg.marginBottom();

            bg.size(width, height);

        }

    }

}
