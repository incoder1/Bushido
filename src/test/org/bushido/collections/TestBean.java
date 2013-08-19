/**
 * 
 */
package org.bushido.collections;

import java.util.Arrays;

/**
 * @author Victor Gubin
 * 
 */
class TestBean implements Cloneable {
	public boolean boolField;
	public byte byteField;
	public long longField;
	public int[] intArrayField;
	public String stringField;
	public float floatField;
	public Object[] objectArrayField;

	public TestBean() {
		this.boolField = true;
		this.byteField = (byte) 0xFA;
		this.longField = Long.MAX_VALUE;
		this.intArrayField = new int[] {0,1,2,3,4,5,6,7,8,9};
		this.stringField = "Lorem Ipsum";
		this.floatField = 0.40495f;
		this.objectArrayField = new Object[] { "Lorem Ipsum", 2, false, System.out, 0.3456d};
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	/**
	 * @return the boolField
	 */
	public boolean isBoolField() {
		return boolField;
	}

	/**
	 * @param boolField
	 *            the boolField to set
	 */
	public void setBoolField(boolean boolField) {
		this.boolField = boolField;
	}

	/**
	 * @return the byteField
	 */
	public byte getByteField() {
		return byteField;
	}

	/**
	 * @param byteField
	 *            the byteField to set
	 */
	public void setByteField(byte byteField) {
		this.byteField = byteField;
	}

	/**
	 * @return the longField
	 */
	public long getLongField() {
		return longField;
	}

	/**
	 * @param longField
	 *            the longField to set
	 */
	public void setLongField(long longField) {
		this.longField = longField;
	}

	/**
	 * @return the intArrayField
	 */
	public int[] getIntArrayField() {
		return intArrayField;
	}

	/**
	 * @param intArrayField
	 *            the intArrayField to set
	 */
	public void setIntArrayField(int[] intArrayField) {
		this.intArrayField = intArrayField;
	}

	/**
	 * @return the stringField
	 */
	public String getStringField() {
		return stringField;
	}

	/**
	 * @param stringField
	 *            the stringField to set
	 */
	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	/**
	 * @return the floatField
	 */
	public float getFloatField() {
		return floatField;
	}

	/**
	 * @param floatField
	 *            the floatField to set
	 */
	public void setFloatField(float floatField) {
		this.floatField = floatField;
	}

	/**
	 * @return the objectArrayField
	 */
	public Object[] getObjectArrayField() {
		return objectArrayField;
	}

	/**
	 * @param objectArrayField
	 *            the objectArrayField to set
	 */
	public void setObjectArrayField(Object[] objectArrayField) {
		this.objectArrayField = objectArrayField;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (boolField ? 1231 : 1237);
		result = prime * result + byteField;
		result = prime * result + Float.floatToIntBits(floatField);
		result = prime * result + Arrays.hashCode(intArrayField);
		result = prime * result + (int) (longField ^ (longField >>> 32));
		result = prime * result + Arrays.hashCode(objectArrayField);
		result = prime * result + ((stringField == null) ? 0 : stringField.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestBean other = (TestBean) obj;
		if (boolField != other.boolField)
			return false;
		if (byteField != other.byteField)
			return false;
		if (Float.floatToIntBits(floatField) != Float.floatToIntBits(other.floatField))
			return false;
		if (!Arrays.equals(intArrayField, other.intArrayField))
			return false;
		if (longField != other.longField)
			return false;
		if (!Arrays.equals(objectArrayField, other.objectArrayField))
			return false;
		if (stringField == null) {
			if (other.stringField != null)
				return false;
		} else if (!stringField.equals(other.stringField))
			return false;
		return true;
	}
}
