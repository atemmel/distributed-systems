package se.miun.distsys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {

	public String name;
	public int id;

	public transient HashMap<String, User> users = new HashMap<String, User>();
	public transient ArrayList<Integer> sequenceList = new ArrayList<Integer>();

	public User(String name) {
		this.name = name;
	}
}
