package org.dnal.api.bean;

public class PersonGroup {
	private String name;
	private Person boss;
	
	public PersonGroup() {
	}
	public PersonGroup(String name, Person boss) {
		this.name = name;
		this.boss = boss;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Person getBoss() {
		return boss;
	}
	public void setBoss(Person boss) {
		this.boss = boss;
	}

}
