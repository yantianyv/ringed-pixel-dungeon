package com.shatteredpixel.shatteredpixeldungeon.items.remains;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.PortableTerminal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

// 碎屏终端：骇客的职业骸骨遗物。
// 使用：执行亡主遗留的批量骇入脚本，对视野内所有敌人叠加骇入。
// 层数按使用者的主动骇入层数结算（零日漏洞/木马大师加成生效，但不触发子网广播——效果本身就是群体的），
// 其它职业使用时固定为基础 2 层。
public class CrackedTerminal extends RemainsItem {

	{
		image = ItemSpriteSheet.CRACKED_TERMINAL;
	}

	@Override
	protected void doEffect(Hero hero) {
		int layers = PortableTerminal.activeHackLayers(hero);
		int count = 0;
		// 拷贝列表遍历：hackTarget 可能令敌人生命上限归零死亡，从而修改 Actor.chars()
		for (Char ch : new ArrayList<>(Actor.chars())) {
			if (ch.isAlive()
					&& ch.alignment == Char.Alignment.ENEMY
					&& Dungeon.level.heroFOV[ch.pos]) {
				PortableTerminal.hackTarget(hero, ch, layers, 0f);
				count++;
			}
		}
		if (count > 0) {
			Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1f, 1.2f);
			GLog.i(Messages.get(this, "broadcast", count));
		} else {
			GLog.w(Messages.get(this, "no_target"));
		}
	}
}
