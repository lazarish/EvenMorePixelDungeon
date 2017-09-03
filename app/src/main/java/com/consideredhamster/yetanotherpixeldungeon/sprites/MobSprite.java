/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Yet Another Pixel Dungeon
 * Copyright (C) 2015-2016 Considered Hamster
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
package com.consideredhamster.yetanotherpixeldungeon.sprites;

import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.noosa.tweeners.ScaleTweener;
import com.consideredhamster.yetanotherpixeldungeon.DungeonTilemap;
import com.consideredhamster.yetanotherpixeldungeon.actors.mobs.Mob;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class MobSprite extends CharSprite {

	private static final float FADE_TIME	= 3f;
	private static final float FALL_TIME	= 1f;

	@Override
	public void update() {
		sleeping = ch != null && ((Mob)ch).state == ((Mob)ch).SLEEPING;

        if (ch != null && visible && ch.dexterity() > 0 && ch.isAlive() ) {
            showAlert();
        } else {
            hideAlert();
        }

        if (sleeping) {
            showSleep();
        } else {
            hideSleep();
        }

		super.update();
	}

//    @Override
//    public void attack( int cell ) {
//        if (!Level.adjacent(cell, ch.pos)) {
//
//            cellToAttack = cell;
//            turnTo( ch.pos , cellToAttack );
//            play(cast);
//
//        } else {
//
//            super.attack(cell);
//
//        }
//    }
	
	@Override
	public void onComplete( Animation anim ) {
		
		super.onComplete(anim);
		
		if (anim == die) {	
			parent.add( new AlphaTweener( this, 0, FADE_TIME ) {
				@Override
				protected void onComplete() {
					MobSprite.this.killAndErase();
					parent.erase( this );
				};
			} );
		}
	}
	
	public void fall() {
		
		origin.set( width / 2, height - DungeonTilemap.SIZE / 2 );
		angularSpeed = Random.Int( 2 ) == 0 ? -720 : 720;
		
		parent.add(new ScaleTweener(this, new PointF(0, 0), FALL_TIME) {
            @Override
            protected void onComplete() {
                MobSprite.this.killAndErase();
                parent.erase(this);
            }

            ;

            @Override
            protected void updateValues(float progress) {
                super.updateValues(progress);
                am = 1 - progress;
            }
        });
	}
}
