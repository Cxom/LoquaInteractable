package net.punchtree.loquainteractable.npc.citizens.heist.edit

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.LocalSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.regions.Region
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.entity.Player


class WorldEditSessionTesting {

    fun doTest(player: Player) {
        val actor = BukkitAdapter.adapt(player)
        val weSessionManager = WorldEdit.getInstance().sessionManager
        val localSession: LocalSession = weSessionManager.getIfPresent(actor) ?: run {
            player.sendMessage(text("No world edit session found").color(RED))
            return
        }

        val selectionWorld = localSession.selectionWorld

        val region: Region
        try {
            if (selectionWorld == null) throw IncompleteRegionException()
            region = localSession.getSelection(selectionWorld)
        } catch (e: IncompleteRegionException) {
            player.sendMessage(text("No selection found").color(RED))
            return
        }

        // TODO We need to change this all to use either WorldGuard regions, or our own custom ones
        // probably worldguard

    }

}