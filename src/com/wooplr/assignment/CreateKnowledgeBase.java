package com.wooplr.assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class Entity {

	String name;
	String type;

	public Entity(String name) {
		this.name = name;
	}
	
	public Entity(String name, String type){
		
		this.name = name;
		this.type = type;
	}

	public String toString() {

		return name + " " + type;
	}
	
	public boolean equals(Entity e){
		
		return (this.name.equals(e.name));
	}
}

class Tuple {

	Entity E1;
	Entity E2;

	public Tuple(Entity E1, Entity E2) {
		this.E1 = E1;
		this.E2 = E2;
	}

	public Tuple(String name1, String name2) {
		this.E1 = new Entity(name1);
		this.E2 = new Entity(name2);
	}
	
	public String toString() {

		return E1.toString() + " " + E2.toString();
	}
}

public class CreateKnowledgeBase {
	/*
	 * This class creates a knowledge base depending on your chosen data
	 * structure, and use the data structure for querying the parsed queries.
	 * The knowledge base should be created or refreshed only when new
	 * information is added.
	 */

	// formattedLine<Entity> KB = new ArrayformattedLine<>();
	Map<String, List<Tuple>> DBase = new HashMap<>();

	void insert(String line) {

		line = line.replaceAll(" ","");
		line = line.substring(1, line.length() - 1);
		String[] formattedLine = line.split(",");
		for (int i = 0; i < formattedLine.length; i++) {
			formattedLine[i] = formattedLine[i].substring(1,
					formattedLine[i].length() - 1);
		}

		Tuple tuple = null; 
		
		if(formattedLine[1].contains("has")){
		
			tuple = new Tuple(new Entity(formattedLine[0], "Person1"), new Entity(formattedLine[2], "Person2"));
		} else if(formattedLine[1].contains("works")){
			
			tuple = new Tuple(new Entity(formattedLine[0], "Person1"), new Entity(formattedLine[2], "University"));
		} else if(formattedLine[1].contains("age")){
			
			tuple = new Tuple(new Entity(formattedLine[0], "Person1"), new Entity(formattedLine[2], "Age"));
		} else if(formattedLine[1].contains("born")){
			
			tuple = new Tuple(new Entity(formattedLine[0], "Person1"), new Entity(formattedLine[2], "City"));
		}
		
		

		List<Tuple> list = DBase.get(formattedLine[1]);
		if (list == null)
			list = new LinkedList<Tuple>();
		list.add(tuple);
		DBase.put(formattedLine[1], list);
	}

	void fromFile() {

		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(
					"C:/Users/Pravesh/Downloads/Wooplr_Assignment/input.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				// System.out.println(sCurrentLine);
				insert(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		CreateKnowledgeBase knowledgeBase = new CreateKnowledgeBase();
		knowledgeBase.fromFile();
		
		Set<Entry<String, List<Tuple>>> entries = knowledgeBase.DBase.entrySet();

		for (Entry<String, List<Tuple>> entry : entries) {
			System.out.print(entry.getKey());
			Iterator<Tuple> itr = entry.getValue().iterator();
			while (itr.hasNext()) {
				System.out.print(" " + itr.next());
			}
			System.out.println();
		}
	}
}
