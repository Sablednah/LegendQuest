package me.sablednah.legendquest.config;

import java.lang.reflect.Field;

import me.sablednah.legendquest.Main;

public class MainConfig extends Config {
	public String one;
	public int two;
	
	public MainConfig(Main p) {
		super(p, "config.yml");
		for (Field f : this.getClass().getDeclaredFields()) {
			try {
				f.set(this, getConfigItem(f.getName(),f.get(this)));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}

	private Object getConfigItem(String name, Object object) {
		// TODO Auto-generated method stub
		System.out.print(name + " is :" + object);
		return null;
	}


	
	
}
