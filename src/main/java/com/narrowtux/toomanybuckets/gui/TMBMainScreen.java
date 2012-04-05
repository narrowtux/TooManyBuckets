/*
 * Copyright (C) 2011 Moritz Schmale <narrow.m@gmail.com>
 *
 * TooManyBuckets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.narrowtux.toomanybuckets.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.narrowtux.narrowtuxlib.NarrowtuxLib;
import com.narrowtux.narrowtuxlib.assistant.GenericWindow;
import com.narrowtux.toomanybuckets.ItemInfo;
import com.narrowtux.toomanybuckets.TMB;
import com.nijikokun.register.narrowtuxlib.payment.Method.MethodAccount;

public class TMBMainScreen extends GenericWindow {
    private SpoutPlayer player;

    private TextField search;

    private List<ItemInfo> visibleItems = new ArrayList<ItemInfo>();

    private Button clearInventoryButton, clearSearchButton, closeWindowButton;

    private Map<GridLocation, ItemButton> buttons = new HashMap<GridLocation, ItemButton>();

    public class GridLocation {
        int x, y;

        public GridLocation(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return x + y * 10000;
        }
    }

    public TMBMainScreen(SpoutPlayer player) {
        super("Too Many Buckets", player);
        this.player = player;
        visibleItems = TMB.getInstance().getDefaultView();
        initScreen();
    }

    private void initScreen() {
        TMB plugin = TMB.getInstance();
        search = new GenericTextField();
        search.setX(getMarginLeft()).setY(getMarginTop() + 25).setHeight(20)
                .setWidth(185);
        attachWidget(plugin, search);
        clearInventoryButton = new GenericButton(ChatColor.RED
                + "Clear Inventory");
        clearInventoryButton.setX(getMarginLeft() + 195).setY(search.getY())
                .setHeight(20).setWidth(185);
        attachWidget(plugin, clearInventoryButton);
        clearSearchButton = new GenericButton("X");
        clearSearchButton.setWidth(20).setHeight(20)
                .setX(getMarginLeft() + 185 - 20).setY(search.getY());
        clearSearchButton.setPriority(RenderPriority.Low);
        clearSearchButton.setTooltip("Clear search");
        closeWindowButton = new GenericButton(ChatColor.RED + "X");
        closeWindowButton.setTooltip("Close Window");
        closeWindowButton.setWidth(20).setHeight(20)
                .setX(getMarginRight() - 26).setY(getMarginTop() - 2);
        closeWindowButton.setPriority(RenderPriority.Low);
        attachWidget(plugin, closeWindowButton);
        search.setTooltip("Search for an item");
        attachWidget(plugin, clearSearchButton);
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 19; x++) {
                GridLocation loc = new GridLocation(x, y);
                ItemButton current = new ItemButton(null, this);
                current.setX(x * 20 + getMarginLeft());
                current.setY(y * 20 + getMarginTop() + 50);
                current.attachToScreen();
                buttons.put(loc, current);
            }
        }
        refreshView();
    }

    public void open() {
        player.getMainScreen().attachPopupScreen(this);
        setDirty(true);
        for (Widget widget : getAttachedWidgets()) {
            widget.setVisible(true);
            widget.setDirty(true);
        }
        refreshView();
    }

    public void hide() {
        player.getMainScreen().closePopup();
    }

    public void handleTextFieldChange(TextField field, String newValue) {
        if (field.equals(search)) {
            doSearch(newValue);
        }
    }

    public void doSearch(String query) {
        List<ItemInfo> result = TMB.getSearchResult(query);
        visibleItems = result;
        refreshView();
    }

    public void handleClick(Button button) {
        int endIx, stackSize;

        if (button.equals(clearInventoryButton)) {
            player.getInventory().clear();
            return;
        }
        if (button.equals(clearSearchButton)) {
            search.setText("");
            search.setDirty(true);
            doSearch("");
            return;
        }
        if (button.equals(closeWindowButton)) {
            player.getMainScreen().closePopup();
            return;
        }
        // if none of the other buttons is clicked, it must be an item button...
        ItemButton ibtn = ItemButton.getByButton(button);
        if (ibtn != null) {
            ItemInfo info = ibtn.getType();
            ItemStack stack = info.stack.clone();
            if (info.price > 0) {
                MethodAccount account = NarrowtuxLib.getMethod().getAccount(
                        player.getName());
                if (account.hasEnough(info.price)) {
                    account.subtract(info.price);
                    player.sendNotification(
                            ChatColor.YELLOW.toString()
                                    + info.stack.getAmount() + " " + info.name,
                            "Bought for "
                                    + NarrowtuxLib.getMethod().format(
                                            info.price), info.stack.getType(),
                            info.stack.getDurability(), 5000);
                    player.getInventory().addItem(stack);
                } else {
                    player.sendNotification(ChatColor.RED
                            + "Not enough money for", info.name,
                            info.stack.getType(), info.stack.getDurability(),
                            5000);
                }
            } else {
                stackSize = String.valueOf(info.stack.getAmount()).length();
                endIx = info.name.length();
                if (endIx + stackSize > 24) {
                    endIx = 24 - stackSize;
                }
                player.sendNotification(
                        ChatColor.YELLOW.toString() + info.stack.getAmount()
                                + " " + info.name.substring(0, endIx),
                        "Added to your inventory", info.stack.getType(),
                        info.stack.getDurability(), 5000);
                if (TMB.getInstance().getCustomConfig().isBroadcast()) {
                    Bukkit.getServer().broadcastMessage(
                            ChatColor.AQUA + "Player "
                                    + player.getDisplayName() + " received "
                                    + Integer.toString(info.stack.getAmount())
                                    + " items of type " + info.name);
                }
                player.getInventory().addItem(stack);
            }
        }
    }

    public void refreshView() {
        int i = 0;
        for (ItemButton button : buttons.values()) {
            ItemInfo info = getInfo(i);
            if (info == null) {
                button.setVisible(false);
            } else {
                button.setVisible(true);
                button.setType(info);
            }
            i++;
        }
    }

    public ItemInfo getInfo(int i) {
        if (visibleItems.size() > i) {
            return visibleItems.get(i);
        } else {
            return null;
        }
    }

    public void handleClose() {

    }
}
