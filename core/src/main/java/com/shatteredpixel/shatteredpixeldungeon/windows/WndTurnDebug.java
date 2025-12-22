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

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.utils.DeviceCompat;

public class WndTurnDebug extends Window {

	private static final int WIDTH	= 120;
	private static final int HEIGHT	= 180;
	private static final int MARGIN	= 4;
	private static final int BTN_HEIGHT = 20;
	private static final int GAP = 2;

	private String debugText;

	public WndTurnDebug() {
		
		super();

		debugText = GameScene.getTurnStateDump("menu");

		RenderedTextBlock info = PixelScene.renderTextBlock( debugText, 6 );
		info.maxWidth(WIDTH - MARGIN * 2);

		ScrollPane sp = new ScrollPane( info );
		add( sp );
		sp.setRect( MARGIN, MARGIN, WIDTH - MARGIN * 2, HEIGHT - MARGIN * 2 - BTN_HEIGHT - GAP );

		RedButton btnCopy = new RedButton( Messages.get(this, "copy") ) {
			@Override
			protected void onClick() {
				copyToClipboard();
			}
		};
		btnCopy.icon(Icons.get(Icons.COPY));
		add( btnCopy );
		btnCopy.setRect( MARGIN, HEIGHT - MARGIN - BTN_HEIGHT, WIDTH - MARGIN * 2, BTN_HEIGHT );

		resize( WIDTH, HEIGHT );
	}

	private void copyToClipboard() {
		if (DeviceCompat.isDesktop()) {
			// Desktop平台使用系统剪贴板
			java.awt.Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.setContents(new java.awt.datatransfer.StringSelection(debugText), null);
		} else {
			// 移动平台使用Gdx剪贴板
			Gdx.app.getClipboard().setContents(debugText);
		}
	}
}




