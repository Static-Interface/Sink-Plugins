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

package de.static_interface.sinklibrary.provider;

import de.static_interface.sinklibrary.api.provider.BanProvider;
import de.static_interface.sinklibrary.api.user.IdentifiableUser;
import de.static_interface.sinklibrary.api.user.SinkUser;
import de.static_interface.sinklibrary.user.IngameUser;
import de.static_interface.sinklibrary.util.StringUtil;

import java.util.UUID;

import javax.annotation.Nullable;

public class SimpleBanProvider implements BanProvider {

    @Override
    public void ban(IngameUser user) {
        ban(user, null, null, null);
    }

    @Override
    public void ban(IngameUser user, @Nullable SinkUser banner) {
        ban(user, banner, null, null);
    }

    @Override
    public void ban(IngameUser user, @Nullable SinkUser banner, @Nullable String reason) {
        ban(user, banner, reason, null);
    }

    @Override
    public void ban(IngameUser user, @Nullable String reason) {
        ban(user, null, reason, null);
    }

    @Override
    public void ban(IngameUser user, @Nullable SinkUser banner, @Nullable Long timeOut) {
        ban(user, banner, null, timeOut);
    }

    @Override
    public void ban(IngameUser user, @Nullable Long timeOut) {
        ban(user, null, null, timeOut);
    }

    @Override
    public void ban(IngameUser user, @Nullable String reason, @Nullable Long timeOut) {
        ban(user, null, reason, timeOut);
    }

    @Override
    public void ban(IngameUser user, @Nullable SinkUser banner, @Nullable String reason, @Nullable Long timeOut) {
        boolean isOnline = user.isOnline();
        if (isOnline) {
            user.getPlayer().kickPlayer(reason);
        }

        if (isBanned(user)) {
            return;
        }

        user.getConfiguration().setBanned(true);
        user.getConfiguration().setBanTime(System.currentTimeMillis());
        setTimeOut(user, timeOut);
        setReason(user, reason);
        setBanner(user, banner);
    }


    @Override
    public void unban(IngameUser user) {
        unban(user, null);
    }

    @Override
    public void unban(IngameUser user, SinkUser unbanner) {
        user.getConfiguration().setBanned(false);
        setUnbanTime(user, System.currentTimeMillis());
        setUnbanner(user, unbanner);
    }

    @Override
    public boolean isBanned(IngameUser user) {
        return user.getConfiguration().isBanned();
    }

    @Override
    public void setUnbanTime(IngameUser user, @Nullable Long unbanTime) {
        user.getConfiguration().setUnbanTime(unbanTime);
    }

    @Override
    public void setReason(IngameUser user, String reason) {
        user.getConfiguration().setBanReason(reason);
    }

    @Nullable
    @Override
    public String getReason(IngameUser user) {
        String s = user.getConfiguration().getBanReason();
        if (StringUtil.isEmptyOrNull(s)) {
            return null;
        }
        return s;
    }

    @Nullable
    @Override
    public Long getBanTime(IngameUser user) {
        long s = user.getConfiguration().getBanTime();
        if (s <= 0) {
            return null;
        }
        return s;
    }

    @Nullable
    @Override
    public Long getUnbanTime(IngameUser user) {
        long s = user.getConfiguration().getUnbanTime();
        if (s <= 0) {
            return null;
        }
        return s;
    }

    @Override
    public Long getTimeOut(IngameUser user) {
        long s = user.getConfiguration().getBanTimeOut();
        if (s <= 0) {
            return null;
        }
        return s;
    }

    @Override
    public void setTimeOut(IngameUser user, @Nullable Long timeOut) {
        user.getConfiguration().setBanTimeOut(timeOut);
    }

    @Nullable
    @Override
    public String getBannerDisplayName(IngameUser user) {
        return user.getConfiguration().getBannerDisplayName();
    }

    @Nullable
    @Override
    public UUID getBannerUniqueId(IngameUser user) {
        return user.getConfiguration().getBannerUniqueId();
    }

    @Nullable
    @Override
    public String getUnbannerDisplayName(IngameUser user) {
        return user.getConfiguration().getUnbannerDisplayName();
    }

    @Nullable
    @Override
    public UUID getUnbannerUniqueId(IngameUser user) {
        return user.getConfiguration().getUnbannerUniqueId();
    }

    @Override
    public void setBanner(IngameUser user, SinkUser banner) {
        UUID id = null;
        if (banner instanceof IdentifiableUser) {
            id = ((IdentifiableUser) banner).getUniqueId();
        }

        user.getConfiguration().setBannerUniqueId(id);
        user.getConfiguration().setBannerDisplayName(banner == null ? null : banner.getDisplayName());
    }

    @Override
    public void setUnbanner(IngameUser user, SinkUser unbanner) {
        UUID id = null;
        if (unbanner instanceof IdentifiableUser) {
            id = ((IdentifiableUser) unbanner).getUniqueId();
        }

        user.getConfiguration().setUnbannerUniqueId(id);
        user.getConfiguration().setUnbannerDisplayName(unbanner == null ? null : unbanner.getDisplayName());
    }
}
