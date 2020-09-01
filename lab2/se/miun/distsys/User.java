package se.miun.distsys;

import java.io.Serializable;

public class User implements Serializable {

	public String name;
	public int clock;

	public User(String name, int clock) {
		this.name = name;
		this.clock = clock;
	}
}
