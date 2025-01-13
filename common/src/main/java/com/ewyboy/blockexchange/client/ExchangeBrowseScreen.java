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

public class ExchangeBrowseScreen extends Screen {

    private List<DbItem> items = null;
    private DbItem hoveredItem = null;
    private EditBox searchBox;

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int lastMouseY = 0;
    private boolean isDraggingScrollbar = false;

    public ExchangeBrowseScreen() {
        super(Component.literal("Block Exchange - Browse"));
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button == 0) {
            int padding = 50;
            int xOffset = padding + 10;
            int yOffset = padding + 10 - scrollOffset;

            IntCounter x = new IntCounter(xOffset, 20);
            IntCounter y = new IntCounter(yOffset, 20);

            items.forEach(dbItem -> {
                if (dbItem.equals(hoveredItem)) {
                    ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(dbItem.getNamespace(), dbItem.getPath());
                    Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                    ItemStack itemStack = new ItemStack(item);
                    if (minecraft != null) {
                        minecraft.setScreen(new ExchangeBuyScreen(itemStack));
                    }
                }

                x.increment();
                if (x.getValue() >= width - padding - 20) {
                    x.setValue(xOffset);
                    y.increment();
                }
            });
        }

        if (button == 0 && maxScroll > 0) {
            int padding = 50;
            int scrollbarWidth = 6;
            int scrollbarHeight = height - padding * 2;
            int scrollbarX = width - padding - scrollbarWidth;
            int contentHeight = (items.size() + getItemsPerRow() - 1) / getItemsPerRow() * 20;
            float scrollRatio = (float) scrollbarHeight / contentHeight;
            int scrollbarThumbHeight = Math.max((int) (scrollbarHeight * scrollRatio), 10);
            int scrollbarThumbY = padding + (int) ((scrollOffset / (float) maxScroll) * (scrollbarHeight - scrollbarThumbHeight));

            if (mouseX >= scrollbarX &&
                    mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarThumbY &&
                    mouseY <= scrollbarThumbY + scrollbarThumbHeight
            ) {
                isDraggingScrollbar = true;
                lastMouseY = (int) mouseY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDraggingScrollbar) {
            int padding = 50;
            int scrollbarHeight = height - padding * 2;

            int deltaYInt = (int) (mouseY - lastMouseY);
            float scrollRatio = (float) maxScroll / (scrollbarHeight - Math.max((int) (scrollbarHeight / (float) items.size()), 10));
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset + (int) (deltaYInt * scrollRatio)));

            lastMouseY = (int) mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDraggingScrollbar = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
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
        if (pKeyCode == InputConstants.KEY_HOME) {
            scrollOffset = 0;
            return true;
        } else if (pKeyCode == InputConstants.KEY_END) {
            scrollOffset = maxScroll;
            return true;
        }

        if (pKeyCode == InputConstants.KEY_UP || pKeyCode == InputConstants.KEY_PAGEUP) {
            scrollOffset = Math.max(0, scrollOffset - 20);
            return true;
        } else if (pKeyCode == InputConstants.KEY_DOWN || pKeyCode == InputConstants.KEY_PAGEDOWN) {
            scrollOffset = Math.min(maxScroll, scrollOffset + 20);
            return true;
        }

        if (searchBox != null) {
            if (pKeyCode != InputConstants.KEY_ESCAPE) {
                if (searchBox.isFocused()) {
                    scrollOffset = 0;
                    return searchBox.keyPressed(pKeyCode, pScanCode, pModifiers);
                } else {
                    searchBox.setFocused(false);
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
        graphics.fill(padding - 5, padding - 5, width - padding + 5, height - padding + 5, Color.DARK_GRAY.hashCode());
        graphics.fill(padding, padding, width - padding, height - padding, 0x80000000);

        graphics.drawCenteredString(font, "Block Exchange", width / 2, 10, 0xFFFFFF);

        if (items == null || items.isEmpty()) {
            graphics.drawCenteredString(font, "No items found", width / 2, height / 2, 0xAAAAAA);
            return;
        }

        renderScrollbar(graphics, padding);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float partialTick) {
        super.render(graphics, pMouseX, pMouseY, partialTick);

        int padding = 50;
        int xOffset = padding + 10;
        int yOffset = padding + 10 - scrollOffset;

        IntCounter x = new IntCounter(xOffset, 20);
        IntCounter y = new IntCounter(yOffset, 20);

        items.forEach(dbItem -> {
            if (dbItem.equals(hoveredItem)) {
                graphics.fill(x.getValue(), y.getValue(), x.getValue() + 16, y.getValue() + 16, 0x50FFFFFF);
            }

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
            renderHoveredItem(graphics, pMouseX, pMouseY);
        }
    }

    private void renderHoveredItem(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(hoveredItem.getNamespace(), hoveredItem.getPath());
        Item item = BuiltInRegistries.ITEM.get(resourceLocation);

        ItemStack itemStack = new ItemStack(item);
        Component displayName = itemStack.getHoverName();

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300);
        graphics.drawCenteredString(font, displayName, pMouseX + 10, pMouseY - 10, 0xFFFFFF);
        graphics.pose().popPose();
    }

    private void renderScrollbar(@NotNull GuiGraphics graphics, int padding) {
        if (maxScroll > 0) {
            int scrollbarHeight = height - padding * 2;
            int scrollbarWidth = 6;
            int scrollbarX = width - padding - scrollbarWidth;

            int contentHeight = (items.size() + getItemsPerRow() - 1) / getItemsPerRow() * 20;
            float scrollRatio = (float) scrollbarHeight / contentHeight;
            int scrollbarThumbHeight = Math.max((int) (scrollbarHeight * scrollRatio), 10);
            int scrollbarThumbY = padding + (int) ((scrollOffset / (float) maxScroll) * (scrollbarHeight - scrollbarThumbHeight));

            graphics.fill(scrollbarX, padding, scrollbarX + scrollbarWidth, padding + scrollbarHeight, 0xFF333333);
            graphics.fill(scrollbarX, scrollbarThumbY, scrollbarX + scrollbarWidth, scrollbarThumbY + scrollbarThumbHeight, 0xFF888888);
        }
    }

}
