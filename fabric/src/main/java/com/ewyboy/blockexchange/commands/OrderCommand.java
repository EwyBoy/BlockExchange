package com.ewyboy.blockexchange.commands;

import com.ewyboy.blockexchange.database.services.DatabaseService;
import com.ewyboy.blockexchange.database.entities.DbItem;
import com.ewyboy.blockexchange.database.entities.DbOrder;
import com.ewyboy.blockexchange.database.entities.DbPlayer;
import com.ewyboy.blockexchange.database.enums.OrderState;
import com.ewyboy.blockexchange.database.enums.OrderTypes;
import com.ewyboy.blockexchange.database.services.DatabaseServiceRegistry;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.time.LocalDateTime;

public class OrderCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, dedicated) ->
                dispatcher.register(Commands.literal("exchange")
                        .then(Commands.literal("buy")
                                .then(Commands.argument("item", ItemArgument.item(commandBuildContext))
                                        .then(Commands.argument("quantity", IntegerArgumentType.integer())
                                                .then(Commands.argument("price", DoubleArgumentType.doubleArg())
                                                        .executes(context -> placeOrder(context, OrderTypes.BUY))))))
                        .then(Commands.literal("sell")
                                .then(Commands.argument("item", ItemArgument.item(commandBuildContext))
                                        .then(Commands.argument("quantity", IntegerArgumentType.integer())
                                                .then(Commands.argument("price", DoubleArgumentType.doubleArg())
                                                        .executes(context -> placeOrder(context, OrderTypes.SELL))))))));
    }

    private static int placeOrder(CommandContext<CommandSourceStack> context, OrderTypes orderType) {
        ServerPlayer player;

        try {
            player = context.getSource().getPlayerOrException();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        ItemInput itemInput = ItemArgument.getItem(context, "item");
        int quantity = context.getArgument("quantity", Integer.class);
        double price = context.getArgument("price", Double.class);

        DatabaseService<DbOrder> orderService = DatabaseServiceRegistry.getService(DbOrder.class);
        DatabaseService<DbPlayer> playerService = DatabaseServiceRegistry.getService(DbPlayer.class);
        DatabaseService<DbItem> itemService = DatabaseServiceRegistry.getService(DbItem.class);

        DbOrder order = new DbOrder();

        order.setPlayer(playerService.findById(player.getUUID()));
        order.setItem(itemService.findById(itemInput.getItem().toString()));
        order.setAmount(quantity);
        order.setPricePerItem(price);
        order.setOrderType(orderType);
        order.setOrderState(OrderState.OPEN);
        order.setFulfilledQuantity(0);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderService.saveOrUpdate(order);

        context.getSource().sendSystemMessage(
                Component.literal(
                        String.format("Successfully placed %s order for %dx %s at %.2f",
                                orderType.toString(), quantity, itemInput.getItem(), price
                        )
                )
        );

        return 1;
    }

}
