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

package com.watabou.noosa.particles;

import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Visual;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Emitter extends Group {

	protected boolean lightMode = false;
	
	public float x;
	public float y;
	public float width;
	public float height;
	
	protected Visual target;
	public boolean fillTarget = true;
	
	protected float interval;
	protected int quantity;
	
	public boolean on = false;

	private boolean started = false;
	public boolean autoKill = true;
	
	protected int count;
	protected float time;
	
	protected Factory factory;
	
	public void pos( float x, float y ) {
		pos( x, y, 0, 0 );
	}
	
	public void pos( PointF p ) {
		pos( p.x, p.y, 0, 0 );
	}
	
	public void pos( float x, float y, float width, float height ) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		target = null;
	}

	public void pos( Visual target ) {
		this.target = target;
	}

	public void pos( Visual target, float x, float y, float width, float height ) {
		pos(x, y, width, height);
		pos(target);
	}
	
	public void burst( Factory factory, int quantity ) {
		start( factory, 0, quantity );
	}
	
	public void pour( Factory factory, float interval ) {
		start( factory, interval, 0 );
	}

	public void start( Factory factory, float interval, int quantity ) {

		this.factory = factory;
		this.lightMode = factory.lightMode();
		
		this.interval = interval;
		this.quantity = quantity;
		
		count = 0;
		time = Random.Float( interval );
		
		on = true;
		started = true;
	}

	public static boolean freezeEmitters = false;

	protected boolean isFrozen(){
		return Game.timeTotal > 1 && freezeEmitters;
	}
	
	@Override
	public void update() {

		if (isFrozen()){
			return;
		}
		
		if (on) {
			time += Game.elapsed;
			while (time > interval) {
				time -= interval;
				emit( count++ );
				if (quantity > 0 && count >= quantity) {
					on = false;
					break;
				}
			}
		} else if (started && autoKill && countLiving() == 0) {
			kill();
		}
		
		super.update();
	}

	@Override
	public void revive() {
		//ensure certain emitter variables default to true
		started = false;
		visible = true;
		fillTarget = true;
		autoKill = true;
		super.revive();
	}

/**
 * 发射粒子或对象的方法
 * 根据目标对象的状态决定发射位置
 * @param index 粒子或对象的索引
 */
	protected void emit( int index ) {
    // 检查是否有目标对象
		if (target == null) {
        // 如果没有目标对象，则在当前对象的区域内随机位置发射
			factory.emit(
				this,
				index,
				x + Random.Float( width ),    // x坐标为当前对象的x位置加上随机偏移
				y + Random.Float( height ) ); // y坐标为当前对象的y位置加上随机偏移
		} else {
        // 如果有目标对象，根据fillTarget标志决定发射位置
			if (fillTarget) {
            // 如果fillTarget为true，则在目标对象的区域内随机位置发射
				factory.emit(
						this,
						index,
						target.x + Random.Float( target.width ),  // x坐标为目标对象的x位置加上随机偏移
						target.y + Random.Float( target.height ) ); // y坐标为目标对象的y位置加上随机偏移
			} else {
            // 如果fillTarget为false，则在目标对象和当前对象的组合区域内随机位置发射
				factory.emit(
						this,
						index,
						target.x + x + Random.Float( width ),    // x坐标为目标对象的x位置加上当前对象的x位置再加上随机偏移
						target.y + y + Random.Float( height ) ); // y坐标为目标对象的y位置加上当前对象的y位置再加上随机偏移
			}
		}
	}
	
	@Override
	public void draw() {
		if (lightMode) {
			Blending.setLightMode();
			super.draw();
			Blending.setNormalMode();
		} else {
			super.draw();
		}
	}
	
	abstract public static class Factory {
		
		abstract public void emit( Emitter emitter, int index, float x, float y );
		
		public boolean lightMode() {
			return false;
		}
	}
}
