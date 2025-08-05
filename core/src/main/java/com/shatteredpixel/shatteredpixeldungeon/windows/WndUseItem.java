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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemJournalButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;

public class WndUseItem extends WndInfoItem {

	private static final float BUTTON_HEIGHT	= 16;
	
	private static final float GAP	= 2;

	public Window owner;
	public Item item;

	public WndUseItem(final Window owner, final Item item) {

		// 调用父类构造函数
		super(item);

		// 保存传入的owner和item
		this.owner = owner;
		this.item = item;

		// 初始化y的值
		float y = height;

		// 如果英雄存活并且拥有该物品
		if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {
			// 增加y的值
			y += GAP;
			// 创建一个按钮列表
			ArrayList<RedButton> buttons = new ArrayList<>();
			// 遍历物品的动作
			for (final String action : item.actions(Dungeon.hero)) {

				RedButton btn = new RedButton(item.actionName(action, Dungeon.hero), 8) {
					@Override
					protected void onClick() {
						hide();
						if (owner != null && owner.parent != null)
							owner.hide();
						if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {
							item.execute(Dungeon.hero, action);
						}
						Item.updateQuickslot();
						if (action.equals(item.defaultAction()) && item.usesTargeting && owner == null) {
							InventoryPane.useTargeting();
						}
					}
				};
				btn.setSize(btn.reqWidth(), BUTTON_HEIGHT);
				buttons.add(btn);
				add(btn);

				if (action.equals(item.defaultAction())) {
					btn.textColor(TITLE_COLOR);
				}

			}
			y = layoutButtons(buttons, width, y);

			// 如果物品有广告链接
			if (item.ad_url != null && !item.ad_url.isEmpty() && item.isIdentified() && SPDSettings.adIntensity() > 0) {
				ItemJournalButton btn = new ItemJournalButton(item, this);
				btn.icon().scale.set(10 / btn.icon().height);
				btn.setRect(width - 8, 4, 10, 10);
				add(btn);

				// 创建广告图标
				ItemSprite adIcon = new ItemSprite(ItemSpriteSheet.GOLD);
				adIcon.scale.set(8/adIcon.height);
				IconButton adBtn = new IconButton(adIcon) {
					@Override
					protected void onClick() {
						// 记录打开时间并设置8秒超时检测
						final long openTime = System.currentTimeMillis();
						Actor.add(new Actor() {
							{
								actPriority = VFX_PRIO; // 视觉效果优先级
								spend(8f); // 设置8秒延迟
							}
							
							@Override
							protected boolean act() {
								if (System.currentTimeMillis() - openTime >= 8000) {
									item.onAdClick();
								}
								Actor.remove(this);
								return true;
							}
						});
						
						// 打开链接
						switch (item.ad_mod) {
							case "default":
							default:		// 默认浏览器打开
								ShatteredPixelDungeon.platform.openURI(item.ad_url);
						}
					}
				};
				adBtn.setRect(width - 20, 4, 10, 10);
				add(adBtn);
			} else {
				// 创建普通的物品日志按钮
				ItemJournalButton btn = new ItemJournalButton(item, this);
				btn.setRect(width - 16, 0, 16, 16);
				add(btn);
			}
		}

		resize(width, (int) (y));
	}

	private static float layoutButtons(ArrayList<RedButton> buttons, float width, float y){
		//创建一个当前行的按钮列表
		ArrayList<RedButton> curRow = new ArrayList<>();
		//当前行的宽度
		float widthLeftThisRow = width;
		
		//当按钮列表不为空时，循环
		while( !buttons.isEmpty() ){
			//获取按钮列表中的第一个按钮
			RedButton btn = buttons.get(0);
			
			//当前行的宽度减去按钮的宽度
			widthLeftThisRow -= btn.width();
			//如果当前行按钮列表为空
			if (curRow.isEmpty()) {
				//将按钮添加到当前行按钮列表中
				curRow.add(btn);
				//从按钮列表中移除该按钮
				buttons.remove(btn);
			} else {
				//当前行的宽度减去1
				widthLeftThisRow -= 1;
				//如果当前行的宽度大于等于0
				if (widthLeftThisRow >= 0) {
					//将按钮添加到当前行按钮列表中
					curRow.add(btn);
					//从按钮列表中移除该按钮
					buttons.remove(btn);
				}
			}
			
			//layout current row. Currently forces a max of 3 buttons but can work with more
			if (buttons.isEmpty() || widthLeftThisRow <= 0 || curRow.size() >= 3){
				
				//re-use this variable for laying out the buttons
				widthLeftThisRow = width - (curRow.size()-1);
				for (RedButton b : curRow){
					widthLeftThisRow -= b.width();
				}
				
				//while we still have space in this row, find the shortest button(s) and extend them
				while (widthLeftThisRow > 0){
					
					ArrayList<RedButton> shortest = new ArrayList<>();
					RedButton secondShortest = null;
					
					for (RedButton b : curRow) {
						if (shortest.isEmpty()) {
							shortest.add(b);
						} else {
							if (b.width() < shortest.get(0).width()) {
								secondShortest = shortest.get(0);
								shortest.clear();
								shortest.add(b);
							} else if (b.width() == shortest.get(0).width()) {
								shortest.add(b);
							} else if (secondShortest == null || secondShortest.width() > b.width()){
								secondShortest = b;
							}
						}
					}
					
					float widthToGrow;
					
					if (secondShortest == null){
						widthToGrow = widthLeftThisRow / shortest.size();
						widthLeftThisRow = 0;
					} else {
						widthToGrow = secondShortest.width() - shortest.get(0).width();
						if ((widthToGrow * shortest.size()) >= widthLeftThisRow){
							widthToGrow = widthLeftThisRow / shortest.size();
							widthLeftThisRow = 0;
						} else {
							widthLeftThisRow -= widthToGrow * shortest.size();
						}
					}
					
					for (RedButton toGrow : shortest){
						toGrow.setRect(0, 0, toGrow.width()+widthToGrow, toGrow.height());
					}
				}
				
				//finally set positions
				float x = 0;
				for (RedButton b : curRow){
					b.setRect(x, y, b.width(), b.height());
					x += b.width() + 1;
				}
				
				//move to next line and reset variables
				y += BUTTON_HEIGHT+1;
				widthLeftThisRow = width;
				curRow.clear();
				
			}
			
		}
		
		return y - 1;
	}

}
