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

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.consideredhamster.yetanotherpixeldungeon.Assets;
import com.consideredhamster.yetanotherpixeldungeon.DamageType;
import com.consideredhamster.yetanotherpixeldungeon.actors.Char;
import com.consideredhamster.yetanotherpixeldungeon.actors.blobs.Miasma;
import com.consideredhamster.yetanotherpixeldungeon.actors.blobs.Blob;
import com.consideredhamster.yetanotherpixeldungeon.effects.MagicMissile;
import com.consideredhamster.yetanotherpixeldungeon.effects.particles.EnergyParticle;
import com.consideredhamster.yetanotherpixeldungeon.levels.Level;
import com.consideredhamster.yetanotherpixeldungeon.mechanics.Ballistica;
import com.consideredhamster.yetanotherpixeldungeon.scenes.GameScene;
import com.consideredhamster.yetanotherpixeldungeon.sprites.CharSprite;
import com.consideredhamster.yetanotherpixeldungeon.sprites.FiendSprite;

import java.util.HashSet;

public class Fiend extends MobRanged {

    private boolean charged = false;

    private static final String CHARGED = "charged";

    public Fiend() {

        super( 19 );

		name = "fiend";
		spriteClass = FiendSprite.class;

	}

    @Override
    public int damageRoll() {
        return super.damageRoll() / 2 ;
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

            sprite.centerEmitter().burst( EnergyParticle.FACTORY_BLACK, 25 );

            spend(attackDelay());

            return true;

        } else {

            charged = false;

            return super.doAttack( enemy );
        }
    }

//    @Override
//    public DamageType damageType() {
//        return DamageType.UNHOLY;
//    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return !isCharmedBy( enemy ) && ( Level.adjacent( pos, enemy.pos )
            || Ballistica.cast( pos, enemy.pos, false, true ) == enemy.pos );
    }

    @Override
    protected void onRangedAttack( int cell ) {

        MagicMissile.shadow(sprite.parent, pos, cell,
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

            enemy.damage( damageRoll(), this, DamageType.UNHOLY );

        } else {

            enemy.sprite.showStatus( CharSprite.NEUTRAL, enemy.defenseVerb() );

        }

        return true;
    }

    @Override
    public void die( Object cause, DamageType dmg ) {

        GameScene.add(Blob.seed(pos, 100, Miasma.class));

        super.die( cause, dmg );
    }

    @Override
    public String description() {
        return
                "Some demons seem to transcend their flesh and wear pure darkness as their form. Shadowy " +
                        "and menacing, these unholy abominations are born of malicious intent and are nothing " +
                        "more than incarnations of distilled evil, revelling only in death and pain.";
    }

    public static final HashSet<Class<? extends DamageType>> RESISTANCES = new HashSet<>();
    public static final HashSet<Class<? extends DamageType>> IMMUNITIES = new HashSet<>();

    static {
        RESISTANCES.add(DamageType.Mind.class);
        RESISTANCES.add(DamageType.Frost.class);

        IMMUNITIES.add(DamageType.Unholy.class);
        IMMUNITIES.add(DamageType.Body.class);
    }

    @Override
    public HashSet<Class<? extends DamageType>> resistances() {
        return RESISTANCES;
    }

    @Override
    public HashSet<Class<? extends DamageType>> immunities() {
        return IMMUNITIES;
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
