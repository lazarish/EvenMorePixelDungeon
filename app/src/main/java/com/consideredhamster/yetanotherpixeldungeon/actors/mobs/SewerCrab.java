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
package com.consideredhamster.yetanotherpixeldungeon.actors.mobs;

import com.consideredhamster.yetanotherpixeldungeon.DamageType;
import com.consideredhamster.yetanotherpixeldungeon.Dungeon;
import com.consideredhamster.yetanotherpixeldungeon.actors.hero.HeroClass;
import com.consideredhamster.yetanotherpixeldungeon.actors.mobs.npcs.Ghost;
import com.consideredhamster.yetanotherpixeldungeon.items.food.MysteryMeat;
import com.consideredhamster.yetanotherpixeldungeon.sprites.CrabSprite;

public class SewerCrab extends MobHealthy {

    public SewerCrab() {

        super( 4 );

		name = "sewer crab";
		spriteClass = CrabSprite.class;
		
		loot = new MysteryMeat();
		lootChance = 0.25f;

//        baseSpeed = 2f;
	}

//    @Override
//    public int guardEffectiveness() {
//
//        return super.shieldAC() * 2;
//
//    }

    @Override
    public int shieldAC() {
        return enemySeen && !stunned ? armorClass : 0 ;
    }

    @Override
    public float moveSpeed() {
        return state == HUNTING || state == FLEEING ? super.moveSpeed() * 2.0f: super.moveSpeed() ;
    }
	
	@Override
	public void die( Object cause, DamageType dmg ) {
		Ghost.Quest.process( pos );
		super.die( cause, dmg );
	}
	
	@Override
	public String description() {
//		return
//			"These huge crabs are at the top of the food chain in the sewers. " +
//			"They are extremely fast and their thick exoskeleton can withstand " +
//			"heavy blows.";

        return "These huge crabs are at the top of the food chain in the sewers. "

                + ( Dungeon.hero.heroClass == HeroClass.WARRIOR ?
                "Despite the thickness of their carapaces, they can move very fast if they want to." : "" )

                + ( Dungeon.hero.heroClass == HeroClass.SCHOLAR ?
                "Old stories hypothetize that they may form entire colonies under a city if not taken care of." : "" )

                + ( Dungeon.hero.heroClass == HeroClass.BRIGAND ?
                "Better be careful around them if you don't want to keep all of your limbs in place." : "" )

                + ( Dungeon.hero.heroClass == HeroClass.ACOLYTE ?
                "Their claws can punch through metal armor. Their carapaces appear to be impenetrable. And their beady eyestalks see you as their next prey." : "" )

                ;
	}
}
