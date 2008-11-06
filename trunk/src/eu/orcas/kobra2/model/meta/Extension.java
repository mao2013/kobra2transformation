package eu.orcas.kobra2.model.meta;

public class Extension {
	
	public Extension() {}
	
	public Extension(String extender){
		this.extender = extender;
	}

	public String getExtender() {
		return extender;
	}

	public void setExtender(String extender) {
		this.extender = extender;
	}

	private String extender;
}
