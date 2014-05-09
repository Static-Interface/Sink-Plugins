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

package de.static_interface.sinklibrary.tests;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest
{
    @Test
    public void testConfiguration()
    {
        URL url = getClass().getResource("/TestConfiguration.yml");
        File testFile = new File(url.getFile());

        TestConfiguration config = new TestConfiguration(testFile);
        HashMap<String, Object> defaultTestValues = config.getDefaultTestValues();
        for ( String s : defaultTestValues.keySet() )
        {
            assertEquals(defaultTestValues.get(s), config.get(s));
        }
    }
}
