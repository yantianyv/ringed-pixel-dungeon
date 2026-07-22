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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

public class WndAppreciationCode extends Window {

	private static final int WIDTH_P = 130;
	private static final int WIDTH_L = 220;

	public WndAppreciationCode() {

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
		float maxImgWidth = width - 10;

		IconTitle title = new IconTitle(
				new ItemSprite(new UnstableBrew()),
				Messages.get(this, "title"));
		title.setRect(0, 0, width, 0);
		add(title);

		Image code = new Image(Assets.Interfaces.APPRECIATION_CODE);
		float maxContentHeight = PixelScene.uiCamera.height - 10 - 12;
		float maxImgHeight = maxContentHeight - title.bottom() - 22 - 8;
		if (maxImgHeight < 10) maxImgHeight = 10;
		float scale = Math.min(1f, Math.min(
				maxImgWidth / code.width(),
				maxImgHeight / code.height()));
		code.scale.set(scale, scale);
		code.x = (width - code.width()) / 2f;
		code.y = title.bottom() + 4;
		add(code);

		RedButton save = new RedButton(Messages.get(this, "save")) {
			@Override
			protected void onClick() {
				super.onClick();
				String path = ShatteredPixelDungeon.platform.saveImageWithDialog(
						Assets.Interfaces.APPRECIATION_CODE, "赞赏码.jpg");
				if (path != null) {
					ShatteredPixelDungeon.scene().addToFront(new WndMessage(
							Messages.format(Messages.get(WndAppreciationCode.class, "saved"), path)));
				} else {
					ShatteredPixelDungeon.scene().addToFront(new WndMessage(
							Messages.get(WndAppreciationCode.class, "save_failed")));
				}
			}
		};
		save.setRect(0, code.y + code.height() + 4, width, 22);
		add(save);

		resize(width, (int) Math.min(save.bottom(), maxContentHeight));
		boundOffsetWithMargin(5);
	}
}
