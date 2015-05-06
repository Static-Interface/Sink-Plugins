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

package de.static_interface.sinkirc.queue;

import de.static_interface.sinkirc.SinkIRC;
import de.static_interface.sinklibrary.api.event.IrcSendMessageEvent;
import de.static_interface.sinklibrary.util.Debug;
import org.bukkit.Bukkit;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

public class IrcQueue {

    public static final int INTERVAL = 64; //Todo, make configurable?
    private static IrcQueue instance;
    private final Deque<QueuedIrcMessage> queue = new ConcurrentLinkedDeque<>();

    Thread queueThread;
    private long lastTime = 0;
    private boolean work = true;

    public static IrcQueue getInstance() {
        if (instance == null) {
            instance = new IrcQueue();
        }

        return instance;
    }

    public static synchronized void addToQueue(String message, String target) {
        Debug.log(Level.INFO, "[Queue] Adding to queue: " + message + " @ " + target);
        getInstance().queue.offer(new QueuedIrcMessage(message, target));
    }

    private void doWork() {
        try {
            QueuedIrcMessage msg = queue.pop();
            if (msg == null) {
                return;
            }

            IrcSendMessageEvent event = new IrcSendMessageEvent(msg.getMessage(), msg.getTarget());
            Bukkit.getPluginManager().callEvent(event);
        } catch (NoSuchElementException ignored) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        work = false;
        queue.clear();
        if (queueThread != null) {
            queueThread.interrupt();
        }
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
                            Thread.sleep(INTERVAL - difference);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "IRC-Queue Thread");
        SinkIRC.getInstance().getLogger().log(Level.INFO, "[Queue] IRC Queue Thread started");
        queueThread.start();
    }
}
