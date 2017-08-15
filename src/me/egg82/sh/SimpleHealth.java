package me.egg82.sh;

import me.egg82.sh.enums.PermissionsType;
import ninja.egg82.plugin.BasePlugin;
import ninja.egg82.plugin.utils.SpigotReflectUtil;

public class SimpleHealth extends BasePlugin {
	//vars
	
	//constructor
	public SimpleHealth() {
		super();
	}
	
	//public
	public void onLoad() {
		super.onLoad();
		
		SpigotReflectUtil.addServicesFromPackage("me.egg82.sh.services");
	}
	
	public void onEnable() {
		super.onEnable();
		
		SpigotReflectUtil.addEventsFromPackage("me.egg82.sh.events");
		SpigotReflectUtil.addPermissionsFromClass(PermissionsType.class);
	}
	public void onDisable() {
		super.onDisable();
		
		SpigotReflectUtil.clearAll();
	}
	
	//private
	
}
