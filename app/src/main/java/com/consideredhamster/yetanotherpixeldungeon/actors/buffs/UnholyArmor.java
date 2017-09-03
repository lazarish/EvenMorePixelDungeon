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

package com.consideredhamster.yetanotherpixeldungeon.actors.buffs;

import com.watabou.utils.Bundle;
import com.consideredhamster.yetanotherpixeldungeon.ui.BuffIndicator;

public class UnholyArmor extends PassiveBuff {

//    private static final float STEP = 1f;

    private static final String TXT_ON_HIT = "absorbed";
//    private static final String TXT_BROKEN = "armor broken";
//
    private int consumed;

//    @Override
//    public boolean act() {
//        spend( STEP );
//        return true;
//    }

//    public int absorb( int damage ) {
//        if (damage >= durability) {
//
//            if(target.sprite != null && Dungeon.visible[ target.pos ] ) {
//                target.sprite.emitter().burst(Speck.factory(Speck.BONE), Random.IntRange(10, 15));
//                target.sprite.showStatus( CharSprite.NEGATIVE, TXT_BROKEN);
//            }
//
//
//            detach();
//            return durability;
//        } else {
//
//            if(target.sprite != null && Dungeon.visible[ target.pos ] ) {
//                target.sprite.emitter().burst(Speck.factory(Speck.BONE), Random.IntRange( 3, 5 ) );
//                target.sprite.showStatus(CharSprite.WARNING, TXT_ON_HIT);
//            }
//
//            durability -= damage;
//            return damage;
//        }
//    }
//
    public int consumed() {
        return consumed;
    }

    public void consumed( int value ) {
        consumed += value;
    }

    @Override
    public int icon() {
        return BuffIndicator.ARMOR;
    }

    @Override
    public String toString() {
        return "Unholy Armor";
    }

    private static final String CONSUMED	= "consumed";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( CONSUMED, consumed );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        consumed = bundle.getInt( CONSUMED );
    }
}