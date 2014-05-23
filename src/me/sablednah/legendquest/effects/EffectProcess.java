package me.sablednah.legendquest.effects;

import java.util.UUID;

import org.bukkit.Location;

public class EffectProcess {
	Effects		effect		= null;
	long		startTime	= 0;
	long		duration	= 0;
	OwnerType	owner		= null;
	UUID		uuid		= null;
	Location	location	= null;
	int			radius		= 3;

	public EffectProcess(Effects effect, long duration, OwnerType owner, UUID uuid) {
		this(effect, System.currentTimeMillis(), duration, owner, uuid, null, 3);
	}

	public EffectProcess(Effects effect, long duration, OwnerType owner, Location location) {
		this(effect, System.currentTimeMillis(), duration, owner, null, location, 3);
	}

	public EffectProcess(Effects effect, long duration, OwnerType owner, Location location, int radius) {
		this(effect, System.currentTimeMillis(), duration, owner, null, location, radius);
	}

	public EffectProcess(Effects effect, long start, long duration, OwnerType owner, Location location) {
		this(effect, start, duration, owner, null, location, 3);
	}

	public EffectProcess(Effects effect, long start, long duration, OwnerType owner, Location location, int radius) {
		this(effect, start, duration, owner, null, location, radius);
	}

	public EffectProcess(Effects effect, long start, long duration, OwnerType owner, UUID uuid) {
		this(effect, start, duration, owner, uuid, null, 3);
	}

	public EffectProcess(Effects effect, long start, long duration, OwnerType owner, UUID uuid, Location location, int radius) {
		this.effect = effect;
		this.startTime = start;
		this.owner = owner;
		this.uuid = uuid;
		this.location = location;
		this.duration = duration;
		this.radius = radius;
	}

	public void start() {
		this.startTime = System.currentTimeMillis();
	}
}
