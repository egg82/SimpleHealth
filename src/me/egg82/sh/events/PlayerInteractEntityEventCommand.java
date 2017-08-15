package me.egg82.sh.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import me.egg82.sh.enums.PermissionsType;
import me.egg82.sh.services.InteractRegistry;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;
import ninja.egg82.plugin.utils.CommandUtil;
import ninja.egg82.plugin.utils.TaskUtil;

public class PlayerInteractEntityEventCommand extends EventCommand<PlayerInteractEntityEvent> {
	//vars
	private IRegistry<UUID> interactRegistry = ServiceLocator.getService(InteractRegistry.class);
	
	//constructor
	public PlayerInteractEntityEventCommand(PlayerInteractEntityEvent event) {
		super(event);
	}
	
	//public
	
	//private
	@SuppressWarnings({ "deprecation", "unchecked" })
	protected void onExecute(long elapsedMilliseconds) {
		if (!(event.getRightClicked() instanceof LivingEntity)) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (!CommandUtil.hasPermission(player, PermissionsType.USE)) {
			return;
		}
		
		LivingEntity entity = (LivingEntity) event.getRightClicked();
		
		UUID uuid = player.getUniqueId();
		UUID entityUuid = entity.getUniqueId();
		
		Set<UUID> bars = interactRegistry.getRegister(uuid, Set.class);
		
		if (bars != null && bars.contains(entityUuid)) {
			return;
		}
		
		if (bars == null) {
			bars = new HashSet<UUID>();
		}
		bars.add(entityUuid);
		
		BossBar bar = Bukkit.createBossBar(entity.getName(), BarColor.WHITE, BarStyle.SOLID);
		bar.setProgress(entity.getHealth() / entity.getMaxHealth());
		bar.addPlayer(player);
		bar.setVisible(true);
		
		interactRegistry.setRegister(uuid, bars);
		
		TaskUtil.runSync(new Runnable() {
			public void run() {
				bar.setVisible(false);
				bar.removePlayer(player);
				
				Set<UUID> bars = interactRegistry.getRegister(uuid, Set.class);
				if (bars != null) {
					bars.remove(entityUuid);
					if (bars.size() == 0) {
						interactRegistry.removeRegister(uuid);
					}
				}
			}
		}, 90L);
	}
}
