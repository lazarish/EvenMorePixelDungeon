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
import com.consideredhamster.yetanotherpixeldungeon.Dungeon;
import com.consideredhamster.yetanotherpixeldungeon.actors.Actor;
import com.consideredhamster.yetanotherpixeldungeon.actors.Char;
import com.consideredhamster.yetanotherpixeldungeon.actors.hero.Hero;
import com.consideredhamster.yetanotherpixeldungeon.effects.CellEmitter;
import com.consideredhamster.yetanotherpixeldungeon.effects.particles.ElmoParticle;
import com.consideredhamster.yetanotherpixeldungeon.items.Item;
import com.consideredhamster.yetanotherpixeldungeon.levels.Level;
import com.consideredhamster.yetanotherpixeldungeon.sprites.CharSprite;
import com.consideredhamster.yetanotherpixeldungeon.sprites.ImpSprite;
import com.consideredhamster.yetanotherpixeldungeon.sprites.MissileSprite;
import com.consideredhamster.yetanotherpixeldungeon.utils.GLog;
import com.consideredhamster.yetanotherpixeldungeon.utils.Utils;

import java.util.HashSet;

public class Imp extends MobRanged {

    public Item item;

    private static final String ITEM = "item";

    protected static final String TXT_STOLE	= "%s stole %s from you!";
	protected static final String TXT_CARRY	= "\n\nThis imp is carrying a _%s_, stolen from you.";

    public Imp() {

        super( 17 );

        name = "malicious imp";
        spriteClass = ImpSprite.class;

        flying = true;

        item = null;
    }

    @Override
    public DamageType damageType() {
        return DamageType.ENERGY;
    }



    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put( ITEM, item );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        item = (Item)bundle.get( ITEM );
    }

    @Override
    protected boolean getFurther( int target ) {

        if( enemySeen ) {
            int newPos = -1;

            for (int i = 0; i < 10; i++) {
                newPos = Dungeon.level.randomRespawnCell( true, false );
                if (newPos != -1) {
                    break;
                }
            }

            if (newPos != -1) {

                Actor.freeCell(pos);

                CellEmitter.get(pos).start(ElmoParticle.FACTORY, 0.03f, 2 + Level.distance(pos, newPos));

                pos = newPos;
                sprite.place(pos);
                sprite.visible = Dungeon.visible[pos];

                return true;

            } else {

                return false;

            }

        } else {

            return super.getFurther( target );

        }
    }

    @Override
    public void die( Object cause, DamageType dmg ) {

        super.die(cause, dmg);

        if (item != null) {
            Dungeon.level.drop( item, pos ).sprite.drop();
        }
    }

    @Override
    protected boolean doAttack( Char enemy ) {

        if ( HP >= HT && item == null && enemy instanceof Hero && ((Hero)enemy).belongings.backpack.countVisibleItems() > 0 ) {

            final int enemyPos = enemy.pos;

            boolean visible = Level.fieldOfView[pos] || Level.fieldOfView[enemyPos];

            if ( visible ) {

                sprite.cast( enemyPos, new Callback() {
                    @Override
                    public void call() { onRangedAttack( enemyPos ); }
                }  );

            } else {

                cast(enemy);

            }

            spend( attackDelay() );

            return !visible;


        } else {

            return super.doAttack( enemy );

        }
    }

    @Override
    protected void onRangedAttack( int cell ) {

        onCastComplete();

        super.onRangedAttack( cell );
    }

    @Override
    public boolean cast( Char enemy ) {

        if (hit( this, enemy, false, false )) {

            if (item == null && enemy instanceof Hero) {

                Hero hero = (Hero)enemy;

                Item item = hero.belongings.randomVisibleUnequipped();

                if (item != null) {

                    Sample.INSTANCE.play( Assets.SND_MIMIC, 1, 1, 1.5f  );
                    GLog.w(TXT_STOLE, this.name, item.name());

                    state = FLEEING;

                    item.detachAll(hero.belongings.backpack);
                    this.item = item;

                    ((MissileSprite) sprite.parent.recycle(MissileSprite.class)).
                            reset(enemy.pos, pos, item, null);

//                    spend( attackDelay() * (-1) );

                }
            }

            return true;

        } else {

            enemy.sprite.showStatus( CharSprite.NEUTRAL, enemy.defenseVerb() );

            return false;
        }
    }

	@Override
    public String description() {
        return
            "Imps are lesser demons. They are notable neither for their strength nor their magic talent, but for their cruelty " +
            "and greed. However, some of them are actually quite smart and sociable. Certainly not this one, though... " +
            ( item != null ? Utils.format( TXT_CARRY, item.name() ) : "" );
    }

    public static final HashSet<Class<? extends DamageType>> RESISTANCES = new HashSet<>();
    public static final HashSet<Class<? extends DamageType>> IMMUNITIES = new HashSet<>();

    static {
        RESISTANCES.add(DamageType.Flame.class);
        RESISTANCES.add(DamageType.Unholy.class);
    }

    @Override
    public HashSet<Class<? extends DamageType>> resistances() {
        return RESISTANCES;
    }

    @Override
    public HashSet<Class<? extends DamageType>> immunities() {
        return IMMUNITIES;
    }
}
