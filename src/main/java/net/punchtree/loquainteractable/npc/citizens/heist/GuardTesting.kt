package net.punchtree.loquainteractable.npc.citizens.heist

import org.bukkit.Location
import org.bukkit.entity.Player

object GuardTesting {

    private var guard: Guard? = null

    fun createGuard(spawnLocation: Location) {
        guard?.destroy()
        guard = Guard(spawnLocation)
    }

    fun makeGuardFireGun(player: Player) {
        guard?.fireGun() ?: {
            player.sendMessage("There is no guard! create one first!");
        }
    }

    fun onDisable() {
        guard?.destroy()
    }

    //
    //do you think you're not letting yourself be reactionary to the bad news around you? to be upset when it would be natural to be upset? do you think you'd be able to? do you think you will?
    //

    // I had a moment tonight where a friend who's going through some shit reached out and shared a poem with me,
    // and it reminded me of that night in February in grand rapids,
    // and I just wanted to say thank you. I know you've sometimes complemented me on the grace with which I
    // make space for the path of others' needs when in company, something that's stuck with me and become a
    // bit of a badge of honor for me. But I also want to say that the patient, pensive inquisition that you
    // bring reiterate
}