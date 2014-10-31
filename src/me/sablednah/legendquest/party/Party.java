package me.sablednah.legendquest.party;

import java.util.UUID;

public class Party {

	public String partyName;
	public UUID player;
	public boolean approved;
	public boolean owner;	
	
	public Party(String partyName, UUID player, boolean approved,  boolean owner) {
		this.partyName = partyName;
		this.player = player;
		this.approved = approved;
		this.owner = owner;
	}
	
}
