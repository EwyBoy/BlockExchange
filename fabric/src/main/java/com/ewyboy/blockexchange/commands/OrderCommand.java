package com.ewyboy.blockexchange.commands;

import com.ewyboy.blockexchange.database.DatabaseService;
import com.ewyboy.blockexchange.database.ItemService;
import com.ewyboy.blockexchange.database.PlayerService;
import com.ewyboy.blockexchange.database.entities.DBOrder;
import com.ewyboy.blockexchange.database.enums.OrderState;
import com.ewyboy.blockexchange.database.enums.OrderTypes;
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

        DatabaseService databaseService = new DatabaseService();
        PlayerService playerService = new PlayerService();
        ItemService itemService = new ItemService();

        DBOrder order = new DBOrder();
        LocalDateTime now = LocalDateTime.now();

        order.setPlayer(playerService.getPlayerByUUID(player.getUUID()));
        order.setItem(itemService.getItem(itemInput.getItem().toString()));
        order.setAmount(quantity);
        order.setPricePerItem(price);
        order.setOrderType(orderType);
        order.setOrderState(OrderState.OPEN);
        order.setFulfilledQuantity(0);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        databaseService.saveOrUpdateEntity(order);

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
