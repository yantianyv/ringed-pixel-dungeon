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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScrollOfMirrorImage extends Scroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_MIRRORIMG;
	}

	private static final int NIMAGES	= 2;
	
	@Override
	public void doRead() {
		detach(curUser.belongings.backpack);
		if ( spawnImages(curUser, NIMAGES) > 0){
			GLog.i(Messages.get(this, "copies"));
		} else {
			GLog.i(Messages.get(this, "no_copies"));
		}
		identify();
		
		Sample.INSTANCE.play( Assets.Sounds.READ );
		
		readAnimation();
	}

	public static int spawnImages( Hero hero, int nImages ){
		return spawnImages( hero, hero.pos, nImages);
	}

	//returns the number of images spawned
	public static int spawnImages( Hero hero, int pos, int nImages ){
		return spawnImages( hero, pos, nImages, true ); // 默认保持隐身（向后兼容）
	}

	// 三仙归洞+3：确定镜像生成位置（复用定位代码）
	// 返回：位置列表和是否在敌人身边生成
	private static class SpawnLocationResult {
		ArrayList<Integer> positions = new ArrayList<>();
		boolean spawnedAtEnemy = false;
	}
	
	private static SpawnLocationResult findSpawnLocations(Hero hero, int centerPos) {
		SpawnLocationResult result = new SpawnLocationResult();
		
		// 三仙归洞+3：优先在可达敌人身边生成
		if (hero.subClass == HeroSubClass.MAGICIAN 
				&& hero.hasTalent(Talent.THREE_IMMORTALS) 
				&& hero.pointsInTalent(Talent.THREE_IMMORTALS) >= 3) {
			
			// 收集视野内的敌人并按距离排序
			ArrayList<Char> enemies = new ArrayList<>();
			for (Char ch : Actor.chars()) {
				if (ch != null && ch.alignment == Char.Alignment.ENEMY 
						&& ch.isAlive() && Dungeon.level.heroFOV[ch.pos]) {
					enemies.add(ch);
				}
			}
			
			enemies.sort((a, b) -> Integer.compare(
					Dungeon.level.distance(hero.pos, a.pos),
					Dungeon.level.distance(hero.pos, b.pos)));
			
			// 依次检查可达敌人，收集周围位置
			for (Char enemy : enemies) {
				Ballistica bolt = new Ballistica(hero.pos, enemy.pos, Ballistica.PROJECTILE);
				if (bolt.collisionPos == enemy.pos) {
					// 敌人可达，收集周围9格位置
					for (int offset : PathFinder.NEIGHBOURS9) {
						int cell = enemy.pos + offset;
						if (Dungeon.level.passable[cell] 
								&& Actor.findChar(cell) == null
								&& !result.positions.contains(cell)) {
							result.positions.add(cell);
							result.spawnedAtEnemy = true;
						}
					}
				}
			}
			
			// 如果所有敌人都不可达或找不到位置，在中心位置周围找位置
			if (!result.spawnedAtEnemy) {
				result.positions.clear();
				for (int offset : PathFinder.NEIGHBOURS9) {
					int cell = centerPos + offset;
					if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
						result.positions.add(cell);
					}
				}
			}
		}
		
		// 如果三仙归洞+3未启用或找不到位置，使用默认位置（中心位置周围）
		if (result.positions.isEmpty()) {
			for (int offset : PathFinder.NEIGHBOURS9) {
				int cell = centerPos + offset;
				if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
					result.positions.add(cell);
				}
			}
		}
		
		return result;
	}

	//returns the number of images spawned
	public static int spawnImages( Hero hero, int pos, int nImages, boolean invisible ){
		
		SpawnLocationResult location = findSpawnLocations(hero, pos);
		
		// 三仙归洞+3：如果没有在敌人身边生成，镜像不隐身
		boolean finalInvisible = invisible;
		if (hero.subClass == HeroSubClass.MAGICIAN 
				&& hero.hasTalent(Talent.THREE_IMMORTALS) 
				&& hero.pointsInTalent(Talent.THREE_IMMORTALS) >= 3
				&& !location.spawnedAtEnemy) {
			finalInvisible = false;
		}
		
		int spawned = 0;
		while (nImages > 0 && !location.positions.isEmpty()) {
			int index = Random.index(location.positions);
			int imagePos = location.positions.get(index);
			
			MirrorImage mob = new MirrorImage();
			mob.duplicate(hero, finalInvisible);
			GameScene.add(mob);
			ScrollOfTeleportation.appear(mob, imagePos);
			mob.updateSwapReady(); // 立即检查并显示粒子效果
			
			location.positions.remove(index);
			nImages--;
			spawned++;
		}
		
		return spawned;
	}

	// 在随机位置生成镜像（斗篷充能召唤），找不到位置则放弃召唤
	public static int spawnImagesAtRandom( Hero hero, int nImages ){
		boolean useScrollMethod = hero.subClass == HeroSubClass.MAGICIAN 
				&& hero.hasTalent(Talent.THREE_IMMORTALS) 
				&& hero.pointsInTalent(Talent.THREE_IMMORTALS) >= 3;
		
		// 三仙归洞+3：先尝试按镜像卷轴的方式确定位置
		if (useScrollMethod) {
			SpawnLocationResult location = findSpawnLocations(hero, hero.pos);
			if (!location.positions.isEmpty()) {
				// 使用镜像卷轴方式生成，镜像隐身
				int spawned = 0;
				while (nImages > 0 && !location.positions.isEmpty()) {
					int index = Random.index(location.positions);
					int imagePos = location.positions.get(index);
					
					MirrorImage mob = new MirrorImage();
					mob.duplicate(hero, true); // 按镜像卷轴方式，隐身
					GameScene.add(mob);
					ScrollOfTeleportation.appear(mob, imagePos);
					mob.updateSwapReady(); // 立即检查并显示粒子效果
					
					location.positions.remove(index);
					nImages--;
					spawned++;
				}
				return spawned;
			}
			// 如果找不到位置，继续执行全图随机逻辑
		}
		
		// 全图随机生成，镜像不隐身
		int spawned = 0;
		for (int i = 0; i < nImages; i++) {
			int count = 20;
			int pos = -1;
			do {
				pos = Dungeon.level.randomRespawnCell( null );
				if (count-- <= 0) {
					break;
				}
			} while (pos == -1 || Dungeon.level.secret[pos]);
			
			if (pos == -1) {
				break;
			}
			
			if (Dungeon.level.passable[pos] && Actor.findChar(pos) == null) {
				MirrorImage mob = new MirrorImage();
				mob.duplicate(hero, false); // 全图随机，不隐身
				GameScene.add(mob);
				ScrollOfTeleportation.appear(mob, pos);
				mob.updateSwapReady(); // 立即检查并显示粒子效果
				spawned++;
			}
		}
		
		return spawned;
	}

	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
