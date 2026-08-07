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

package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class StoneOfIntuition extends InventoryStone {
	
	{
		image = ItemSpriteSheet.STONE_INTUITION;
	}

	// 骇客（符石混淆）专精：拥有等级/诅咒这类信息的物品（武器、护甲、法杖、戒指、神器）
	private static boolean hasUpgradeInfo(Item item){
		return item instanceof Weapon || item instanceof Armor
				|| item instanceof Wand || item instanceof Ring || item instanceof Artifact;
	}

	// 骇客（符石混淆）专精：拥有隐藏种类的物品（药水、卷轴、戒指）
	private static boolean hasHiddenType(Item item){
		return item instanceof Ring || item instanceof Potion || item instanceof Scroll;
	}

	private static boolean typeKnown(Item item){
		if (item instanceof Ring) return ((Ring) item).isKnown();
		if (item instanceof Potion) return ((Potion) item).isKnown();
		if (item instanceof Scroll) return ((Scroll) item).isKnown();
		return true;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		// 骇客（符石混淆）：可以使用感知符石鉴定所有可被鉴定的物品（种类/等级/诅咒/法杖充能任一未知即可）
		if (Dungeon.hero != null && Dungeon.hero.hasTalent(Talent.RUNESTONE_CONFUSION)){
			// 已完全鉴定的物品（没有任何可鉴定信息）不可选中
			if (hasUpgradeInfo(item) && !item.levelKnown) return true;
			if (hasUpgradeInfo(item) && !item.cursedKnown) return true;
			if (hasHiddenType(item) && !typeKnown(item)) return true;
			if (item instanceof Wand && !((Wand) item).curChargeKnown) return true;
			return false;
		}
		if (item instanceof Ring){
			return !((Ring) item).isKnown();
		} else if (item instanceof Potion){
			return !((Potion) item).isKnown();
		} else if (item instanceof Scroll){
			return !((Scroll) item).isKnown();
		}
		return false;
	}
	
	@Override
	protected void onItemSelected(Item item) {
		// 骇客（符石混淆）：不再需要猜测，直接进行独立判定
		if (Dungeon.hero != null && Dungeon.hero.hasTalent(Talent.RUNESTONE_CONFUSION)){
			hackConfuse(item);
			return;
		}
		GameScene.show( new WndGuess(item));
		
	}

	// 符石混淆：分别独立判定鉴定等级/诅咒/种类/法杖充能
	// +1 基础触发概率 10%，+2 为 20%；每个不适用的效果（物品无该类信息，或该类信息已被鉴定）额外 +5%/+10%
	private void hackConfuse(Item item){
		int points = Dungeon.hero.pointsInTalent(Talent.RUNESTONE_CONFUSION);
		int chance = points * 10; // +1: 10%，+2: 20%

		// 统计不适用的效果数量，每个不适用效果 +5%/+10%
		int nonApplicable = 0;
		if (!hasUpgradeInfo(item) || item.levelKnown) nonApplicable++;
		if (!hasUpgradeInfo(item) || item.cursedKnown) nonApplicable++;
		if (!hasHiddenType(item) || typeKnown(item)) nonApplicable++;
		if (!(item instanceof Wand) || ((Wand) item).curChargeKnown) nonApplicable++;
		chance += nonApplicable * points * 5;

		boolean any = false;
		// 鉴定物品等级
		if (hasUpgradeInfo(item) && !item.levelKnown && Random.Int(100) < chance) {
			item.levelKnown = true; any = true;
		}
		// 鉴定物品诅咒状态
		if (hasUpgradeInfo(item) && !item.cursedKnown && Random.Int(100) < chance) {
			item.cursedKnown = true; any = true;
		}
		// 鉴定物品的种类
		if (hasHiddenType(item) && !typeKnown(item) && Random.Int(100) < chance) {
			if (item instanceof Ring) {
				((Ring) item).setKnown();
			} else if (item instanceof Potion) {
				((Potion) item).setKnown();
			} else if (item instanceof Scroll) {
				((Scroll) item).setKnown();
			}
			any = true;
		}
		// 鉴定（法杖的）充能
		if (item instanceof Wand && !((Wand) item).curChargeKnown && Random.Int(100) < chance) {
			((Wand) item).curChargeKnown = true; any = true;
		}

		if (any){
			Item.updateQuickslot();
			GLog.p( Messages.get(this, "confused") );
			curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );
		} else {
			GLog.w( Messages.get(this, "confused_fail") );
		}

		// 消耗逻辑与猜测一致：第一次免费，之后消耗
		if (!anonymous) {
			Catalog.countUse(StoneOfIntuition.class);
			if (curUser.buff(IntuitionUseTracker.class) == null) {
				Buff.affect(curUser, IntuitionUseTracker.class);
			} else {
				curItem.detach(curUser.belongings.backpack);
				curUser.buff(IntuitionUseTracker.class).detach();
			}
			Talent.onRunestoneUsed(curUser, curUser.pos, StoneOfIntuition.class);
		}
	}

	@Override
	public String desc() {
		String text = super.desc();
		if (Dungeon.hero != null){
			if (Dungeon.hero.buff(IntuitionUseTracker.class) == null){
				text += "\n\n" + Messages.get(this, "break_info");
			} else {
				text += "\n\n" + Messages.get(this, "break_warn");
			}
		}
		return text;
	}

	public static class IntuitionUseTracker extends Buff {{ revivePersists = true; }};
	
	private static Class curGuess = null;

	public class WndGuess extends Window {
		
		private static final int WIDTH = 120;
		private static final int BTN_SIZE = 20;
		
		public WndGuess(final Item item){
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon( new ItemSprite(item) );
			titlebar.label( Messages.titleCase(item.name()) );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextBlock text = PixelScene.renderTextBlock(6);
			text.text( Messages.get(this, "text") );
			text.setPos(0, titlebar.bottom()+2);
			text.maxWidth( WIDTH );
			add(text);
			
			final RedButton guess = new RedButton(""){
				@Override
				protected void onClick() {
					super.onClick();
					useAnimation();
					if (item.getClass() == curGuess){
						if (item instanceof Ring){
							((Ring) item).setKnown();
							Item.updateQuickslot();
						} else {
							item.identify();
						}
						GLog.p( Messages.get(WndGuess.class, "correct") );
						curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );
					} else {
						GLog.w( Messages.get(WndGuess.class, "incorrect") );
					}
					if (!anonymous) {
						Catalog.countUse(StoneOfIntuition.class);
						if (curUser.buff(IntuitionUseTracker.class) == null) {
							Buff.affect(curUser, IntuitionUseTracker.class);
						} else {
							curItem.detach(curUser.belongings.backpack);
							curUser.buff(IntuitionUseTracker.class).detach();
						}
						Talent.onRunestoneUsed(curUser, curUser.pos, StoneOfIntuition.class);
					}
					curGuess = null;
					hide();
				}
			};
			guess.visible = false;
			guess.icon( new ItemSprite(item) );
			guess.enable(false);
			guess.setRect(0, 80, WIDTH, 20);
			add(guess);
			
			float left;
			float top = text.bottom() + 5;
			int rows;
			int placed = 0;
			
			final ArrayList<Class<?extends Item>> unIDed = new ArrayList<>();
			if (item.isIdentified()){
				hide();
				return;
			} else if (item instanceof Potion){
				if (item instanceof ExoticPotion) {
					for (Class<?extends Item> i : Potion.getUnknown()){
						unIDed.add(ExoticPotion.regToExo.get(i));
					}
				} else {
					unIDed.addAll(Potion.getUnknown());
				}
			} else if (item instanceof Scroll){
				if (item instanceof ExoticScroll) {
					for (Class<?extends Item> i : Scroll.getUnknown()){
						unIDed.add(ExoticScroll.regToExo.get(i));
					}
				} else {
					unIDed.addAll(Scroll.getUnknown());
				}
			} else if (item instanceof Ring) {
				unIDed.addAll(Ring.getUnknown());
			} else {
				hide();
				return;
			}
			
			if (unIDed.size() <= 5){
				rows = 1;
				top += BTN_SIZE/2f;
				left = (WIDTH - BTN_SIZE*unIDed.size())/2f;
			} else {
				rows = 2;
				left = (WIDTH - BTN_SIZE*((unIDed.size()+1)/2))/2f;
			}
			
			for (final Class<?extends Item> i : unIDed){

				IconButton btn = new IconButton(){
					@Override
					protected void onClick() {
						curGuess = i;
						guess.visible = true;
						guess.text( Messages.titleCase(Messages.get(curGuess, "name")) );
						guess.enable(true);
						super.onClick();
					}
				};
				Image im = new Image(Assets.Sprites.ITEM_ICONS);
				im.frame(ItemSpriteSheet.Icons.film.get(Reflection.newInstance(i).icon));
				im.scale.set(2f);
				btn.icon(im);
				btn.setRect(left + placed*BTN_SIZE, top, BTN_SIZE, BTN_SIZE);
				add(btn);
				
				placed++;
				if (rows == 2 && placed == ((unIDed.size()+1)/2)){
					placed = 0;
					if (unIDed.size() % 2 == 1){
						left += BTN_SIZE/2f;
					}
					top += BTN_SIZE;
				}
			}
			
			resize(WIDTH, 100);
			
		}

	}
}
