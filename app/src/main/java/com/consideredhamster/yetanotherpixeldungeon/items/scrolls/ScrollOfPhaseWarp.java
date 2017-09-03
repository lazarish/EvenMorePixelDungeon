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
package com.consideredhamster.yetanotherpixeldungeon.items.scrolls;

import com.watabou.utils.Random;
import com.consideredhamster.yetanotherpixeldungeon.Dungeon;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Buff;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Confusion;
import com.consideredhamster.yetanotherpixeldungeon.effects.SpellSprite;
import com.consideredhamster.yetanotherpixeldungeon.items.wands.WandOfBlink;
import com.consideredhamster.yetanotherpixeldungeon.utils.GLog;

import java.util.Arrays;

public class ScrollOfPhaseWarp extends Scroll {

	public static final String TXT_TELEPORTED_VISITED =
		"In a blink of an eye you were teleported to somewhere else. You feel deja vu.";

    public static final String TXT_TELEPORTED_UNKNOWN =
        "In a blink of an eye you were teleported to somewhere else. You can't remember this place.";
	
	public static final String TXT_NO_TELEPORT = 
		"Teleportation fails!";
	
	{
		name = "Scroll of Phase Warp";
        shortName = "Ph";

        spellSprite = SpellSprite.SCROLL_TELEPORT;
        spellColour = SpellSprite.COLOUR_WILD;
	}
	
	@Override
	protected void doRead() {

        int pos;

        pos = Dungeon.level.randomRespawnCell( false, true );

        if (pos == -1) {

            GLog.w( TXT_NO_TELEPORT );

        } else {

//            float chance = 0.5f / curUser.magicPower();
//
//            if( chance > Random.Float() ) {
//
//                GLog.i(Dungeon.level.visited[pos] ? TXT_TELEPORTED_VISITED : TXT_TELEPORTED_UNKNOWN);
//                Arrays.fill(Dungeon.level.visited, false);
//
//            }

            WandOfBlink.appear(curUser, pos);
            Dungeon.level.press(pos, curUser);

            Buff.affect(curUser, Confusion.class, Random.Float(5f, 10f));
            Dungeon.observe();

        }

        super.doRead();
	}

    @Override
	public String desc() {
		return
			"The spell on this parchment instantly transports the reader " +
			"to a random location on the dungeon level. It can be used " +
			"to escape a dangerous situation, but this method of transportation " +
            "can be harmful for the mind of its user.";
	}

    @Override
    public int price() {
        return isTypeKnown() ? 75 * quantity : super.price();
    }
}
