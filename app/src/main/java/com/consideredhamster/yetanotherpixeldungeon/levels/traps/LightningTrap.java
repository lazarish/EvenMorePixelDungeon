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
package com.consideredhamster.yetanotherpixeldungeon.levels.traps;

import com.consideredhamster.yetanotherpixeldungeon.DamageType;
import com.consideredhamster.yetanotherpixeldungeon.Dungeon;
import com.consideredhamster.yetanotherpixeldungeon.actors.Actor;
import com.consideredhamster.yetanotherpixeldungeon.actors.Char;
import com.consideredhamster.yetanotherpixeldungeon.effects.CellEmitter;
import com.consideredhamster.yetanotherpixeldungeon.effects.Lightning;
import com.consideredhamster.yetanotherpixeldungeon.effects.particles.SparkParticle;
import com.consideredhamster.yetanotherpixeldungeon.levels.Level;
import com.consideredhamster.yetanotherpixeldungeon.levels.Terrain;
import com.watabou.utils.Random;

public class LightningTrap extends Trap {

    // FIXME
    public static LightningTrap TRAP = new LightningTrap();
	
	// 00x66CCEE
	
	public static void trigger( int pos ) {

        // FIXME

        Level.set( pos, Terrain.INACTIVE_TRAP );

        Char ch = Actor.findChar( pos );

		if (ch != null) {

            int power = 10 + Dungeon.chapter() * 3;

			ch.damage( Math.max( 1, Random.IntRange( power / 2, power ) ), TRAP, DamageType.SHOCK);

//			if (ch == Dungeon.hero) {
//				if (!ch.isAlive()) {
//					Dungeon.fail( Utils.format( ResultDescriptions.TRAP, name, Dungeon.depth ) );
//					GLog.n( "You were killed by a discharge of a lightning trap..." );
//				}
//			}
			
			int[] points = new int[2];
			
			points[0] = pos - Level.WIDTH;
			points[1] = pos + Level.WIDTH;
			ch.sprite.parent.add( new Lightning( points, 2, null ) );
			
			points[0] = pos - 1;
			points[1] = pos + 1;
			ch.sprite.parent.add( new Lightning( points, 2, null ) );
		}
		
		CellEmitter.center( pos ).burst( SparkParticle.FACTORY, Random.IntRange( 3, 4 ) );
		
	}
}
