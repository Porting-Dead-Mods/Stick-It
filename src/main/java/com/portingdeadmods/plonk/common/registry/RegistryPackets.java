package com.portingdeadmods.plonk.common.registry;

import com.portingdeadmods.plonk.Plonk;
import com.portingdeadmods.plonk.common.packet.PacketBase;
import com.portingdeadmods.plonk.common.packet.PacketPlaceItem;
import com.portingdeadmods.plonk.common.packet.PacketRotateTile;
import net.minecraftforge.network.simple.SimpleChannel;

public class RegistryPackets {
    private static final SimpleChannel CHANNEL = Plonk.CHANNEL;

    public static void register(int id, PacketBase packet) {
        CHANNEL.registerMessage(
                id, packet.getClass().asSubclass(PacketBase.class),
                PacketBase::write,
                packet::read,
                PacketBase::onMessage,
                packet.getNetworkDirection()
        );
    }

    @SuppressWarnings("UnusedAssignment")
    public static void init() {
        int id = 0;
        // Client -> Server
        register(id++, new PacketPlaceItem());
        register(id++, new PacketRotateTile());
    }
}
