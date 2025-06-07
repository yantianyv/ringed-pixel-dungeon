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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;

import java.util.ArrayList;

public class StartScene extends PixelScene {

	// 每个保存槽的宽度和高度
	private static final int SLOT_WIDTH = 120;
	private static final int SLOT_HEIGHT = 22;

	@Override
	public void create() {
		super.create();

		// 加载全局徽章和日志
		Badges.loadGlobal();
		Journal.loadGlobal();

		// 隐藏UI相机
		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		// 添加拱门
		Archs archs = new Archs();
		archs.setSize(w, h);
		add(archs);

		// 添加退出按钮
		ExitButton btnExit = new ExitButton();
		btnExit.setPos(w - btnExit.width(), 0);
		add(btnExit);

		// 添加标题
		IconTitle title = new IconTitle(Icons.ENTER.get(), Messages.get(this, "title"));
		title.setSize(200, 0);
		title.setPos(
				(w - title.reqWidth()) / 2f,
				(20 - title.height()) / 2f);
		align(title);
		add(title);

		// 获取所有保存的游戏
		ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();

		// 计算保存槽的数量
		int slotCount = Math.min(GamesInProgress.MAX_SLOTS, games.size() + 1);
		// 计算保存槽之间的间隔
		int slotGap = 10 - slotCount;
		// 计算保存槽的总高度
		int slotsHeight = slotCount * SLOT_HEIGHT + (slotCount - 1) * slotGap;
		// 添加一些额外的空间
		slotsHeight += 14;

		// 如果保存槽的总高度超过了屏幕高度，则减少间隔
		while (slotGap >= 2 && slotsHeight > (h - title.bottom() - 2)) {
			slotGap--;
			slotsHeight -= slotCount - 1;
		}

		// 计算保存槽的y坐标
		float yPos = (h - slotsHeight + title.bottom() + 2) / 2f - 4;
		yPos = Math.max(yPos, title.bottom() + 2);
		float slotLeft = (w - SLOT_WIDTH) / 2f;

		// 遍历所有保存的游戏，并添加保存槽
		for (GamesInProgress.Info game : games) {
			SaveSlotButton existingGame = new SaveSlotButton();
			existingGame.set(game.slot);
			existingGame.setRect(slotLeft, yPos, SLOT_WIDTH, SLOT_HEIGHT);
			yPos += SLOT_HEIGHT + slotGap;
			align(existingGame);
			add(existingGame);

		}

		// 如果保存槽的数量小于最大数量，则添加一个新的保存槽
		if (games.size() < GamesInProgress.MAX_SLOTS) {
			SaveSlotButton newGame = new SaveSlotButton();
			newGame.set(GamesInProgress.firstEmpty());
			newGame.setRect(slotLeft, yPos, SLOT_WIDTH, SLOT_HEIGHT);
			yPos += SLOT_HEIGHT + slotGap;
			align(newGame);
			add(newGame);
		}

		// 设置当前保存槽为0
		GamesInProgress.curSlot = 0;

		// 获取排序方式
		String sortText = "";
		switch (SPDSettings.gamesInProgressSort()) {
			case "level":
				sortText = Messages.get(this, "sort_level");
				break;
			case "last_played":
				sortText = Messages.get(this, "sort_recent");
				break;
		}

		// 添加排序按钮
		StyledButton btnSort = new StyledButton(Chrome.Type.TOAST_TR, sortText, 6) {
			@Override
			protected void onClick() {
				super.onClick();

				// 切换排序方式
				if (SPDSettings.gamesInProgressSort().equals("level")) {
					SPDSettings.gamesInProgressSort("last_played");
				} else {
					SPDSettings.gamesInProgressSort("level");
				}

				// 重新加载场景
				ShatteredPixelDungeon.seamlessResetScene();
			}
		};
		btnSort.textColor(0xCCCCCC);

		// 如果保存槽的数量大于等于2，则添加排序按钮
		if (yPos + 10 > Camera.main.height) {
			btnSort.setRect(slotLeft - btnSort.reqWidth() - 6, Camera.main.height - 14, btnSort.reqWidth() + 4, 12);
		} else {
			btnSort.setRect(slotLeft, yPos, btnSort.reqWidth() + 4, 12);
		}
		if (games.size() >= 2)
			add(btnSort);

		// 淡入
		fadeIn();

	}

	@Override
	protected void onBackPressed() {
		// 返回标题场景
		ShatteredPixelDungeon.switchNoFade(TitleScene.class);
	}

	// 保存槽按钮
	private static class SaveSlotButton extends Button {

		// 背景
		private NinePatch bg;

		// 英雄图像
		private Image hero;
		// 名字
		private RenderedTextBlock name;
		// 最后游戏时间
		private RenderedTextBlock lastPlayed;

		// 声明变量
		private Image steps;
		private BitmapText depth;
		private Image classIcon;
		private BitmapText level;

		// 声明变量
		private int slot;
		private boolean newGame;

		// 重写createChildren方法
		@Override
		protected void createChildren() {
			super.createChildren();

			// 创建背景
			bg = Chrome.get(Chrome.Type.TOAST_TR);
			add(bg);

			// 创建名字
			name = PixelScene.renderTextBlock(9);
			add(name);

			// 创建最后游戏时间
			lastPlayed = PixelScene.renderTextBlock(6);
			add(lastPlayed);
		}

