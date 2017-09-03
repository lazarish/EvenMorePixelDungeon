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
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Ensnared;
import com.consideredhamster.yetanotherpixeldungeon.sprites.ItemSpriteSheet;

public class Bolas extends ThrowingWeaponSpecial {

	{
		name = "bolas";
		image = ItemSpriteSheet.BOLAS;
	}

	public Bolas() {
		this( 1 );
	}

	public Bolas(int number) {
        super( 2 );
		quantity = number;
	}

    @Override
    public void proc( Char attacker, Char defender, int damage ) {
        super.proc(attacker, defender, damage);

        if( Random.Int( defender.HT ) < damage * 2) {
            Buff.prolong(defender, Ensnared.class, damage);
        }
    }
	
	@Override
	public String desc() {
		return 
			"Bolas are mostly used for hunting and they usually don't do much damage but " +
            "they can ensnare the target leaving it helpless and motionless for some time.";
	}
}
