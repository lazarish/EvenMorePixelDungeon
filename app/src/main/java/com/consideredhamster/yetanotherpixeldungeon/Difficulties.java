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
package com.consideredhamster.yetanotherpixeldungeon;

public class Difficulties {

	public static final int EASY			= 0;
	public static final int NORMAL			= 1;
	public static final int HARDCORE		= 2;
	public static final int IMPOSSIBLE		= 3;
	
	public static final String[] NAMES = {
		"Easy",
		"Normal",
		"Hardcore",
		"Impossible"
	};

    public static final String[] ABOUT = {

            "- You receive less damage\n" +
            "- Mob's health is minimized\n" +
            "- Bosses have 80% health\n" +
            "- Prevents earning any badges!\n",

            "- You receive normal damage\n" +
            "- Mob's health is randomized\n" +
            "- Bosses have 100% health\n"+
            "- Nothing special here!\n",

            "- You receive normal damage\n" +
            "- Mob's health is maximized\n" +
            "- Bosses have 120% health\n"+
            "- Beat the game on Normal to unlock!\n",

            "- You receive more damage\n" +
            "- Mob's health is maximized\n" +
            "- Bosses have 150% health\n"+
            "- Beat the game on Hardcore to unlock!\n",
    };
}
