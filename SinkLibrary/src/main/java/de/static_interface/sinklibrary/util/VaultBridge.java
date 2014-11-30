/*
 * Copyright (c) 2013 - 2014 http://static-interface.de and contributors
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

package de.static_interface.sinklibrary.util;

import de.static_interface.sinklibrary.SinkLibrary;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class VaultBridge {

    public static double getBalance(OfflinePlayer account) {
        return SinkLibrary.getInstance().getEconomy().getBalance(account.getName());
    }

    public static double getBalance(String account) {
        return getBalance(Bukkit.getOfflinePlayer(account));
    }

    public static boolean addBalance(OfflinePlayer account, double amount) {
        if (amount == 0) {
            return true;
        }

        Economy economy = SinkLibrary.getInstance().getEconomy();
        double roundedAmount = MathUtil.round(amount);

        EconomyResponse response;
        boolean withdraw = roundedAmount < 0;

        roundedAmount = Math.abs(roundedAmount);

        if (withdraw) {
            response = economy.withdrawPlayer(account, roundedAmount);
        } else {
            response = economy.depositPlayer(account, roundedAmount);
        }
        return response.transactionSuccess();
    }

    @Deprecated
    public static boolean addBalance(String account, double amount) {
        return addBalance(Bukkit.getOfflinePlayer(account), amount);
    }

    public static boolean isAccountAvailable(String account) {
        Economy economy = SinkLibrary.getInstance().getEconomy();
        return economy.hasAccount(account);
    }

    public static void createAccount(String account) {
        if (isAccountAvailable(account)) {
            return;
        }
        Economy economy = SinkLibrary.getInstance().getEconomy();
        economy.createPlayerAccount(Bukkit.getOfflinePlayer(account));
    }

    public static String getCurrenyName() {
        return SinkLibrary.getInstance().getEconomy().currencyNamePlural();
    }
}
