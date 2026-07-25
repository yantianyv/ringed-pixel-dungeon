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
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndAppreciationCode;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

public class SupporterScene extends PixelScene {

	private static final int BTN_HEIGHT = 22;
	private static final int GAP = 2;

	public static final String[] APPRECIATION_KEYS = {
			"milktea", "bbq", "burger", "naan", "donate", "cup", "cat",
			"flower", "redpacket", "token",
			"feast", "stewed", "mystery_meat", "carpaccio", "candy", "fish", "pie",
			"cake1", "cake2", "cake3", "egg", "blob", "bone_soup"
	};

	public static final int[] APPRECIATION_ICONS = {
			ItemSpriteSheet.SPARKLING_POTION,
			ItemSpriteSheet.STEAK,
			ItemSpriteSheet.CRAZY_BURGER,
			ItemSpriteSheet.PASTY,
			ItemSpriteSheet.WAND_TRANSFUSION,
			ItemSpriteSheet.ARTIFACT_CHALICE1,
			ItemSpriteSheet.HONEYPOT,
			ItemSpriteSheet.ARTIFACT_ROSE1,
			ItemSpriteSheet.GOLD,
			ItemSpriteSheet.TOKEN,
			ItemSpriteSheet.MEAT_PIE,
			ItemSpriteSheet.STEWED,
			ItemSpriteSheet.MEAT,
			ItemSpriteSheet.CARPACCIO,
			ItemSpriteSheet.CANDY_CANE,
			ItemSpriteSheet.STEAMED_FISH,
			ItemSpriteSheet.PUMPKIN_PIE,
			ItemSpriteSheet.RINGED_CAKE,
			ItemSpriteSheet.SHATTERED_CAKE,
			ItemSpriteSheet.VANILLA_CAKE,
			ItemSpriteSheet.EASTER_EGG,
			ItemSpriteSheet.BLOB,
			ItemSpriteSheet.RAT_SKULL
	};

	public static int randomAppreciationIndex(){
		return Random.Int(APPRECIATION_KEYS.length);
	}

	@Override
	public void create() {
		super.create();

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		int colGap = 8;
		int minColWidth = 100;
		boolean twoColumn = PixelScene.landscape() && w >= 2 * minColWidth + colGap + 20;
		int elementWidth = twoColumn ? (w - colGap - 20) / 2 : Math.min(120, w - 20);

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

		StyledButton link = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "supporter_link")) {
			@Override
			protected void onClick() {
				super.onClick();
				String link = "https://www.patreon.com/ShatteredPixel";
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

		int appreciationIndex = Random.Int(APPRECIATION_KEYS.length);

		StyledButton link_appreciation = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, APPRECIATION_KEYS[appreciationIndex])) {
			@Override
			protected void onClick() {
				super.onClick();
				addToFront(new WndAppreciationCode());
			}
		};
		link_appreciation.icon(new ItemSprite(APPRECIATION_ICONS[appreciationIndex]));
		link_appreciation.textColor(Window.TITLE_COLOR);
		link_appreciation.setSize(elementWidth, BTN_HEIGHT);
		add(link_appreciation);

		float buttonsHeight = 4 * BTN_HEIGHT + 3 * GAP;
		float elementHeight = twoColumn
				? Math.max(msg.height(), buttonsHeight)
				: msg.height() + buttonsHeight + GAP;

		float top = 16 + (h - 16 - elementHeight) / 2f;
		float left = twoColumn
				? (w - (2 * elementWidth + colGap)) / 2f
				: (w - elementWidth) / 2f;

		msg.setPos(left, top);
		align(msg);

		if (twoColumn) {
			float right = left + elementWidth + colGap;
			link.setPos(right, top);
			link_ringed.setPos(right, link.bottom() + GAP);
			link_ringed_ad.setPos(right, link_ringed.bottom() + GAP);
			link_appreciation.setPos(right, link_ringed_ad.bottom() + GAP);
		} else {
			link.setPos(left, msg.bottom() + GAP);
			link_ringed.setPos(left, link.bottom() + GAP);
			link_ringed_ad.setPos(left, link_ringed.bottom() + GAP);
			link_appreciation.setPos(left, link_ringed_ad.bottom() + GAP);
		}
		align(link);
		align(link_ringed);
		align(link_ringed_ad);
		align(link_appreciation);
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
