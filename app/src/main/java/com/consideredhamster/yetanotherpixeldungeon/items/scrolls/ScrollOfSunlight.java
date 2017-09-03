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

import com.watabou.noosa.audio.Sample;
import com.consideredhamster.yetanotherpixeldungeon.Assets;
import com.consideredhamster.yetanotherpixeldungeon.actors.blobs.Blob;
import com.consideredhamster.yetanotherpixeldungeon.actors.blobs.Sunlight;
import com.consideredhamster.yetanotherpixeldungeon.effects.Speck;
import com.consideredhamster.yetanotherpixeldungeon.effects.SpellSprite;
import com.consideredhamster.yetanotherpixeldungeon.scenes.GameScene;
import com.consideredhamster.yetanotherpixeldungeon.utils.GLog;

public class ScrollOfSunlight extends Scroll {

    private static final String TXT_MESSAGE	= "The whole area is suddenly illuminated with rays of warm light.";

	{
		name = "Scroll of Sunlight";
        shortName = "Su";

        spellSprite = SpellSprite.SCROLL_SUNLIGHT;
        spellColour = SpellSprite.COLOUR_HOLY;
	}
	
	@Override
	protected void doRead() {

        curUser.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );
        Sample.INSTANCE.play( Assets.SND_LULLABY );

        GameScene.add( Blob.seed( curUser.pos, (int)( 250 * curUser.magicPower() ), Sunlight.class ) );

        GLog.i( TXT_MESSAGE );

        super.doRead();
	}
	
	@Override
	public String desc() {
		return
			"Reading this scroll will light the area with a bright sunlight. Normally, this would be a very " +
            "situational ability, but here deep in the dungeon, sunlight can mean much more than simple illumination. " +
            "Everything that lives which is touched by this light, will flourish and regenerate health at accelerated " +
            "rate, just as grass and herbs will regrow in mere moments.";
	}
	
	@Override
	public int price() {
		return isTypeKnown() ? 70 * quantity : super.price();
	}
}
