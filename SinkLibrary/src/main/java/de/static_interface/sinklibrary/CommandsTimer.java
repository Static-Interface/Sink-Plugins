/*
 * Copyright (c) 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import static de.static_interface.sinklibrary.Constants.TICK;


public class CommandsTimer implements Runnable
{
    private final transient Set<String> onlineUsers = new HashSet<>();
    private transient long lastPoll = System.nanoTime();
    private final LinkedList<Double> history = new LinkedList<>();
    private int skip1 = 0;
    private int skip2 = 0;

    CommandsTimer()
    {
        history.add(TICK);
    }

    @Override
    public void run()
    {
        final long startTime = System.nanoTime();
        long timeSpent = (startTime - lastPoll) / 1000;
        if ( timeSpent == 0 )
        {
            timeSpent = 1;
        }
        if ( history.size() > 10 )
        {
            history.remove();
        }
        long tickInterval = 50;
        double tps = tickInterval * 1000000.0 / timeSpent;
        if ( tps <= 21 )
        {
            history.add(tps);
        }
        lastPoll = startTime;
        int count = 0;
        for ( Player player : Bukkit.getOnlinePlayers() )
        {
            count++;
            if ( skip1 > 0 )
            {
                skip1--;
                continue;
            }
            if ( count % 10 == 0 )
            {
                long maxTime = 10 * 1000000;
                if ( System.nanoTime() - startTime > maxTime / 2 )
                {
                    skip1 = count - 1;
                    break;
                }
            }
            onlineUsers.add(player.getName());
        }

        count = 0;
        final Iterator<String> iterator = onlineUsers.iterator();
        while ( iterator.hasNext() )
        {
            count++;
            if ( skip2 > 0 )
            {
                skip2--;
                continue;
            }
            if ( count % 10 == 0 )
            {
                long maxTime = 10 * 1000000;
                if ( System.nanoTime() - startTime > maxTime )
                {
                    skip2 = count - 1;
                    break;
                }
            }
        }
    }

    public double getAverageTPS()
    {
        double avg = 0;
        for ( Double d : history )
        {
            if ( d != null )
            {
                avg += d;
            }
        }
        return avg / history.size();
    }
}