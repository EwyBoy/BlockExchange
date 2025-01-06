package com.ewyboy.blockexchange.client;

import com.ewyboy.blockexchange.database.entities.DbItem;
import com.ewyboy.blockexchange.database.entities.DbOrder;
import com.ewyboy.blockexchange.database.services.DatabaseService;
import com.ewyboy.blockexchange.helpers.IntCounter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExchangeScreen extends Screen {

    private List<DbOrder> orders = null;
    private List<DbItem> items = null;
    private EditBox searchBox = null;

    public ExchangeScreen(Component component) {
        super(component);
    }

    @Override
    protected void init() {
        DatabaseService<DbOrder> dbOrderService = new DatabaseService<>(DbOrder.class);
        orders = dbOrderService.findAll();

        // Create an edit box as a search bar
        searchBox = new EditBox(font, width / 2 - 100, 20, 200, 20, Component.literal("Search..."));
        searchBox.setMaxLength(100);
        addRenderableWidget(searchBox);

        searchBox.setResponder(value -> {
            if (value == null || value.isEmpty()) {
                orders = dbOrderService.findAll();
            } else {
                orders = dbOrderService.runQuery(
                        "SELECT o FROM DbOrder o WHERE o.item.id IN (SELECT i.id FROM DbItem i WHERE i.path LIKE :path)",
                        query -> query.setParameter("path", "%" + value + "%"
                ));
            }
        });
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);

        // draw a pink border
        graphics.fill(45, 45, width - 45, height - 45, 0xFFFF00FF);

        // draw a black background
        graphics.fill(50, 50, width - 50, height - 50, 0xFF000000);

        // Render the background of the screen
        graphics.drawCenteredString(font, "Block Exchange", width / 2, 10, 0xFFFFFF);

        IntCounter y = new IntCounter(50, 10);

        // filter orders by the search bar
        orders.forEach(order -> {
            DbItem dbItem = order.getItem();
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(dbItem.getNamespace(), dbItem.getPath());
            Item item = BuiltInRegistries.ITEM.get(resourceLocation);

            // get the name of the player who created the order
            String playerName = order.getPlayer().getUsername();
            String orderText = order.getOrderType().name() + " " + order.getAmount() + " " + item.getDescription().getString() + " for " + order.getPricePerItem() + " from " + playerName;

            graphics.renderItem(new ItemStack(item, order.getAmount()), 60, y.getValue());
            graphics.drawString(font, Component.literal(orderText), 60 + 20, y.getValue() + 5, 0xFFFFFF);

            y.add(20);
        });
    }

}
