package me.sablednah.legendquest.loadout;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Loadout implements Cloneable {

	public String				name			= "";
	
	public ItemStack			repository		= null;
	public ItemStack			activator		= null;
	public ArrayList<String>	skills			= new ArrayList<String>();
	public String				activeskill		= null;

	public boolean				blockItemUsage	= false;

	public Loadout(String name, ItemStack repo, ItemStack activ, ArrayList<String> skilllist) {
		this.name = name;
		this.repository = repo;
		this.activator = activ;
		this.skills = skilllist;
		if (skills != null && skills.size() > 0) {
			this.activeskill = skills.get(0);
		}
	}

	public Loadout(String name, Material repo, Material activ, ArrayList<String> skilllist) {
		this(name, new ItemStack(repo), new ItemStack(activ), skilllist);
	}

	public void nextItem() {
		if (skills != null && skills.size() > 0) {
			// has skills
			if (skills.size() > 1 || activeskill == null) {
				// more than one skill
				// get current skill.
				int index = skills.indexOf(activeskill);
				index++;
				if (index >= skills.size()) {
					index = 0;
				}
				this.activeskill = skills.get(index);
			} else {
				// one skill - just pick it (or no skill pick first)
				this.activeskill = skills.get(0);
			}
		}
	}

	public String getActive() {
		return this.activeskill;
	}

	public boolean getBlockItemUsage() {
		return this.blockItemUsage;
	}
	public void setBlockItemUsage(boolean blockitem) {
		this.blockItemUsage = blockitem;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		@SuppressWarnings("unchecked")
		ArrayList<String> newArrayList = (ArrayList<String>) skills.clone();
		Loadout newload = new Loadout(name, repository.clone(), activator.clone(), newArrayList);

		newload.activeskill = activeskill;
		newload.blockItemUsage = blockItemUsage;

		return newload;

	}

}
