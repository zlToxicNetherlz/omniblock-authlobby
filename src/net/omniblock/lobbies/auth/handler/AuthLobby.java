/*
 * Omniblock Developers Team - Copyright (C) 2018 - All Rights Reserved
 *
 * 1. This software is not a free license software, you are not authorized to read, copy, modify, redistribute or
 * alter this file in any form without the respective authorization and consent of the Omniblock Developers Team.
 *
 * 2. If you have acquired this file violating the previous clause described in this Copyright Notice then you must
 * destroy this file from your hard disk or any other storage device.
 *
 * 3. As described in the clause number one, no third party are allowed to read, copy, modify, redistribute or
 * alter this file in any form without the respective authorization and consent of the Omniblock Developers Team.
 *
 * 4. Any concern about this Copyright Notice must be discussed at our support email: soporte.omniblock@gmail.com
 * -------------------------------------------------------------------------------------------------------------
 *
 * Equipo de Desarrollo de Omniblock - Copyright (C) 2018 - Todos los Derechos Reservados
 *
 * 1. Este software no es un software de libre uso, no está autorizado a leer, copiar, modificar, redistribuir
 * o alterar este archivo de ninguna manera sin la respectiva autorización y consentimiento del
 * Equipo de Desarrollo de Omniblock.
 *
 * 2. Si usted ha adquirido este archivo violando la clausula anterior descrita en esta Noticia de Copyright entonces
 * usted debe destruir este archivo de su unidad de disco duro o de cualquier otro dispositivo de almacenamiento.
 *
 * 3. Como se ha descrito en la cláusula número uno, ningun tercero está autorizado a leer, copiar, modificar,
 * redistribuir o alterar este archivo de ninguna manera sin la respectiva autorización y consentimiento del
 * Equipo de Desarrollo de Omniblock.
 *
 * 4. Cualquier duda acerca de esta Noticia de Copyright deberá ser discutido mediante nuestro correo de soporte:
 * soporte.omniblock@gmail.com
 */

package net.omniblock.lobbies.auth.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.omniblock.lobbies.OmniLobbies;
import net.omniblock.lobbies.api.LobbyUtility;
import net.omniblock.lobbies.api.object.LobbyBoard;
import net.omniblock.lobbies.api.object.LobbyScan;
import net.omniblock.lobbies.api.object.LobbySystem;
import net.omniblock.lobbies.api.object.LobbyWorld;
import net.omniblock.lobbies.api.type.CommonLobby;
import net.omniblock.lobbies.apps.attributes.type.AttributeType;
import net.omniblock.lobbies.utils.PlayerUtils;
import net.omniblock.lobbies.auth.handler.packets.base.AuthBase;
import net.omniblock.network.library.addons.resourceaddon.ResourceHandler;
import net.omniblock.network.library.addons.resourceaddon.type.ResourceType;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.network.systems.adapters.GameJOINAdapter;
import net.omniblock.packets.util.Lists;

@SuppressWarnings("deprecation")
public class AuthLobby extends CommonLobby {

	public static LobbyWorld lobbyWorld = LobbyUtility.getLobbyWorld("Auth");
	protected static List<Player> authPlayers = new ArrayList<Player>();
	
	protected AuthLobby instance;
	
	protected Map<String, List<Location>> scan;
	
	public AuthLobby() {
		super(lobbyWorld);
		return;
		
	}

	@Override
	public void onScanCompleted(Map<String, List<Location>> scan) {
		
		this.scan = scan;
		return;
		
	}

	@Override
	public void onLobbyUnloaded() {
		
	}

	@Override
	public void setup() {
		
		this.start();
		this.instance = this;
		
	}
	
	@Override
	public void onStartBeingExecute() {
		
		GameJOINAdapter.toggleJoinMSG(false);
		
		this.setSpawnPoint(new Location(this.getWorld().getBukkitWorld(), 23.5, 55, 3.5, 90, (float) 0));
		return;
		
	}

	@Override
	public void onStopBeingExecute() {
		
		return;
		
	}

	@Override
	public void giveItems(Player player) {
		
	}

	@Override
	public LobbyScan getScan() {
		return new LobbyScan() {
			
			@Override
			public Map<String, Material> getKeys() {
				return new HashMap<String, Material>();
			}

			@Override
			public String getScanName() {
				return "AUTHLOBBY_SCAN";
			}
			
		};
	}
	
	public Player getPlayer(String playername) {
		
		for(Player player : authPlayers)
			if(player.getName().equals(playername))
				return player;
		
		return null;
		
	}
	
	public Map<String, List<Location>> getLastScan() {
		return scan;
	}
	
	@Override
	public String getLobbyName() {
		return "AuthLobby";
	}

	@Override
	public Listener getEvents() {
		return new Listener() {
			
			@EventHandler
			public void onJoin(PlayerJoinEvent e){
				
				PlayerUtils.forcePlayerGameMode(e.getPlayer(), GameMode.ADVENTURE);
				PlayerUtils.clearPlayerInventory(e.getPlayer());
				PlayerUtils.clearPlayerPotions(e.getPlayer());
				
				e.getPlayer().setAllowFlight(false);
				e.getPlayer().setFlying(false);
				
				e.getPlayer().setCanPickupItems(false);
				e.getPlayer().setFireTicks(0);
				
				e.getPlayer().resetMaxHealth();
				e.getPlayer().resetTitle();
				e.getPlayer().resetPlayerWeather();
				e.getPlayer().resetPlayerTime();
				
				e.getPlayer().setExp(0);
				e.getPlayer().setLevel(0);
				
				if(!authPlayers.contains(e.getPlayer()))
					authPlayers.add(e.getPlayer());
				
				AuthBase.evaluate(e.getPlayer());
				
				for(Player player : Bukkit.getOnlinePlayers()) {
					
					player.hidePlayer(e.getPlayer());
					e.getPlayer().hidePlayer(player);
					
				}
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						
						if(e.getPlayer() != null)
							if(e.getPlayer().isOnline())
								e.getPlayer().kickPlayer(TextUtil.format("&6&l¡Se te agotó el tiempo de Acceso!"));
						
					}
					
				}.runTaskLater(OmniLobbies.getInstance(), 20 * 60);
				
				ResourceHandler.sendResourcePack(e.getPlayer(), ResourceType.OMNIBLOCK_DEFAULT);
				
				teleportPlayer(e.getPlayer());
				
			}
			
			@EventHandler
			public void onQuit(PlayerQuitEvent e) {
				
				if(authPlayers.contains(e.getPlayer()))
					authPlayers.remove(e.getPlayer());
				
			}
			
			@EventHandler(priority = EventPriority.LOW)
			public void onChat(PlayerChatEvent e) {
				
				e.setCancelled(true);
				return;
				
			}
			
		};
	}

	@Override
	public List<LobbySystem> getSystems() {
		return Lists.newArrayList();
	}

	@Override
	public List<BukkitTask> getTasks() {
		return Lists.newArrayList();
	}

	@Override
	public List<AttributeType> getAttributes() {
		return Arrays.asList(
				AttributeType.VOID_TELEPORTER,
				AttributeType.GAMEMODE_ADVENTURE,
				AttributeType.NO_DAMAGE,
				AttributeType.NO_HUNGER,
				AttributeType.NOT_COLLIDE
				);
	}

	@Override
	public LobbyBoard getBoard() {
		return null;
	}

}