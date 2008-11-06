package eu.orcas.kobra2.model.meta;

public class OwnedAttribute extends OwnedElement {
	
	public OwnedAttribute() {
		super();
	}
	
	public OwnedAttribute(
		String type, String id, String name, String visibility, 
		String aggregation, String association, Value upperValue,
		Value lowerValue) {

		super(type, id, name, visibility, aggregation, 
			association, upperValue, lowerValue);
	}
}