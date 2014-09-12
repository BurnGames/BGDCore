/*
 * COPYRIGHT AND PERMISSION NOTICE
 *
 * Copyright (c) 2014, PaulBGD, <paul@paulbgd.me>.
 *
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright
 * notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization of the copyright holder.
 */

package me.paulbgd.bgdcore.blocks.block.paster;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.Data;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.blocks.BlockUtils;
import me.paulbgd.bgdcore.blocks.block.Blocks;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Pastes blocks "async", or in chunks. Can be modified by being extended.
 */
public class AsyncPaster extends BukkitRunnable implements BlockPaster {

    private final List<QueueItem> queue = new ArrayList<>();

    protected long speed = 1l;
    protected int chunkSize = 1000;

    public AsyncPaster(long speed) {
        this.speed = speed;
        this.runTaskTimer(BGDCore.getPlugin(BGDCore.class), speed, speed);
    }

    public AsyncPaster() {
        this.runTaskTimer(BGDCore.getPlugin(BGDCore.class), speed, speed);
    }

    @Override
    public void handle(Blocks blocks, Block location, CommandSender paster, boolean air) {
        paster.sendMessage(ChatColor.AQUA + String.format("Proccessing %s blocks..", blocks.size()));
        List<me.paulbgd.bgdcore.blocks.block.Block> sorted = new ArrayList<>(blocks);
        Collections.sort(sorted, new Comparator<me.paulbgd.bgdcore.blocks.block.Block>() {
            @Override
            public int compare(me.paulbgd.bgdcore.blocks.block.Block o1, me.paulbgd.bgdcore.blocks.block.Block o2) {
                return Integer.compare(o1.getPosition().getRelativeY(), o2.getPosition().getRelativeY());
            }
        });
        List<List<me.paulbgd.bgdcore.blocks.block.Block>> toProcess = Lists.partition(sorted, chunkSize);
        for (int i = 0; i < toProcess.size(); i++) {
            queue.add(new QueueItem(toProcess.get(i), location, air, paster, i, toProcess.size() - 1));
        }
    }

    @Override
    public void run() {
        if (queue.isEmpty()) {
            return;
        }
        QueueItem item = queue.remove(0);
        BlockUtils.paste(item.getBlocks(), item.getLocation(), item.air);
        onPaste(item);
        if (item.getPosition() == item.totalSize && (!(item.getPaster() instanceof Player) || ((Player) item.getPaster()).isOnline())) {
            item.getPaster().sendMessage(ChatColor.GREEN + "Pasted!");
        }
    }

    protected void onPaste(QueueItem queueItem) {

    }

    public void finish() {
        this.cancel();
    }

    @Data
    public class QueueItem {
        private final List<me.paulbgd.bgdcore.blocks.block.Block> blocks;
        private final Block location;
        private final boolean air;
        private final CommandSender paster;
        private final double position;
        private final double totalSize;
    }

}
