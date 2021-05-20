package dev.qruet.insomnia.nms.packet;

import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.misc.ReflectionUtils;
import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import dev.qruet.insomnia.server.Server;
import dev.qruet.insomnia.server.world.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Source modified and pulled from {@see <a href="https://www.spigotmc.org/threads/cancel-packet.429156/">LoxleyShadow's Spigot Thread</a>}
 *
 * @author qruet
 * @author LoxleyShadow
 */
public class PacketHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        injectPlayer(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    private void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

    public void disable() {
        Bukkit.getOnlinePlayers().forEach(this::removePlayer);
    }

    private void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                if (packet instanceof PacketPlayOutSpawnEntityLiving) {
                    PacketPlayOutSpawnEntityLiving packetSpawn = (PacketPlayOutSpawnEntityLiving) packet;
                    //PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
                    //packetSpawn.b(serializer);
                    //UUID uid = serializer.k();

                    Field b = ReflectionUtils.getField(PacketPlayOutSpawnEntityLiving.class, "b");
                    UUID uid = null;
                    try {
                        uid = (UUID) b.get(packetSpawn);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    EntityInsomniaPhantom phantom = null;
                    for (World world : Server.getWorlds().collect(Collectors.toList())) {
                        EntityInsomniaPhantom p = world.getPhantom(uid);
                        if (p != null) {
                            phantom = p;
                            break;
                        }
                    }

                    if (phantom != null && phantom.isGhost() && !phantom.getTarget().getUniqueId().equals(player.getUniqueId()))
                        return;

                }
                super.write(channelHandlerContext, packet, channelPromise);
            }


        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);

    }

}
