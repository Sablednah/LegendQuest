package me.sablednah.legendquest.db;

import java.util.concurrent.ConcurrentHashMap;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Bukkit;

import lib.PatPeter.SQLibrary.*;

public class DataSync {
	public Main lq;
	public ConcurrentHashMap<String,PC> pendingWrites = new ConcurrentHashMap<String, PC>(Bukkit.getMaxPlayers());

	public MySQL mysql;
	String hostname;
	int port;
	String database;
	String username;
	String password;
	
	public DataSync(Main p) {
		this.lq = p;
		 
		this.mysql = new MySQL(lq.logger,"LQ_",hostname,port,database,username,password);
		
		
		// TODO add async task to push contents of pending writes to db
		// take copy of pendingwrites, empty it and process copy
	
		// TODO add async ticker task to keep connection alive
	}

	public synchronized void addWrite(String pName, PC pc){
		pendingWrites.put(pName, pc);
	}
	
	public synchronized PC getData(String pName){
		// TODO read PC record from database
		
		return null;
	}
	
	

	
}
