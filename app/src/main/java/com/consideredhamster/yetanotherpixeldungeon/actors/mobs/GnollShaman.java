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

import com.consideredhamster.yetanotherpixeldungeon.Dungeon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.consideredhamster.yetanotherpixeldungeon.Assets;
import com.consideredhamster.yetanotherpixeldungeon.DamageType;
import com.consideredhamster.yetanotherpixeldungeon.actors.Char;
import com.consideredhamster.yetanotherpixeldungeon.effects.MagicMissile;
import com.consideredhamster.yetanotherpixeldungeon.effects.particles.EnergyParticle;
import com.consideredhamster.yetanotherpixeldungeon.levels.Level;
import com.consideredhamster.yetanotherpixeldungeon.mechanics.Ballistica;
import com.consideredhamster.yetanotherpixeldungeon.sprites.CharSprite;
import com.consideredhamster.yetanotherpixeldungeon.sprites.ShamanSprite;
import com.watabou.utils.Callback;

import java.util.HashSet;

public class GnollShaman extends MobRanged {

    private boolean charged = false;

    private static final String CHARGED = "charged";

    public GnollShaman() {

        super( 8 );

		name = "gnoll shaman";
		spriteClass = ShamanSprite.class;
		
//		loot = Generator.Category.HERB;
//		lootChance = 0.25f;
	}

    @Override
    public boolean act() {

        if( !enemySeen )
            charged = false;

        return super.act();

    }

    @Override
    protected boolean doAttack( Char enemy ) {

        if( !Level.adjacent( pos, enemy.pos ) && !charged ) {

            charged = true;

            if( Dungeon.visible[ pos ] ) {
                sprite.centerEmitter().burst(EnergyParticle.FACTORY_WHITE, 15);
            }

            spend( attackDelay() );

            return true;

        } else {

            charged = false;

            return super.doAttack( enemy );
        }
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return !isCharmedBy( enemy ) && ( Level.adjacent( pos, enemy.pos )
            || Ballistica.cast( pos, enemy.pos, false, true ) == enemy.pos );
    }

    @Override
    protected void onRangedAttack( int cell ) {

        MagicMissile.blueLight(sprite.parent, pos, cell,
                new Callback() {
                    @Override
                    public void call() {
                        onCastComplete();
                    }
                });

        Sample.INSTANCE.play(Assets.SND_ZAP);

        super.onRangedAttack( cell );
    }
	


    @Override
    public boolean cast( Char enemy ) {

        if (hit( this, enemy, true, true )) {

            enemy.damage( absorb( damageRoll() + damageRoll(), enemy.armorClass() ), this, null );

        } else {

            enemy.sprite.showStatus( CharSprite.NEUTRAL, enemy.defenseVerb() );

        }

        return true;
    }

    @Override
    public String description() {
        return
                "The most intelligent gnolls can master shamanistic magic. Gnoll shamans prefer " +
                        "battle spells to compensate for lack of might, not hesitating to use them " +
                        "on those who question their status in a tribe.";
    }

//    @Override
//    public void call() {
//        next();
//    }

//	@Override
//	protected boolean doAttack( Char enemy ) {
//
//		if (Level.distance( pos, enemy.pos ) <= 1) {
//
//			return super.doAttack( enemy );
//
//		} else {
//
//			boolean visible = Level.fieldOfView[pos] || Level.fieldOfView[enemy.pos];
//			if (visible) {
//				sprite.cast(enemy.pos);
//			}
//
//			spend( TIME_TO_ZAP );
//
//			if ( hit( this, enemy, false, true ) ) {
//
//				enemy.damage(damageRoll(), this, DamageType.SHOCK);

//				if (enemy == Dungeon.hero) {
//
//					Camera.main.shake( 2, 0.3f );
					
//					if (!enemy.isAlive()) {
//						Dungeon.fail( Utils.format( ResultDescriptions.MOB,
//							Utils.indefinite( name ), Dungeon.depth ) );
//						GLog.n( TXT_LIGHTNING_KILLED, name );
//					}
//				}
//			} else {
//				enemy.sprite.showStatus( CharSprite.NEUTRAL, enemy.defenseVerb() );
//			}
//
//			return !visible;
//		}
//	}



    public static final HashSet<Class<? extends DamageType>> RESISTANCES = new HashSet<>();
    static {
//        RESISTANCES.add(DamageType.Shock.class);
//        RESISTANCES.add(DamageType.Body.class);
    }

    @Override
    public HashSet<Class<? extends DamageType>> resistances() {
        return RESISTANCES;
    }

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put( CHARGED, charged );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        charged = bundle.getBoolean( CHARGED );
    }
}
