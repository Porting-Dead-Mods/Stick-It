package com.portingdeadmods.plonk.common.networking;

import com.portingdeadmods.plonk.Plonk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Plonk.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkEvents {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Plonk.MOD_ID);
        registrar.playToServer(
                PlaceItemPayload.TYPE,
                PlaceItemPayload.STREAM_CODEC,
                PlaceItemPayload::action
        );
        registrar.playToServer(
                RotateTilePayload.TYPE,
                RotateTilePayload.STREAM_CODEC,
                RotateTilePayload::action
        );
    }
}