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

package com.mojang.api.profiles;

import com.google.gson.Gson;
import com.mojang.api.http.BasicHttpClient;
import com.mojang.api.http.HttpBody;
import com.mojang.api.http.HttpClient;
import com.mojang.api.http.HttpHeader;
import de.static_interface.sinklibrary.SinkLibrary;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class HttpProfileRepository implements ProfileRepository
{

    private static final int MAX_PAGES_TO_CHECK = 100;
    private static Gson gson = new Gson();
    private HttpClient client;

    public HttpProfileRepository()
    {
        this(BasicHttpClient.getInstance());
    }

    public HttpProfileRepository(HttpClient client)
    {
        this.client = client;
    }

    @Override
    public Profile[] findProfilesByCriteria(ProfileCriteria... criteria)
    {
        try
        {
            HttpBody body = new HttpBody(gson.toJson(criteria));
            List<HttpHeader> headers = new ArrayList<>();
            headers.add(new HttpHeader("Content-Type", "application/json"));
            List<Profile> profiles = new ArrayList<>();
            for ( int i = 1; i <= MAX_PAGES_TO_CHECK; i++ )
            {
                ProfileSearchResult result = post(new URL("https://api.mojang.com/profiles/page/" + i), body, headers);
                if ( result.getSize() == 0 )
                {
                    break;
                }
                profiles.addAll(Arrays.asList(result.getProfiles()));
            }
            return profiles.toArray(new Profile[profiles.size()]);
        }
        catch ( Exception e )
        {
            SinkLibrary.getCustomLogger().log(Level.SEVERE, "Couldn't get player UUID", e);
            return new Profile[0];
        }
    }

    private ProfileSearchResult post(URL url, HttpBody body, List<HttpHeader> headers) throws IOException
    {
        String response = client.post(url, body, headers);
        return gson.fromJson(response, ProfileSearchResult.class);
    }

}