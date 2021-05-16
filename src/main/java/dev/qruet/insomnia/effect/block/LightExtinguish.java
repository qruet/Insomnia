package dev.qruet.insomnia.effect.block;

import dev.qruet.insomnia.misc.Tasky;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Torch;

public class LightExtinguish {

    public static void playEffect(Block block) {
        Material type = block.getType();
        if (type == Material.TORCH || type == Material.WALL_TORCH) {

            Directional directional = (Directional) block.getState().getData();
            BlockFace face = directional.getFacing();

            if (type == Material.TORCH)
                block.setType(Material.REDSTONE_TORCH);
            else {
                block.setType(Material.REDSTONE_WALL_TORCH);
                BlockState bs = block.getState();
                Directional data = (Directional) bs.getData().clone();
                data.setFacingDirection(face);
                bs.setData((MaterialData) data);
                bs.update();
            }

            Tasky.sync(t -> {
                if (!(block.getBlockData() instanceof Lightable)) {
                    t.cancel();
                    return;
                }

                Lightable lightable = (Lightable) block.getBlockData();
                lightable.setLit(false);
                block.setBlockData(lightable, false);

                Directional data = (Directional) block.getState().getData().clone();
                data.setFacingDirection(face);
                block.getState().setData((MaterialData) data);
                block.getState().update();
            }, 0L, 1L);

            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.65f, 1f);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(0, 0.8, 0), 10, 0, 0, 0, 0);

        }
    }

}
