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

import java.util.HashSet;

import com.consideredhamster.yetanotherpixeldungeon.effects.Speck;
import com.consideredhamster.yetanotherpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.consideredhamster.yetanotherpixeldungeon.Badges;
import com.consideredhamster.yetanotherpixeldungeon.Challenges;
import com.consideredhamster.yetanotherpixeldungeon.DamageType;
import com.consideredhamster.yetanotherpixeldungeon.Dungeon;
import com.consideredhamster.yetanotherpixeldungeon.Statistics;
import com.consideredhamster.yetanotherpixeldungeon.actors.Actor;
import com.consideredhamster.yetanotherpixeldungeon.actors.Char;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Amok;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Blindness;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Buff;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Challenge;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Charm;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Enraged;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Invisibility;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Sleep;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Summoned;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Terror;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Confusion;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Withered;
import com.consideredhamster.yetanotherpixeldungeon.actors.hero.Hero;
//import com.consideredhamster.yetanotherpixeldungeon.effects.EmoIcon;
import com.consideredhamster.yetanotherpixeldungeon.effects.Flare;
import com.consideredhamster.yetanotherpixeldungeon.effects.Wound;
import com.consideredhamster.yetanotherpixeldungeon.items.misc.Explosives;
import com.consideredhamster.yetanotherpixeldungeon.items.Generator;
import com.consideredhamster.yetanotherpixeldungeon.items.misc.Gold;
import com.consideredhamster.yetanotherpixeldungeon.items.Item;
//import com.consideredhamster.yetanotherpixeldungeon.items.weap1.melee.Dagger;
import com.consideredhamster.yetanotherpixeldungeon.items.rings.RingOfFortune;
import com.consideredhamster.yetanotherpixeldungeon.items.rings.RingOfKnowledge;
import com.consideredhamster.yetanotherpixeldungeon.items.rings.RingOfShadows;
import com.consideredhamster.yetanotherpixeldungeon.items.weapons.Weapon;
import com.consideredhamster.yetanotherpixeldungeon.items.weapons.throwing.ThrowingWeaponAmmo;
import com.consideredhamster.yetanotherpixeldungeon.levels.Level;
import com.consideredhamster.yetanotherpixeldungeon.sprites.CharSprite;
import com.consideredhamster.yetanotherpixeldungeon.ui.HealthIndicator;
import com.consideredhamster.yetanotherpixeldungeon.utils.GLog;
import com.consideredhamster.yetanotherpixeldungeon.utils.Utils;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public abstract class Mob extends Char {
	
	private static final String	TXT_DIED     = "You hear something died in the distance";
    private static final String TXT_HEARD    = "You think you hear a %s %s nearby";
	
	protected static final String TXT_ECHO  = "echo of ";

	protected static final String TXT_RAGE	= "#$%^";
	
	public AiState SLEEPING     = new Sleeping();
	public AiState HUNTING		= new Hunting();
	public AiState WANDERING	= new Wandering();
	public AiState FLEEING		= new Fleeing();
	public AiState PASSIVE		= new Passive();
	public AiState state        = SLEEPING;
	
	public Class<? extends CharSprite> spriteClass;
	
	protected Char self = this;
	protected int target = -1;

	protected int EXP = 0;
    protected int tier = 0;
    protected int maxLvl = 25;

    protected int minDamage = 0;
    protected int maxDamage = 0;

    protected int accuracy = 0;
    protected int dexterity = 0;
    protected int armorClass = 0;

	protected Char enemy;
	protected boolean enemySeen;
	protected boolean alerted = false;
	protected static final float TIME_TO_WAKE_UP = 1f;

//	protected float chanceToWakeUp      = 0.05f;
//	protected float chanceToFallAsleep  = 0.25f;
//    protected float chanceToCalmDown    = 0.50f;

    protected boolean swarmer = false;

	public boolean hostile = true;
	public boolean special = false;
    public boolean noticed = false;
    private boolean recentlyNoticed = false;

	private static final String STATE	= "state";
    private static final String TARGET	= "target";
    private static final String ALERTED	= "alerted";
    private static final String NOTICED	= "noticed";

    @Override
    public int STR() {
        return maxDamage;
    }

    @Override
    public int accuracy() {

        int acc = accuracy;

        if( ( buff(Enraged.class) != null ) )
            acc *= 2;

        if( ( buff(Challenge.class) != null ) )
            acc *= 2;

        if( ( buff(Confusion.class) != null ) )
            acc /= 2;

        if( ( buff(Blindness.class) != null ) )
            acc /= 2;

        return acc;
    }

    @Override
    public int dexterity() {
        return enemySeen && !stunned && !counter ? dexterity : 0;
    }

    @Override
    public int magicSkill() {
        return accuracy() * 2;
    }

    @Override
    public int armourAC() {

        int ac = armorClass;

//        if( buff( Withered.class ) != null )
//            ac = (int)( ac * buff( Withered.class ).modifier() );

        return ac;
    }

    @Override
    public int damageRoll() {

        int damage = Random.NormalIntRange( minDamage, maxDamage );

        if( buff( Enraged.class ) != null )
            damage += Random.NormalIntRange( minDamage, maxDamage );

        if( buff( Challenge.class ) != null )
            damage += damage / 2;

        if( buff( Withered.class ) != null )
            damage = (int)( damage * buff( Withered.class ).modifier() );

        return damage;
    }

    @Override
    public float awareness() {
        return state == HUNTING ? super.awareness() : super.awareness() * 0.5f ;
    }

    @Override
    public float stealth() {
        return state != HUNTING ? super.stealth() : super.stealth() * 0.5f ;
    }

    @Override
    public float attackDelay() {
        return 1f;
    }

    @Override
    public float moveSpeed() {
        return state != WANDERING ? super.moveSpeed() : super.moveSpeed() * 0.75f;
    }

    public int viewDistance() {
        return ( state != SLEEPING ? super.viewDistance() : super.viewDistance() / 2 ) ;
    };
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		
		super.storeInBundle(bundle);
		
		if (state == SLEEPING) {
			bundle.put( STATE, Sleeping.TAG );
		} else if (state == WANDERING) {
			bundle.put( STATE, Wandering.TAG );
		} else if (state == HUNTING) {
			bundle.put( STATE, Hunting.TAG );
		} else if (state == FLEEING) {
			bundle.put( STATE, Fleeing.TAG );
		} else if (state == PASSIVE) {
			bundle.put( STATE, Passive.TAG );
		}

		bundle.put( TARGET, target );
        bundle.put( NOTICED, noticed );
        bundle.put( ALERTED, enemySeen );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle(bundle);
		
		String state = bundle.getString( STATE );
		if (state.equals( Sleeping.TAG )) {
			this.state = SLEEPING;
		} else if (state.equals( Wandering.TAG )) {
			this.state = WANDERING;
		} else if (state.equals( Hunting.TAG )) {
			this.state = HUNTING;
		} else if (state.equals( Fleeing.TAG )) {
			this.state = FLEEING;
		} else if (state.equals( Passive.TAG )) {
			this.state = PASSIVE;
		}

		target = bundle.getInt( TARGET );
        noticed = bundle.getBoolean( NOTICED );
        enemySeen = bundle.getBoolean( ALERTED );
	}
	
	public CharSprite sprite() {
		CharSprite sprite = null;
		try {
			sprite = spriteClass.newInstance();
		} catch (Exception e) {
//            return null;
		}
		return sprite;
	}
	
	@Override
	protected boolean act() {
		
		super.act();

		boolean justAlerted = alerted;
        alerted = false;

        if( noticed ) {

            noticed = false;
            recentlyNoticed = true;

        } else if( recentlyNoticed && Level.distance( pos, Dungeon.hero.pos ) > 2 ) {

            recentlyNoticed = false;

        }
		
		if (stunned) {
			spend( TICK );
			return true;
		}
		
		enemy = chooseEnemy();
		
		boolean enemyInFOV = 
			enemy != null && enemy.isAlive() && 
			Level.fieldOfView[enemy.pos] && enemy.invisible <= 0;

        boolean act = state.act( enemyInFOV, justAlerted );

        if( !recentlyNoticed && Dungeon.hero.isAlive() && !Dungeon.hero.restoreHealth
            && !sprite.visible && state != PASSIVE && Level.distance( pos, Dungeon.hero.pos ) == 2
            && Dungeon.hero.detected( this ) && Dungeon.hero.detected( this )
        ) {
            Dungeon.hero.interrupt( "You were awoken by a noise." );

            if( !enemySeen ) {
                GLog.w(TXT_HEARD, name, state.status());
            }

//            noticed = true;
        }
		
		return act;
	}
	
	protected Char chooseEnemy() {

        Charm charm = buff( Charm.class );

		if ( charm != null) {

			if (enemy == Dungeon.hero || enemy == null) {
				
				HashSet<Mob> enemies = new HashSet<Mob>();
				for (Mob mob:Dungeon.level.mobs) {
					if (mob != this && Level.fieldOfView[mob.pos] && mob.hostile && !(mob.isCharmedBy( charm.object ) ) ) {
						enemies.add( mob );
					}
				}
				if (enemies.size() > 0) {
					return Random.element( enemies );
				}
				
			}
		}
		
		Terror terror = (Terror)buff( Terror.class );
		if (terror != null) {
			Char source = (Char)Actor.findById( terror.object );
			if (source != null) {
				return source;
			}
		}

		return enemy != null && enemy.isAlive() ? enemy : Dungeon.hero;
	}
	
	protected boolean moveSprite( int from, int to ) {

		if (sprite.isVisible() && (Dungeon.visible[from] || Dungeon.visible[to])) {
			sprite.move( from, to );
			return true;
		} else {
			sprite.place( to );
			return true;
		}
	}
	
	@Override
	public boolean add( Buff buff ) {

        if ( buff.awakensMobs() && state != HUNTING) {
            notice();
            state = HUNTING;
        }

        enemySeen = true;
        alerted = true;

        if (buff instanceof Amok) {
            if (sprite != null) {
                sprite.showStatus(CharSprite.NEGATIVE, TXT_RAGE);
            }
            state = HUNTING;
        } else if (buff instanceof Terror) {
            state = FLEEING;
        } else if (buff instanceof Sleep) {
            if (sprite != null) {
                new Flare(4, 32).color(0x44ffff, true).show(sprite, 2f);
            }
            state = SLEEPING;
            postpone(Random.Float(Sleep.SWS, Sleep.SWS * 2));
        }

        return super.add(buff);
    }
	
	@Override
	public void remove( Buff buff ) {
		super.remove(buff);
		if (buff instanceof Terror) {
			sprite.showStatus( CharSprite.NEGATIVE, TXT_RAGE );
			state = HUNTING;
		}
	}
	
	protected boolean canAttack( Char enemy ) {
		return Level.adjacent( pos, enemy.pos ) && !isCharmedBy( enemy );
	}

    protected int nextStepTo( Char enemy ) {
        return Dungeon.findPath( this, pos, enemy.pos,
                flying ? Level.passable : Level.mob_passable,
                Level.fieldOfView );
    }

	protected boolean getCloser( int target ) {

        if (rooted) {

            return false;

        }

		int step = Dungeon.findPath(this, pos, target,
                flying ? Level.passable : Level.mob_passable,
                Level.fieldOfView);

		if (step != -1) {

            Char ch = Actor.findChar( step );

            if( ch == null ) {

                if( !enemySeen ) {

                    for (int n : Level.NEIGHBOURS9) {
                        ch = Actor.findChar(step + n);
                        if ( ch != null && ch instanceof Hero && detected(ch) ) {
                            beckon(ch.pos);
                            break;
                        }
                    }
                }

//                if( ch == null ) {
                    move(step);
                    return true;
//                }

            } else {
                Invisibility.dispel( ch );
                beckon(step);
            }
//			return true;
//		} else {
//			return false;
		}
        return false;
    }
	
	protected boolean getFurther( int target ) {

        if (rooted) {

            return false;

        }

		int step = Dungeon.flee(this, pos, target,
                Level.passable,
                Level.fieldOfView);
		if (step != -1) {
            Char ch = Actor.findChar( step );

            if( ch == null ) {

                move(step);
                return true;

            } else {
                Invisibility.dispel(ch);
                beckon(step);
            }
//			return false;
//		} else {
		}
        return false;
    }
	
