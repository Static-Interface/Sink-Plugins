/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkirc;

import de.static_interface.sinklibrary.api.event.IrcSendMessageEvent;
import org.bukkit.Bukkit;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class IrcQueue {

    public static final int INTERVAL = 250; //Todo, make configurable?
    private static IrcQueue instance;
    private final Deque<String> messageQueue = new ConcurrentLinkedDeque<>();
    private final Deque<String> targetQueue = new ConcurrentLinkedDeque<>();
    Thread queueThread;
    private long lastTime = 0;
    private boolean work = true;

    public static IrcQueue getInstance() {
        if (instance == null) {
            instance = new IrcQueue();
        }

        return instance;
    }

    public static void addToQueue(String message, String target) {
        getInstance().messageQueue.offer(message);
        getInstance().targetQueue.offer(target);
    }

    private void doWork() {
        String msg = messageQueue.pop();
        String target = targetQueue.pop();
        if (msg == null || target == null) {
            return;
        }
        IrcSendMessageEvent event = new IrcSendMessageEvent(msg, target);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void stop() {
        work = false;
        messageQueue.clear();
        targetQueue.clear();
        queueThread.interrupt();
    }

    public void start() {
        work = true;
        queueThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (work) {
                    long difference = System.currentTimeMillis() - lastTime;
                    if (difference >= INTERVAL) {
                        doWork();
                    } else {
                        try {
                            Thread.sleep(difference);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "IRC-Queue Thread");

        queueThread.start();
    }
}
