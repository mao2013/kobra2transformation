package eu.orcas.kobra2.model.meta;

public abstract class OwnedElement {
	
	public OwnedElement() {}
	
	public OwnedElement(
		String type, String id, String name, String visibility, 
		String aggregation, String association,	Value upperValue,
		Value lowerValue) {

		this.type = type;
		this.id = id;
		this.name = name;
		this.visibility = visibility;
		this.aggregation = aggregation;
		this.association = association;
		this.upperValue = upperValue;
		this.lowerValue = lowerValue;
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
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	public String getAggregation() {
		return aggregation;
	}
	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
	}
	public String getAssociation() {
		return association;
	}
	public void setAssociation(String association) {
		this.association = association;
	}
	public Value getUpperValue() {
		return upperValue;
	}
	public void setUpperValue(Value upperValue) {
		this.upperValue = upperValue;
	}
	public Value getLowerValue() {
		return lowerValue;
	}
	public void setLowerValue(Value lowerValue) {
		this.lowerValue = lowerValue;
	}

	private String type;
	private String id;
	private String name;
	private String visibility;
	private String aggregation;
	private String association;
	private Value upperValue;
	private Value lowerValue;
}