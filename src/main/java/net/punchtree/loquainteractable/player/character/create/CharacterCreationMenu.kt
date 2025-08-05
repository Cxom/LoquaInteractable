package net.punchtree.loquainteractable.player.character.create

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.event.entity.CreatureSpawnEvent

class CharacterCreationMenu private constructor(val location: Location) {

    /* GTA character creation goes:
     *  - Sex (not relevant)
     *  - Heritage (skin color)
     *  - Features/Appearance (skin texture, eyes, hair, face)
     *  -
     */


    /*
     *  Menu layout:
     *   - Appearance & Features
     *    - Skin Color
     *    - Eyes
     *     - Eye Type
     *     - Eye Color(s)
     *    - Hair
     *     - Hair Type
     *     - Hair Color
     *   - Apparel
     *   - Name
     */

    private val textDisplay: TextDisplay

    init {
        val menuOptions = listOf("Appearance", "Apparel", "Name")

        textDisplay = location.world.spawnEntity(location, EntityType.TEXT_DISPLAY, CreatureSpawnEvent.SpawnReason.CUSTOM) {
            it as TextDisplay

            val text = menuOptions.map { text(it + "\n") }.reduce(TextComponent::append)

            it.text(text)
            it.backgroundColor = Color.fromARGB(0)
        } as TextDisplay
    }

    internal fun remove() {
        textDisplay.remove()
    }

    companion object {
        fun createAt(menuLocation: Location): CharacterCreationMenu {
            return CharacterCreationMenu(menuLocation)
        }
    }

}
