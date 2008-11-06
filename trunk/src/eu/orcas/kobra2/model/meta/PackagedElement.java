package eu.orcas.kobra2.model.meta;

public class PackagedElement {
	
	public PackagedElement() {}
	
	public PackagedElement(
		String type, String id, String name, String memberEnd) {

		this.type = type;
		this.id = id;
		this.name = name;
		this.memberEnd = memberEnd;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMemberEnd() {
		return memberEnd;
	}
	public void setMemberEnd(String memberEnd) {
		this.memberEnd = memberEnd;
	}

	private String type;
	private String id;
	private String name;
	private String memberEnd;
}
