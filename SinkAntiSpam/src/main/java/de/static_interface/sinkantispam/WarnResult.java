/*
 * Copyright (c) 2013 - 2014 http://adventuria.eu, http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinkantispam;

public class WarnResult
{
    private int resultCode;
    public String censoredMessage;

    public static final int PASS = 0;
    public static final int CENSOR = 1;
    public static final int CANCEL = 2;

    public void setCensoredMessage(String censoredMessage) { this.censoredMessage = censoredMessage; }

    public String getCensoredMessage() { return censoredMessage; }

    public void setResultcode(int resultCode) { this.resultCode = resultCode; }

    public int getResultCode()
    {
        return resultCode;
    }
}
