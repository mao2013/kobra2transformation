package eu.orcas.kobra2.model.meta;

public class Model {
	
	public Model(
		String version, String schemaLocation, String id, String name) {
		
		this.version = version;
		this.schemaLocation = schemaLocation;
		this.id = id;
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
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

	private String version;
	private String schemaLocation;
	private String id;
	private String name;
}