//	@Override
//	public void move( int step ) {
//
//        if ( !rooted ) {
//            super.move( step );
//
//            Dungeon.level.press( step, this );
//        }
//	}

    @Override
    public boolean isRanged() {
        return enemy != null && Level.distance( pos, enemy.pos ) > 1;
    }
	
	protected boolean doAttack( Char enemy ) {

        final int enemyPos = enemy.pos;

		boolean visible = Dungeon.visible[pos] || Dungeon.visible[enemyPos];

        if( Level.adjacent( pos, enemyPos ) ) {

            if ( visible ) {

                Dungeon.visible[pos] = true;
                sprite.attack( enemyPos );

            } else {

                attack( enemy );

            }

        } else {

            if ( visible ) {

                Dungeon.visible[pos] = true;
                sprite.cast( enemyPos, new Callback() {
                    @Override
                    public void call() { onRangedAttack( enemyPos ); }
                }  );

            } else {

                cast( enemy );

            }
        }

        if( enemy == Dungeon.hero ) {
            noticed = true;
        }
				
		spend( attackDelay() );
		
		return !visible;
	}

    protected void onRangedAttack( int cell ) {

        if ( enemy == Dungeon.hero ) {
            Dungeon.hero.interrupt( "You were awoken by an attack!" );
        }

        sprite.idle();
//        next();

    }
	
	@Override
	public void onAttackComplete() {
		attack( enemy );
		super.onAttackComplete();
	}

    @Override
    public void onCastComplete() {
        cast(enemy);
        super.onCastComplete();
    }

    public boolean cast( Char enemy ) {
        return attack(enemy);
    }


	
	@Override
	public int defenseProc( Char enemy, int damage, boolean blocked ) {
		if ( dexterity() == 0 && enemy instanceof Hero ) {

            Hero hero = (Hero)enemy;

            Weapon weapon = hero.rangedWeapon != null ? hero.rangedWeapon : hero.currentWeapon;

            if( !counter ) {

                if (weapon != null && weapon.canBackstab()) {
                    damage += hero.damageRoll();
                    Wound.hit(this);
                }

                damage = (int) (damage * hero.ringBuffs(RingOfShadows.Shadows.class));

                if( sprite != null ) {
                    sprite.showStatus(CharSprite.DEFAULT, TXT_AMBUSH);
                }

            } else {

                damage += hero.damageRoll() / 2;
                sprite.emitter().burst(Speck.factory(Speck.MASTERY), 6);

                if( sprite != null ) {
                    sprite.showStatus(CharSprite.DEFAULT, TXT_COUNTER);
                }

            }
        }
		return damage;
	}
	
	public void aggro( Char ch ) {
		enemy = ch;
	}

    @Override
    public void damage( int dmg, Object src, DamageType type ) {

        HealthIndicator.instance.target( this );

		Buff.detach( this, Sleep.class );

        Terror terror = buff( Terror.class );

        if (terror == null) {
            if (src != null && state == FLEEING || state == WANDERING || state == SLEEPING) {
                notice();
                state = HUNTING;
            }
        }

        enemySeen = true;
        alerted = true;

        if( src instanceof Char ) {
            enemy = (Char)src;
        }
		
		super.damage( dmg, src, type );
	}
	
	
	@Override
	public void destroy() {

		Dungeon.level.mobs.remove(this);

		if (Dungeon.hero.isAlive()) {

			if (hostile) {
				Statistics.enemiesSlain++;
				Badges.validateMonstersSlain();
				Statistics.qualifiedForNoKilling = false;
			}

            if ( EXP > 0 && Dungeon.hero.lvl <= maxLvl + Dungeon.hero.lvlBonus ) {

                int exp = EXP;

                if( buff(Challenge.class) != null )
                    exp *= 2;

                float bonus = Dungeon.hero.ringBuffs(RingOfKnowledge.Knowledge.class) * exp - exp;

                exp += (int)bonus;
                exp += Random.Float() < bonus % 1 ? 1 : 0 ;

                Dungeon.hero.earnExp(exp);
			}
		}

        super.destroy();
	}
	
	@Override
	public void die( Object cause, DamageType dmg ) {
		
		super.die(cause, dmg);

//		if (Dungeon.hero.lvl <= maxLvl + 2) {
			dropLoot();
//		}

//		if (Dungeon.hero.isAlive() && !Dungeon.visible[pos]) {
//			GLog.i( TXT_DIED );
//		}

//        if( buff( Enraged.class ) != null ) {
//            for (Mob mob : Dungeon.level.mobs) {
//                mob.beckon(mob.pos);
//
//                Buff.affect(mob, Enraged.class, Random.Float( 10f ) );
//            }
//        }
	}
	
	protected Object loot = null;
	protected float lootChance = 0;
	
	@SuppressWarnings("unchecked")
	protected void dropLoot() {

		if (loot != null && Random.Float() < lootChance * Dungeon.hero.ringBuffs( RingOfFortune.Fortune.class ) ) {

			Item item = null;

			if (loot instanceof Generator.Category) {
				
				item = Generator.random( (Generator.Category)loot );
				
			} else if (loot instanceof Class<?>) {
				
				item = Generator.random( (Class<? extends Item>)loot );
				
			} else {
				
				item = (Item)loot;
				
			}

            if( item instanceof Gold ) {
                item.quantity = Bestiary.isBoss(this) ?
                        Random.IntRange(400, 600) + item.quantity * 5 :
                        Math.max( 1, item.quantity / (6 - Dungeon.chapter() ) );
            }

            if( item instanceof ThrowingWeaponAmmo || item instanceof Explosives ) {
                item.quantity = Math.max( 1, item.quantity / (6 - Dungeon.chapter() ) );
            }

			Dungeon.level.drop( item, pos ).sprite.drop();
		}
	}
	
	public boolean reset() {
		return false;
	}
	
	public void beckon( int cell ) {

        enemySeen = true;
        target = cell;

        if ( state == WANDERING || state == SLEEPING ) {

            notice();
            state = HUNTING;

        }
	}

    public void inspect( int cell ) {

        if ( state == SLEEPING || state == WANDERING ) {

            state = WANDERING;
            target = cell;

        }
    }
	
	public String description() {
		return "Real description is coming soon!";
	}
	
	public void notice() {
        if( sprite != null && sprite.visible ) {
            sprite.showAlert();
        }
	}
	
	public void yell( String str ) {

        str = "\"" + str + "\"";

		GLog.i("%s: %s", name, str.replaceAll("\\n", " "));

        for( String s : str.split( "\\n" ) ) {
            sprite.showStatus(CharSprite.DEFAULT, Utils.format("%s", s));
        }
	}
	
	public interface AiState {
		public boolean act( boolean enemyInFOV, boolean justAlerted );
		public String status();
	}
	
	private class Sleeping implements AiState {

		public static final String TAG	= "SLEEPING";
		
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

            enemySeen = enemyInFOV && detected( enemy );

			if ( enemySeen && buff( Sleep.class ) == null ) {

                notice();
				state = HUNTING;
				target = enemy.pos;

                for ( Mob mob : Dungeon.level.mobs ) {
                    if ( mob != Mob.this && !mob.enemySeen && ( Level.distance( pos, mob.pos ) <= 2 || Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE ) ) ) {
                        mob.beckon( target );
                    }
                }
				
				spend( TIME_TO_WAKE_UP );
				
			} else {

				spend( TICK );
				
			}
			return true;
		}
		
		@Override
		public String status() {
			return "sleeping";
		}
	}
	
	private class Wandering implements AiState {
		
		public static final String TAG	= "WANDERING";
		
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

            enemySeen = enemyInFOV && detected(enemy);

			if ( enemySeen ) {

                notice();
				state = HUNTING;
				target = enemy.pos;

                for (Mob mob : Dungeon.level.mobs) {
                    if (mob != Mob.this && !mob.enemySeen && ( Level.distance( pos, mob.pos ) <= 2 || Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE ) ) ) {
                        mob.beckon( target );
                    }
                }

                spend( TICK );
				
			} else {
				
				int oldPos = pos;

				if (target != -1 && getCloser( target )) {
					spend( TICK / moveSpeed() );
					return moveSprite( oldPos, pos );
				} else {

                    target = Dungeon.level.randomDestination();
					spend( TICK / moveSpeed() );
				}
				
			}
			return true;
		}
		
		@Override
		public String status() {
			return "wandering";
		}
	}
	
	private class Hunting implements AiState {
		
		public static final String TAG	= "HUNTING";
		
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			enemySeen = enemyInFOV;

            if( justAlerted ) {
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob != Mob.this && !mob.enemySeen && ( Level.distance( pos, mob.pos ) <= 2 || Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE ) ) ) {
                        mob.beckon( target );
                    }
                }
            }

            if (enemySeen && canAttack( enemy )) {

                return doAttack( enemy );

            } else {

                if (enemySeen) {
                    target = enemy.pos;
                }

                int oldPos = pos;

                if (target != -1 && getCloser( target )) {

                    spend( TICK / moveSpeed() );
                    return moveSprite( oldPos,  pos );

                } else {

                    if( enemy != null && enemy.invisible <= 0 ) {

//                        target = enemy.pos;
                        target = nextStepTo( enemy );

                        if (!enemySeen && !detected(enemy) ) {
                            state = WANDERING;
                        }

                    } else {

                        target = Dungeon.level.randomDestination();
                        state = WANDERING;

                    }

                    spend( TICK / moveSpeed() );

                    return true;
                }
            }
		}
		
		@Override
		public String status() {
			return enemySeen ? "attacking you" : "hunting";
		}
	}
	
	protected class Fleeing implements AiState {
		
		public static final String TAG	= "FLEEING";
		
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

            enemySeen = enemyInFOV;

            for (Mob mob : Dungeon.level.mobs) {
                if (mob != Mob.this && !mob.enemySeen && ( Level.distance( pos, mob.pos ) <= 2 || Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE ) ) ) {
                    mob.beckon( target );
                }
            }

            if (enemySeen) {

				target = enemy.pos;

			}

            int oldPos = pos;

            if (target != -1 && getFurther( target )) {

                if (!enemySeen && !detected(enemy) ) {

                    target = Dungeon.level.randomDestination();

                    state = WANDERING;
                }

                spend( TICK / moveSpeed() );

                return moveSprite( oldPos,  pos );

            } else {

				spend( TICK );
				nowhereToRun();

				return true;
			}

//			enemySeen = enemyInFOV;
//
//			if (enemySeen) {
//				target = enemy.pos;
//			}
//
//			int oldPos = pos;
//			if (target != -1 && getFurther( target )) {
//
//				spend( 1 / moveSpeed() );
//				return moveSprite( oldPos, pos );
//
//			} else {
//
//				spend( TICK );
//				nowhereToRun();
//
//				return true;
//			}
		}
		
		protected void nowhereToRun() {
            if (buff( Terror.class ) == null) {
				state = HUNTING;
			}
		}
		
		@Override
		public String status() {
			return "fleeing";
		}
	}
	
	protected class Passive implements AiState {
		
		public static final String TAG	= "PASSIVE";
		
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = false;
			spend( TICK );
			return true;
		}
		
		@Override
		public String status() {
			return "passive";
		}
	}
}
