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
package com.consideredhamster.yetanotherpixeldungeon.items.weapons.throwing;

import com.watabou.utils.Random;
import com.consideredhamster.yetanotherpixeldungeon.actors.Char;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Buff;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Poison;
import com.consideredhamster.yetanotherpixeldungeon.sprites.ItemSpriteSheet;

public class PoisonDarts extends ThrowingWeaponSpecial {

	{
		name = "poison darts";
		image = ItemSpriteSheet.THROWING_DART;
	}

	public PoisonDarts() {
		this( 1 );
	}

	public PoisonDarts(int number) {
        super( 1 );
		quantity = number;
	}

    @Override
    public void proc( Char attacker, Char defender, int damage ) {
        super.proc(attacker, defender, damage);

        Poison buff = Buff.affect( defender, Poison.class );

        if( buff != null ) {
            buff.addDuration(Random.IntRange( damage, damage * 2 ));
        }
    }
	
	@Override
	public String desc() {
		return 
			"Stings of these vicious little darts are covered in some kind of toxin, " +
			"poisoning their targets with some kind of deadly venom.";
	}
}
