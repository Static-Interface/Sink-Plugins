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

import de.static_interface.sinklibrary.api.configuration.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestConfiguration extends Configuration {

    public HashMap<String, Object> defaultTestValues = new HashMap<>();

    public TestConfiguration(File file) {
        super(file);
    }

    @Override
    public void addDefaults() {
        defaultTestValues.put("Default.String", "Test");
        defaultTestValues.put("Default.Integer", Integer.MAX_VALUE);
        defaultTestValues.put("Default.Long", Long.MAX_VALUE);
        defaultTestValues.put("Default.Boolean", true);
        defaultTestValues.put("Default.Double", 0.1);
        List<Object> testList = new ArrayList<>();
        testList.add("test1");
        testList.add("test2");
        testList.add(1337);
        testList.add(true);
        testList.add(Long.MAX_VALUE);
        testList.add(0.1);
        defaultTestValues.put("Default.List", testList);

        for (String s : defaultTestValues.keySet()) {
            addDefault(s, defaultTestValues.get(s));
        }
    }

    public HashMap<String, Object> getDefaultTestValues() {
        return defaultTestValues;
    }

    @Override
    public void save() {
        // Do nothing, we dont want to save this
    }
}