		// 设置slot
		public void set(int slot) {
			this.slot = slot;
			// 获取游戏信息
			GamesInProgress.Info info = GamesInProgress.check(slot);
			newGame = info == null;
			if (newGame) {
				// 如果是新游戏
				name.text(Messages.get(StartScene.class, "new"));

				// 移除英雄
				if (hero != null) {
					remove(hero);
					hero = null;
					remove(steps);
					steps = null;
					remove(depth);
					depth = null;
					remove(classIcon);
					classIcon = null;
					remove(level);
					level = null;
				}
			} else {

				// 如果不是新游戏
				if (info.subClass != HeroSubClass.NONE) {
					// 如果有子类
					name.text(Messages.titleCase(info.subClass.title()));
				} else {
					// 如果没有子类
					name.text(Messages.titleCase(info.heroClass.title()));
				}

				// 创建英雄
				if (hero == null) {
					hero = new Image(info.heroClass.spritesheet(), 0, 15 * info.armorTier, 12, 15);
					add(hero);

					steps = new Image(Icons.get(Icons.STAIRS));
					add(steps);
					depth = new BitmapText(PixelScene.pixelFont);
					add(depth);

					classIcon = new Image(Icons.get(info.heroClass));
					add(classIcon);
					level = new BitmapText(PixelScene.pixelFont);
					add(level);
				} else {
					// 复制英雄
					hero.copy(new Image(info.heroClass.spritesheet(), 0, 15 * info.armorTier, 12, 15));

					classIcon.copy(Icons.get(info.heroClass));
				}

				// 计算最后游戏时间
				long diff = Game.realTime - info.lastPlayed;
				if (diff > 99L * 30 * 24 * 60 * 60_000) {
					lastPlayed.text(" "); // show no text for >99 months ago
				} else if (diff < 60_000) {
					lastPlayed.text(Messages.get(StartScene.class, "one_minute_ago"));
				} else if (diff < 2 * 60 * 60_000) {
					lastPlayed.text(Messages.get(StartScene.class, "minutes_ago", diff / 60_000));
				} else if (diff < 2 * 24 * 60 * 60_000) {
					lastPlayed.text(Messages.get(StartScene.class, "hours_ago", diff / (60 * 60_000)));
				} else if (diff < 2L * 30 * 24 * 60 * 60_000) {
					lastPlayed.text(Messages.get(StartScene.class, "days_ago", diff / (24 * 60 * 60_000)));
				} else {
					lastPlayed.text(Messages.get(StartScene.class, "months_ago", diff / (30L * 24 * 60 * 60_000)));
				}

				// 设置深度
				depth.text(Integer.toString(info.depth));
				depth.measure();

				// 设置等级
				level.text(Integer.toString(info.level));
				level.measure();

				// 设置作弊与挑战
				if (info.cheat > 0) {
					name.hardlight(0x8b00f0);
					lastPlayed.hardlight(0x8b00f0);
					depth.hardlight(0x8b00f0);
					level.hardlight(0x8b00f0);

				} else if (info.challenges > 0) {
					name.hardlight(Window.TITLE_COLOR);
					lastPlayed.hardlight(Window.TITLE_COLOR);
					depth.hardlight(Window.TITLE_COLOR);
					level.hardlight(Window.TITLE_COLOR);
				} else {
					name.resetColor();
					lastPlayed.resetColor();
					depth.resetColor();
					level.resetColor();
				}

				// 设置每日游戏
				if (info.daily) {
					if (info.dailyReplay) {
						steps.hardlight(1f, 0.5f, 2f);
					} else {
						steps.hardlight(0.5f, 1f, 2f);
					}
				} else if (!info.customSeed.isEmpty()) {
					steps.hardlight(1f, 1.5f, 0.67f);
				}

			}

			// 布局
			layout();
		}

		// 重写layout方法
		@Override
		protected void layout() {
			super.layout();

			// 设置背景位置和大小
			bg.x = x;
			bg.y = y;
			bg.size(width, height);

			// 如果有英雄
			if (hero != null) {
				hero.x = x + 8;
				hero.y = y + (height - hero.height()) / 2f;
				align(hero);

				// 设置名字位置
				name.setPos(
						hero.x + hero.width() + 6,
						y + (height - name.height() - lastPlayed.height() - 2) / 2f);
				align(name);

				// 设置最后游戏时间位置
				lastPlayed.setPos(
						hero.x + hero.width() + 6,
						name.bottom() + 2);

				// 设置子类图标位置
				classIcon.x = x + width - 24 + (16 - classIcon.width()) / 2f;
				classIcon.y = y + (height - classIcon.height()) / 2f;
				align(classIcon);

				// 设置等级位置
				level.x = classIcon.x + (classIcon.width() - level.width()) / 2f;
				level.y = classIcon.y + (classIcon.height() - level.height()) / 2f + 1;
				align(level);

				// 设置步数图标位置
				steps.x = x + width - 40 + (16 - steps.width()) / 2f;
				steps.y = y + (height - steps.height()) / 2f;
				align(steps);

				// 设置深度位置
				depth.x = steps.x + (steps.width() - depth.width()) / 2f;
				depth.y = steps.y + (steps.height() - depth.height()) / 2f + 1;
				align(depth);

			} else {
				// 如果没有英雄
				name.setPos(
						x + (width - name.width()) / 2f,
						y + (height - name.height()) / 2f);
				align(name);
			}

		}

		// 重写onClick方法
		@Override
		protected void onClick() {
			if (newGame) {
				// 如果是新游戏
				GamesInProgress.selectedClass = null;
				GamesInProgress.curSlot = slot;
				ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
			} else {
				// 如果不是新游戏
				ShatteredPixelDungeon.scene().add(new WndGameInProgress(slot));
			}
		}
	}
}
