package net.punchtree.loquainteractable.clothing;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class SkinGrabber {

	public static void changeCape(Player player, String capeUrlPath) {
		URL capeUrl;
		try {
			capeUrl = new URL(capeUrlPath);
		} catch (MalformedURLException e) {
			player.sendMessage(text("The given skin URL string was not a valid url!").color(RED));
			return;
		}
		PlayerProfile playerProfile = player.getPlayerProfile();
		PlayerTextures textures = playerProfile.getTextures();
		textures.setCape(capeUrl);
		playerProfile.setTextures(textures);
		player.setPlayerProfile(playerProfile);
	}

	public static void changeSkin(Player player, String skinUrlPath) {
        URL skinUrl;
        try {
            skinUrl = new URL(skinUrlPath);
        } catch (MalformedURLException e) {
			player.sendMessage(text("The given skin URL string was not a valid url!").color(RED));
            return;
        }
        PlayerProfile playerProfile = player.getPlayerProfile();
		PlayerTextures textures = playerProfile.getTextures();
		textures.setSkin(skinUrl);
		playerProfile.setTextures(textures);
		player.setPlayerProfile(playerProfile);

//		CraftPlayer craftPlayer = (CraftPlayer) player;
//		ServerPlayer playerHandle = craftPlayer.getHandle();
//		GameProfile profile = playerHandle.getGameProfile();
//		ServerPlayerConnection connection = playerHandle.connection;
//
//		connection.send(new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId())));
//
//		Collection<Property> textures = profile.getProperties().get("textures");
//		System.out.println(textures);
//		profile.getProperties().removeAll("textures");
//		profile.getProperties().put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTczNjEzNTMxMDYxMiwKICAicHJvZmlsZUlkIiA6ICIyNjJkMzM5YjJkZTc0NmI1OGU5MWY4NDdlNWU2NjNhZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDeG9tIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RhZDMzYWI2NGM4MWI0M2JmNGJmNTI2MWRmZGQ4NGI4MTI5MTUyZGQ3YjEyMjYxZjVkYWNiNzczNjI5MjJhNWQiCiAgICB9CiAgfQp9"));
//
//		connection.send(ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(playerHandle, true));
	}
	
	private static Property getSkin() {
		Random r = new Random();
        int num = r.nextInt(4);
		num = 1;
        switch (num) {
        case 0: return new Property("textures", 
        		"eyJ0aW1lc3RhbXAiOjE0ODI1MjE4MTU4MTQsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ODQ1NzU2ODI5YjZiY2E1MTZiNWJmOTI1MWFlMzFjNzljZDZkZGJjM2M1N2YxMTkzNzBiMGNjZDhkNmY1YTEifX19",
        		"SKrjFrc/FJfl+xSG1/gsjcPMkEWhHkme527T3dTZIXtofzunAQ8VqZcPu9NJmwOCOlvqRL8T0STwSGaNZctHkh5+5xVKsS6w8/oe3rbdz+7g854C/p5Op7xCH0H/HhEB1HgVqQx5ZOWFuaGn2EaPIdx1v/9z//TG/SyCeNeYsiafJcETDFTeFLxl+L+RpyMxjBlTwPD1vQPrt2VZ8PLfpMlndbQUuquPhCeoYRNil9fqjYjNJHSnc9URjGfpBVZk/XCb+F6i3ljkbv9OChSgPUhli9ktckVnyFQmkImOq1eviyThh2pjg9qV7peaU9dxNyNazpf40B82X4Wztor62Y14DJXaGzUZcQN6oMbr/L8xjEwXRXuWBt9Szemi7ZZIpGXR00GSqeEW20+C5ZiwbsjzmuLxDw876FG/w76U2T9Z1joEf4ef+c13Byc+9KVXBX3ybhTerrXkW+oXbx2XBRZ5K5cOHmlcFT7rmR9iiXTcA2smB4eFxyHqgITLK/28aWSGyQFjZJMLxSr0EP0I0yrMil8tXggTZFq7kFeO32Ehr1unqGQet3kjCnk6z3vNUAnW6PmzmATwEaGEvOeIpBufq7EnylBK/UzpqoGTkn47zEXBHXY7m+BT2PwFSEF0D0X63h8SHx+G5hiSbcH6BRG1i1OLi7GofuadMHEPddI=");

			// {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/dad33ab64c81b43bf4bf5261dfdd84b8129152dd7b12261f5dacb77362922a5d"}}}
			// {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/5ae5d656c63527822c217f4267da0b7526e654254b41e4077ea677bc77863c5b"}}}
			//InRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYWQzM2FiNjRjODFiNDNiZjRiZjUyNjFkZmRkODRiODEyOTE1MmRkN2IxMjI2MWY1ZGFjYjc3MzYyOTIyYTVkIgogICAgfQogIH0=

//        case 1: return new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYwODI1NDU1NDk0OSwKICAicHJvZmlsZUlkIiA6ICIzM2ViZDMyYmIzMzk0YWQ5YWM2NzBjOTZjNTQ5YmE3ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJEYW5ub0JhbmFubm9YRCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYWQzM2FiNjRjODFiNDNiZjRiZjUyNjFkZmRkODRiODEyOTE1MmRkN2IxMjI2MWY1ZGFjYjc3MzYyOTIyYTVkIgogICAgfQogIH0KfQ==", "NWkfC4LjPAHCY5mvfjZ/QmtnvAJ8GOQnlg8U6k10si6e545TSNi5XpNIXS1cTuRdSoZ3mzWm4a00Cob6bOxb7G5o115UcX2rFzAT6nRNnAIkDkdYi3NxFcdip+pNDdOV+y+QR4J4VpZAwoMaGCsKyIMoT8CYeyexIO+ifYMpwZsEsLRlWaz+GNOes7Ql6+3aONsZuU9qTiRCRXyqBlDAWV3DyNToJDoudQ0Hl0L5KGbR4hUB913JHTlEuBfcRT3mHNmOrzMp9Zdr08q5SA2Fn1IZ9gMr+Dj1K+NEjkzyyRbZkjHR+CSzXajBvIztgGsdb0ZOPDckuPabVBvIQrHVj9Wq4A7u1z8H2cUroVOaH0ePIwi5YSCkeXW3sw8dyS4XSd7x6upjhAJaJ4hkrw8FFTeKYo96LbqROkO57A5fr8d/f+l0pfR4uSJtKtSMeeSkqPmJQsPWYx9tWeqSDEsr3HGekxO/hcuBrlSmyXjhmzdIO1zTUmWTxqaCfFxDx6JFan8QcbVCBxFm9Ro8KnTIOfcc+np82N4oFo67wZwZYOo27oV8NzKbwvRukdQGSNfT4xHwWMLR8wNsBUcOvoTco+lZDNL1Er1a4nKTigURPiokOVsnwvGR1B8nuE5xAil5F22644Yt8NQkzO06CcLFShw9FrLfbb3Znexp1VC2ztg=");
		case 1: return new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTczNjEzNTMxMDYxMiwKICAicHJvZmlsZUlkIiA6ICIyNjJkMzM5YjJkZTc0NmI1OGU5MWY4NDdlNWU2NjNhZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDeG9tIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RhZDMzYWI2NGM4MWI0M2JmNGJmNTI2MWRmZGQ4NGI4MTI5MTUyZGQ3YjEyMjYxZjVkYWNiNzczNjI5MjJhNWQiCiAgICB9CiAgfQp9");
        case 2: return new Property("textures", "eyJ0aW1lc3RhbXAiOjE1NzUzMzg1MjYxMjUsInByb2ZpbGVJZCI6IjJjY2I1NDYxYmNhMDQ4YzE4ZGU4MjIwYWMzMjQ4ZWY3IiwicHJvZmlsZU5hbWUiOiJSZWluZGVlciIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjNiYjBjMzQ5M2M4YzdkOWE3NDAxNTRmOTMyMjhmM2JjMjkzZTA1ZWZlZTkyMzQwMTk2YzI0MTMzMGUxYTJkMiJ9fX0=", "LB3VTkXygcg9/8RrWZLjtz0vf+KeDHGiJIyX47KDScVNSvvOOykFy18ncKioGV1yXdXyPlyIa5b9vu/Mfhn+21ykDaMtzbwfgQ4K0VSyXYyH7aLpHGB3izE26ghYULfqUKMEAH6uz3SGrFW9UbsOv6YdNI4tSxvBMetgsRU46vf2QcSRRgw+7ap3ZBNRNG8ca62u+a4HiC+5bMVgRwOM6YOSrw7MXtJjNlz3bsS0we8okUhCMq+my2mcl1XTF34UIogCYFk2R7LgDP2Fzkh/f1srt2TMNjMtFoKlmXy3/GVMW1sM3plKGVcfARMJYTpVI+Tsq4Wrze+yzK89dvm/x0N71MpkDgdZxTnTFu7EsFfml1nyM4RwE2h0LU2K6BNm8UBShb5plD8PnuBNVuQJzG0IvVoRJWKqRjGAJL4qgLxSjgVWjsAbXBEgqsx8b2MIFWHSXkiycIW7ZZBhP/LAtIAsLaxFbnidpPBR7rdBQ7ERyqrGTDc1DLBRUpGTtQb4Q4kF0y0wh5gZA05wvn85fh+qAu8HxEo0vE5I+Wa+v5b8Uo0Udsby0yqEZXxGQERNXEYK6BPmu1CKu5OZJJ8DOe0JWZs8Lu0NYWnvyQkSRlhLZqePW8B9nXCHyWFTaSRoO3FB5VisTpzayMBP4v86fLI3CUbyb0hEPdZe0TV77Zs=");
        default: return new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYwNjc5MDYyMTExOCwKICAicHJvZmlsZUlkIiA6ICJlZDUzZGQ4MTRmOWQ0YTNjYjRlYjY1MWRjYmE3N2U2NiIsCiAgInByb2ZpbGVOYW1lIiA6ICI0MTQxNDE0MWgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODlhNDFmMGRiYzcyOTYyZWFlNzExODcwZTkyYTc3YmNkMDFjZGNjNmE2ZDQwZTExZTk2NDYzOWMyMWIyYjVmOSIKICAgIH0KICB9Cn0=", "pegOuK3kV049MM5A0yKTwMxUu2f9jOB14FMkWkXs9GG7f7KsIssOj1eRt/39j7aW9U+M4pULYM2CG6rlpyih6Qofw26IHubG4P594ybhIKxtMbSEbOBIl7xkZI86hul/3odPvwkghgngsp3aY1jfgTNVjfROF9rOVGfugkWdXvYX/+CJs+pKkwdkKZpeUmSSwlB7p0MGvKCY8LuBms4/g0k8ipTOfEK4upOLHD/vBA01GHX+Epspd8tKvucIJLD+VANu12DmT0d2JZKrIyga4zuH4phFqnlg3JoNBurgJhAsDSXWuOnQRtcDqv8KOg/W8KRuvmAP4TZB1amAyPND72nmteF9YinN0KH1QkBKb6d4gvkP/0RKz562Z9+srZnlxBBrJ5V7JpT7kshQHYHP4qy6zE8De/vomJa23GB0T1lQai2a/p0uOCMk+oXu7aKuf0MrxaFptEN+q2VYJBz+9l6VRxRyJJZhFA0Yc0o2eD9zeqEQNTulbY17VP1bIU06Fxuo+S2AMjdV3a+IMaYo8FGx6I5H5rGXrL3Uqnu5IJFXAFN1eHW2RulOpvWO1SUvUYQUTCIestFB87quWCINUFk/DjUGysEZ+7E6q5kNM75SaTCFsMmDI0wFCCRPD6Hv6kcHNu+xx7Bezse75kUsdSiNVyNC4B3mVNMBuBFn/Nw=");
        }
	}
	
}
