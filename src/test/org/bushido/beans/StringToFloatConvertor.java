package org.bushido.beans;

import org.bushido.beans.convertor.CustomConverter;

/**
 * Simply converts string to float
 * 
 * @author Victor Gubin
 * 
 */
public class StringToFloatConvertor implements CustomConverter<String, Float> {
	@Override
	public Float convert(String src) {
		return Float.parseFloat(src);
	}
}
