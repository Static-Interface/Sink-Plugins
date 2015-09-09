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

package de.static_interface.sinklibrary.api.provider;

import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;

import java.util.UUID;

import javax.annotation.Nullable;

public interface BanProvider {

    void ban(IngameUser user);

    void ban(IngameUser user, @Nullable SinkUser banner);

    void ban(IngameUser user, @Nullable SinkUser banner, @Nullable String reason);

    void ban(IngameUser user, @Nullable String reason);

    void ban(IngameUser user, @Nullable SinkUser banner, @Nullable Long timeOut);

    void ban(IngameUser user, @Nullable Long unbanTime);

    void ban(IngameUser user, @Nullable String reason, @Nullable Long timeOut);

    void ban(IngameUser user, @Nullable SinkUser banner, @Nullable String reason, @Nullable Long timeOut);

    void unban(IngameUser user);

    void unban(IngameUser user, SinkUser unbanner);

    boolean isBanned(IngameUser user);

    void setUnbanTime(IngameUser user, @Nullable Long timeOut);

    void setReason(IngameUser user, String reason);

    @Nullable
    String getReason(IngameUser user);

    @Nullable
    Long getBanTime(IngameUser user);

    @Nullable
    Long getUnbanTime(IngameUser user);

    Long getTimeOut(IngameUser user);

    void setTimeOut(IngameUser user, @Nullable Long timeOut);

    @Nullable
    String getBannerDisplayName(IngameUser user);

    @Nullable
    UUID getBannerUniqueId(IngameUser user);

    @Nullable
    String getUnbannerDisplayName(IngameUser user);

    @Nullable
    UUID getUnbannerUniqueId(IngameUser user);

    void setBanner(IngameUser user, SinkUser banner);

    void setUnbanner(IngameUser user, SinkUser unbanner);
}
