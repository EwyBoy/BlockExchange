package com.ewyboy.blockexchange.client;

import com.ewyboy.blockexchange.client.ui.NumericEditBox;
import com.ewyboy.blockexchange.database.entities.DbItem;
import com.ewyboy.blockexchange.database.entities.DbOrder;
import com.ewyboy.blockexchange.database.entities.DbPlayer;
import com.ewyboy.blockexchange.database.enums.OrderState;
import com.ewyboy.blockexchange.database.enums.OrderTypes;
import com.ewyboy.blockexchange.database.services.DatabaseService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

public class ExchangeBuyScreen extends Screen {

    private final ItemStack itemStack;

    private NumericEditBox amountBox;
    private NumericEditBox priceBox;

    private Button cancelButton;
    private Button backButton;

    private Button buttonAmountPlus;
    private Button buttonAmountMinus;

    private Button buttonBuyPlus;
    private Button buttonBuyMinus;

    private Button buttonBuy;

    public ExchangeBuyScreen(ItemStack itemStack) {
        super(Component.literal("Block Exchange - Buy"));
        this.itemStack = itemStack;
    }

    @Override
    protected void init() {
        super.init();

        this.amountBox = new NumericEditBox(this.font, this.width / 2 - 100, this.height / 2 - 20, 200, 20, Component.literal("Amount"));
        this.priceBox = new NumericEditBox(this.font, this.width / 2 - 100, this.height / 2 + 20, 200, 20, Component.literal("Price"));

        this.addRenderableWidget(this.amountBox);
        this.addRenderableWidget(this.priceBox);

        this.cancelButton = this.addRenderableWidget(
                Button.builder(Component.literal("x"), button -> this.onClose())
                        .pos(this.width / 2 + 130, this.height / 2 - 100)
                        .size(20, 20)
                        .build()
        );

        this.backButton = this.addRenderableWidget(
                Button.builder(Component.literal("<-"), button -> Objects.requireNonNull(minecraft).setScreen(new ExchangeBrowseScreen()))
                        .pos(this.width / 2 - 150, this.height / 2 - 100)
                        .size(20, 20)
                        .build()
        );

        this.buttonAmountMinus = this.addRenderableWidget(
                Button.builder(Component.literal("-"), this::onPressButtonAmountMinus)
                        .pos(this.width / 2 - 120, this.height / 2 - 20)
                        .size(20, 20)
                        .build()
        );

        this.buttonAmountPlus = this.addRenderableWidget(
                Button.builder(Component.literal("+"), this::onPressButtonAmountPlus)
                        .pos(this.width / 2 + 100, this.height / 2 - 20)
                        .size(20, 20)
                        .build()
        );

        this.buttonBuyPlus = this.addRenderableWidget(
                Button.builder(Component.literal("+"), this::onPressButtonBuyPlus)
                        .pos(this.width / 2 + 100, this.height / 2 + 20)
                        .size(20, 20)
                        .build()
        );

        this.buttonBuyMinus = this.addRenderableWidget(
                Button.builder(Component.literal("-"), this::onPressButtonBuyMinus)
                        .pos(this.width / 2 - 120, this.height / 2 + 20)
                        .size(20, 20)
                        .build()
        );


        this.buttonBuy = this.addRenderableWidget(
                Button.builder(Component.literal("Buy"), button -> placeBuyOrder())
                        .pos(this.width / 2 - 50, this.height / 2 + 50)
                        .size(100, 20)
                        .build()
        );

        this.buttonBuy.active = false;
    }

    private void placeBuyOrder() {
        DatabaseService<DbOrder> orderService = new DatabaseService<>(DbOrder.class);
        DatabaseService<DbItem> itemService = new DatabaseService<>(DbItem.class);
        DatabaseService<DbPlayer> playerService = new DatabaseService<>(DbPlayer.class);

        DbOrder order = new DbOrder();

        ResourceLocation itemLocation = BuiltInRegistries.ITEM.getKey(itemStack.getItem());

        order.setAmount(this.amountBox.getNumericValue());
        order.setPricePerItem(this.priceBox.getNumericValue());
        order.setOrderType(OrderTypes.BUY);
        order.setOrderState(OrderState.OPEN);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setItem(itemService.findById(itemLocation.toString()));
        if (minecraft != null) {
            if (minecraft.player != null) {
                order.setPlayer(playerService.findById(minecraft.player.getUUID()));
            }
        }
        orderService.saveOrUpdate(order);

        onClose();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float partialTick) {
        super.renderBackground(graphics, pMouseX, pMouseY, partialTick);

        graphics.fill(this.width / 2 - 150, this.height / 2 - 100, this.width / 2 + 150, this.height / 2 + 100, 0x80000000);
        graphics.renderItem(itemStack, this.width / 2 - 8, this.height / 2 - 50);
        graphics.drawCenteredString(this.font, "Buy " + itemStack.getHoverName().getString(), this.width / 2, this.height / 2 - 60, 0xFFFFFF);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float partialTick) {
        super.render(graphics, pMouseX, pMouseY, partialTick);

        graphics.drawCenteredString(this.font, "Amount", this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "Price", this.width / 2, this.height / 2 + 10, 0xFFFFFF);
    }

    private void updateBuyButton() {
        this.buttonBuy.active = this.priceBox.getNumericValue() > 0 && this.amountBox.getNumericValue() > 0;
    }

    private void onPressButtonAmountPlus(Button button) {
        this.amountBox.setNumericValue(this.amountBox.getNumericValue() + 1);
        updateBuyButton();
    }

    private void onPressButtonAmountMinus(Button button) {
        if (this.amountBox.getNumericValue() < 0) return;
        this.amountBox.setNumericValue(this.amountBox.getNumericValue() - 1);
        updateBuyButton();
    }

    private void onPressButtonBuyPlus(Button button) {
        this.priceBox.setNumericValue(this.priceBox.getNumericValue() + 1);
        updateBuyButton();
    }

    private void onPressButtonBuyMinus(Button button) {
        if (this.priceBox.getNumericValue() < 0) return;
        this.priceBox.setNumericValue(this.priceBox.getNumericValue() - 1);
        updateBuyButton();
    }

}
