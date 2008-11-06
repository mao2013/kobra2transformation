package eu.orcas.kobra2.model.meta;

public class Value {
	
	public Value(String type, String id) {
		this.type = type;
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String type;
	private String id;
}
