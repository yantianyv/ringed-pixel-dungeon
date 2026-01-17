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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChanges;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChangesTabbed;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_1_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_2_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_3_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_4_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_5_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_6_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_7_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_8_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_9_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v1_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v2_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v3_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.Ringed_Changes;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class ChangesScene extends PixelScene {
	
	public static int changesSelected = 0;

	private NinePatch rightPanel;
	private ScrollPane rightScroll;
	private IconTitle changeTitle;
	private RenderedTextBlock changeBody;
	
	@Override
	public void create() {
		super.create();

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

		int w = Camera.main.width;
		int h = Camera.main.height;

		IconTitle title = new IconTitle(Icons.CHANGES.get(), Messages.get(this, "title"));
		title.setSize(200, 0);
		title.setPos(
				(w - title.reqWidth()) / 2f,
				(20 - title.height()) / 2f
		);
		align(title);
		add(title);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);

		// 计算tab栏总宽度用于面板布局
		float panelTabWidth = 22;
		float panelTabSpacing = 1;
		String[] panelVersions = {"ringed", "3.X", "2.X", "1.X", "0.9", "0.8", "0.7", "0.6", "0.5"};
		float panelAvailableWidth = w - 40;
		float panelTotalTabWidth = panelVersions.length * panelTabWidth + (panelVersions.length - 1) * panelTabSpacing;
		
		if (panelTotalTabWidth > panelAvailableWidth) {
			panelTabWidth = (panelAvailableWidth - (panelVersions.length - 1) * panelTabSpacing) / panelVersions.length;
			if (panelTabWidth < 18) panelTabWidth = 18;
			panelTotalTabWidth = panelVersions.length * panelTabWidth + (panelVersions.length - 1) * panelTabSpacing;
		}
		
		// 使用tab栏宽度作为面板宽度
		int pw = (int)panelTotalTabWidth + panel.marginLeft() + panel.marginRight() + 4;
		int ph = h - 36;

		if (h >= PixelScene.MIN_HEIGHT_FULL && w >= 300) {
			panel.size( pw, ph );
			panel.x = (w - pw) / 2f - pw/2 - 1;
			panel.y = 20;

			rightPanel = Chrome.get(Chrome.Type.TOAST);
			rightPanel.size( pw, ph );
			rightPanel.x = (w - pw) / 2f + pw/2 + 1;
			rightPanel.y = 20;
			add(rightPanel);

			rightScroll = new ScrollPane(new Component());
			add(rightScroll);
			rightScroll.setRect(
					rightPanel.x + rightPanel.marginLeft(),
					rightPanel.y + rightPanel.marginTop()-1,
					rightPanel.innerWidth() + 2,
					rightPanel.innerHeight() + 2);
			rightScroll.scrollTo(0, 0);

			changeTitle = new IconTitle(Icons.get(Icons.CHANGES), Messages.get(this, "right_title"));
			changeTitle.setPos(0, 1);
			changeTitle.setSize(pw, 20);
			rightScroll.content().add(changeTitle);

			String body = Messages.get(this, "right_body");

			changeBody = PixelScene.renderTextBlock(body, 6);
			changeBody.maxWidth(pw - panel.marginHor());
			changeBody.setPos(0, changeTitle.bottom()+2);
			rightScroll.content().add(changeBody);

		} else {
			panel.size( pw, ph );
			panel.x = (w - pw) / 2f;
			panel.y = 20;
		}
		align( panel );
		add( panel );
		
		final ArrayList<ChangeInfo> changeInfos = new ArrayList<>();

		// 对于Ringed更新日志，不显示语言警告
		if (Messages.lang() != Languages.ENGLISH && changesSelected != 8){
			ChangeInfo langWarn = new ChangeInfo("", true, Messages.get(this, "lang_warn"));
			langWarn.hardlight(CharSprite.WARNING);
			changeInfos.add(langWarn);
		}
		
		switch (changesSelected){
			case 0: default:
				v3_X_Changes.addAllChanges(changeInfos);
				break;
			case 1:
				v2_X_Changes.addAllChanges(changeInfos);
				break;
			case 2:
				v1_X_Changes.addAllChanges(changeInfos);
				break;
			case 3:
				v0_9_X_Changes.addAllChanges(changeInfos);
				break;
			case 4:
				v0_8_X_Changes.addAllChanges(changeInfos);
				break;
			case 5:
				v0_7_X_Changes.addAllChanges(changeInfos);
				break;
			case 6:
				v0_6_X_Changes.addAllChanges(changeInfos);
				break;
			case 7:
				v0_5_X_Changes.addAllChanges(changeInfos);
				v0_4_X_Changes.addAllChanges(changeInfos);
				v0_3_X_Changes.addAllChanges(changeInfos);
				v0_2_X_Changes.addAllChanges(changeInfos);
				v0_1_X_Changes.addAllChanges(changeInfos);
				break;
			case 8:
				Ringed_Changes.addAllChanges(changeInfos);
				break;
		}

		ScrollPane list = new ScrollPane( new Component() ){

			@Override
			public void onClick(float x, float y) {
				for (ChangeInfo info : changeInfos){
					if (info.onClick( x, y )){
						return;
					}
				}
			}

		};
		add( list );

		Component content = list.content();
		content.clear();

		float posY = 0;
		for (ChangeInfo info : changeInfos){
			info.setRect(0, posY, panel.innerWidth(), 0);
			content.add(info);
			posY = info.bottom();
		}

		content.setSize( panel.innerWidth(), (int)Math.ceil(posY) );

		list.setRect(
				panel.x + panel.marginLeft(),
				panel.y + panel.marginTop() - 1,
				panel.innerWidth() + 2,
				panel.innerHeight() + 2);
		list.scrollTo(0, 0);

		// 优化tab栏布局 - 防止超出屏幕边界
		float tabWidth = 22; // 减小按钮宽度
		float tabHeight = 19;
		float tabSpacing = 1; // 减小间距
		float availableWidth = Camera.main.width - 40; // 留出左右边距
		
		// 计算实际需要的按钮数量和宽度
		String[] versions = {"ringed", "3.X", "2.X", "1.X", "0.9", "0.8", "0.7", "0.6", "0.5"};
		int[] indices = {8, 0, 1, 2, 3, 4, 5, 6, 7}; // Ringed是8，其他按顺序
		
		// 动态计算每个按钮的宽度
		float totalTabWidth = versions.length * tabWidth + (versions.length - 1) * tabSpacing;
		
		// 如果总宽度超出可用宽度，进一步缩小
		if (totalTabWidth > availableWidth) {
			tabWidth = (availableWidth - (versions.length - 1) * tabSpacing) / versions.length;
			if (tabWidth < 18) tabWidth = 18; // 最小宽度
		}
		
		// 重新计算总宽度和起始位置
		totalTabWidth = versions.length * tabWidth + (versions.length - 1) * tabSpacing;
		float startX = (Camera.main.width - totalTabWidth) / 2f;
		
		// 创建所有按钮
		for (int i = 0; i < versions.length; i++) {
			final int idx = indices[i];
			final String version = versions[i];
			
			StyledButton btn = new StyledButton(Chrome.Type.GREY_BUTTON_TR, version, 8){
				@Override
				protected void onClick() {
					super.onClick();
					if (changesSelected != idx) {
						changesSelected = idx;
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			};
			if (changesSelected != idx) btn.textColor( 0xBBBBBB );
			float xPos = startX + i * (tabWidth + tabSpacing);
			btn.setRect(xPos, list.bottom(), tabWidth, changesSelected == idx ? tabHeight : 15);
			addToBack(btn);
		}

		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );

		fadeIn();
	}

	private void updateChangesText(Image icon, String title, String... messages){
		if (changeTitle != null){
			changeTitle.icon(icon);
			changeTitle.label(title);
			changeTitle.setPos(changeTitle.left(), changeTitle.top());

			String message = "";
			for (int i = 0; i < messages.length; i++){
				message += messages[i];
				if (i != messages.length-1){
					message += "\n\n";
				}
			}
			changeBody.text(message);
			rightScroll.content().setSize(rightScroll.width(), changeBody.bottom()+2);
			rightScroll.setSize(rightScroll.width(), rightScroll.height());
			rightScroll.scrollTo(0, 0);

		} else {
			if (messages.length == 1) {
				addToFront(new WndChanges(icon, title, messages[0]));
			} else {
				addToFront(new WndChangesTabbed(icon, title, messages));
			}
		}
	}

	public static void showChangeInfo(Image icon, String title, String... messages){
		Scene s = ShatteredPixelDungeon.scene();
		if (s instanceof ChangesScene){
			((ChangesScene) s).updateChangesText(icon, title, messages);
			return;
		}
		if (messages.length == 1) {
			s.addToFront(new WndChanges(icon, title, messages[0]));
		} else {
			s.addToFront(new WndChangesTabbed(icon, title, messages));
		}
	}
	
	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade(TitleScene.class);
	}

}
