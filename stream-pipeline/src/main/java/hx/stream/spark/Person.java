package hx.stream.spark;

import java.io.Serializable;

// make it ban-compliant: Serializable, public no-argument constructor, getter and setters
public class Person implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id, age; String name;
	
	public Person() { }
	
	public Person(int id, String name, int age) {
		this.id = id;
		this.age = age;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", age=" + age + ", name=" + name + "]";
	}
	
}

