package org.bushido.samples.beans;

import org.bushido.beans.mapping.ExportFlow;
import org.bushido.beans.mapping.ExportSource;
import org.bushido.beans.mapping.ImportDestination;
import org.bushido.beans.mapping.Path;
import org.bushido.beans.mapping.Setter;

@ExportSource
@ImportDestination
public class InjectBean {
	private boolean boolField;
	private byte byteField;
	private short shortField;
	private int intField;
	private long longField;
	private char charField;
	private float floatField;
	private double doubleField;
	private String stringField;

	@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setBoolField"))
	public boolean isBoolField() {
		return boolField;
	}

	@Path("getLevel1.getLevel2.isBoolField")
	public void setBoolField(boolean boolField) {
		this.boolField = boolField;
	}

	@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setByteField"))
	public byte getByteField() {
		return byteField;
	}

	@Path("getLevel1.getLevel2.getByteField")
	public void setByteField(byte byteField) {
		this.byteField = byteField;
	}

	@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setShortField"))
	public short getShortField() {
		return shortField;
	}

	@Path("getLevel1.getLevel2.getShortField")
	public void setShortField(short shortField) {
		this.shortField = shortField;
	}

	@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setIntField"))
	public int getIntField() {
		return intField;
	}

	@Path("getLevel1.getLevel2.getIntField")
	public void setIntField(int intField) {
		this.intField = intField;
	}

	@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setLongField"))
	public long getLongField() {
		return longField;
	}

	@Path("getLevel1.getLevel2.getLongField")
	public void setLongField(long longField) {
		this.longField = longField;
	}

	@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setCharField"))
	public char getCharField() {
		return charField;
	}

	@Path("getLevel1.getLevel2.getCharField")
	public void setCharField(char charField) {
		this.charField = charField;
	}

	@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setFloatField"))
	public float getFloatField() {
		return floatField;
	}

	@Path("getLevel1.getLevel2.getFloatField")
	public void setFloatField(float floatField) {
		this.floatField = floatField;
	}

	@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setDoubleField"))
	public double getDoubleField() {
		return doubleField;
	}

	@Path("getLevel1.getLevel2.getDoubleField")
	public void setDoubleField(double doubleField) {
		this.doubleField = doubleField;
	}

	@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setStringField"))
	public String getStringField() {
		return stringField;
	}

	@Path("getLevel1.getLevel2.getStringField")
	public void setStringField(String stringField) {
		this.stringField = stringField;
	}
}
