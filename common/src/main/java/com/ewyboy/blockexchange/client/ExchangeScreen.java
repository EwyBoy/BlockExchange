package com.ewyboy.blockexchange.client;

import com.ewyboy.blockexchange.database.entities.DbItem;
import com.ewyboy.blockexchange.database.services.DatabaseService;
import com.ewyboy.blockexchange.helpers.IntCounter;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class ExchangeScreen extends Screen {

    private List<DbItem> items = null;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private DbItem hoveredItem = null;
    private EditBox searchBox;

    public ExchangeScreen(Component component) {
        super(component);
    }

    @Override
    protected void init() {
        DatabaseService<DbItem> itemService = new DatabaseService<>(DbItem.class);
        items = itemService.findAll();

        int searchBoxWidth = (int) (width * 0.4);
        searchBox = new EditBox(font, width / 2 - searchBoxWidth / 2, 20, searchBoxWidth, 20, Component.literal("Search..."));
        searchBox.setMaxLength(128);
        searchBox.setFocused(false);
        addRenderableWidget(searchBox);

        searchBox.setResponder(value -> {
            if (value == null || value.isEmpty()) {
                items = itemService.findAll();
            } else {
                items = itemService.runQuery(
                        "SELECT o FROM DbItem o WHERE o.path LIKE :path",
                        query -> query.setParameter("path", "%" + value + "%")
                );
            }
            calculateMaxScroll();
        });

        calculateMaxScroll();
    }

    private void calculateMaxScroll() {
        int rows = (items.size() + getItemsPerRow() - 1) / getItemsPerRow();
        int visibleRows = (height - 100) / 20;
        maxScroll = Math.max(0, (rows - visibleRows) * 20);
    }

    private int getItemsPerRow() {
        return Math.max(1, (width - 100) / 20);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        scrollOffset = (int) Math.max(0, Math.min(scrollOffset - pScrollY * 20, maxScroll));
        return true;
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        hoveredItem = null;

        int padding = 50;
        int xOffset = padding + 10;
        int yOffset = padding + 10 - scrollOffset;

        IntCounter x = new IntCounter(xOffset, 20);
        IntCounter y = new IntCounter(yOffset, 20);

        for (DbItem dbItem : items) {
            if (pMouseX >= x.getValue() && pMouseX <= x.getValue() + 16 &&
                    pMouseY >= y.getValue() && pMouseY <= y.getValue() + 16) {
                hoveredItem = dbItem;
                break;
            }
            x.increment();
            if (x.getValue() >= width - padding - 20) {
                x.setValue(xOffset);
                y.increment();
            }
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (searchBox != null) {
            if (pKeyCode != InputConstants.KEY_ESCAPE) {
                if (searchBox.isFocused()) {
                    return searchBox.keyPressed(pKeyCode, pScanCode, pModifiers);
                } else {
                    searchBox.setFocused(true);
                    return true;
                }
            }
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);

        int padding = 50;
        graphics.fill(padding - 5, padding - 5, width - padding + 5, height - padding + 5, Color.GREEN.hashCode());
        graphics.fill(padding, padding, width - padding, height - padding, 0xFF000000);

        graphics.drawCenteredString(font, "Block Exchange", width / 2, 10, 0xFFFFFF);

        if (items == null || items.isEmpty()) {
            graphics.drawCenteredString(font, "No items found", width / 2, height / 2, 0xAAAAAA);
            return;
        }

        graphics.fill(padding, padding, width - padding, padding + 20, 0xFF000000);

        int xOffset = padding + 10;
        int yOffset = padding + 10 - scrollOffset;

        IntCounter x = new IntCounter(xOffset, 20);
        IntCounter y = new IntCounter(yOffset, 20);

        items.forEach(dbItem -> {
            if (y.getValue() >= padding && y.getValue() <= height - padding) {
                ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(dbItem.getNamespace(), dbItem.getPath());
                Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                graphics.renderItem(new ItemStack(item), x.getValue(), y.getValue());
            }

            x.increment();
            if (x.getValue() >= width - padding - 20) {
                x.setValue(xOffset);
                y.increment();
            }
        });

        if (hoveredItem != null) {
            graphics.drawString(font, hoveredItem.getPath(), pMouseX + 10, pMouseY - 10, 0xFFFFFF);
        }
    }
}
