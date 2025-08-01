package net.punchtree.loquainteractable.listeners

import net.punchtree.loquainteractable.MessageOfTheDay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerPingListener : Listener {

    @EventHandler
    fun onServerPing(event: ServerListPingEvent) {
        event.motd(MessageOfTheDay.getFullServerListingText())
    }

}