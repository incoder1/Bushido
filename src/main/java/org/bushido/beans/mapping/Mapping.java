/*
 *  This library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.bushido.beans.mapping;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bushido.beans.convertor.CustomConverter;

/**
 * Contains cgi based reflection mapping
 * 
 * @author Victor Gubin
 * 
 */
public class Mapping {

	private final List<CallPair> mapping;

	private final Map<Method, CustomConverter<?, ?>> converterMapping;

	public Mapping(final List<CallPair> mapping,
			final Map<Method, CustomConverter<?, ?>> converterMapping) {
		this.mapping = mapping;
		this.converterMapping = converterMapping;
	}

	/**
	 * Returns unmodifiable cgi method mapping for injecting beans data
	 * 
	 * @return method mapping
	 */
	public Collection<CallPair> getMapping() {
		return Collections.unmodifiableList(this.mapping);
	}

	/**
	 * Return unmodifiable cgi converter mapping for customer conveting
	 * 
	 * @return
	 */
	public Map<Method, CustomConverter<?, ?>> getConverterMapping() {
		return Collections.unmodifiableMap(this.converterMapping);
	}

}
