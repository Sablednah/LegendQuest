package me.sablednah.legendquest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class LevelItems {

	public HashMap<String, LevelBonus>	levelUp	= new HashMap<String, LevelBonus>();

	public void addEntry(int level, String key, Object value) {
		if (levelUp.get(key)!= null) {
			LevelBonus lu = levelUp.get(key);
			lu.setLevel(level, value);
			levelUp.put(key,lu);
		} else {
			levelUp.put(key, new LevelBonus(level,value));
		}
	}

	public LevelBonus getKey(String key) {
		LevelBonus x = levelUp.get(key);
		return x;
	}

	public double getTotal(String key, int level) {
		LevelBonus keydata = levelUp.get(key);
//		System.out.print("getting total for : "+ key + " ("+level+")");

		double total = 0.0D;
		if (keydata!=null) {
//			System.out.print("found key data");
			total = keydata.getUptoLevel(level);
		}
		return total;
	}

	public List<Object> getList(String key, int level) {
		LevelBonus keydata = levelUp.get(key);
		List<Object> list = new ArrayList<Object>();
		if (keydata!=null) {
			list = keydata.getUptoLevelList(level);
		}
		return list; 
	}
	
	public class LevelBonus {
		private HashMap<Integer, Object> bonusList = new HashMap<Integer, Object>(); 

		public LevelBonus(int l, Object v) {
			bonusList.put(l,v);
		}
		/**
		 * @return the value for level
		 */
		public Object getLevel(int level) {
			return bonusList.get(level);
		}
		/**
		 * @param key the key to set
		 */
		public void setLevel(int l, Object v) {
			bonusList.put(l,v);
		}
		/**
		 * @return the  total as double upto level
		 */
		public double getUptoLevel(int level) {
			double total=0.0D;
//			System.out.print("adding up: "+level);

			for (Entry<Integer, Object> ent : bonusList.entrySet()) {
//				System.out.print("checking: "+ ent.getKey() + " < "+level+" ?");
				if (ent.getKey()<=level) {
//					System.out.print("adding: "+ ent.getValue().toString() + " to "+total+" ?");
					if (ent.getValue() instanceof Double) {
						total += (Double)ent.getValue();
//						System.out.print("newtotal (d): = "+total+" ?");
					} else {
						total += (Integer)ent.getValue();
//						System.out.print("newtotal (i): = "+total+" ?");
					}
				}
			}
			return total;
		}
		/**
		 * @return the  total as list upto level
		 */
		public List<Object> getUptoLevelList(int level) {
			List<Object> list = new ArrayList<Object>();
			for (Entry<Integer, Object> ent : bonusList.entrySet()) {
				if (ent.getKey()<=level) {
					list.add(ent.getValue());
				}
			}
			return list;
		}
	}

}
