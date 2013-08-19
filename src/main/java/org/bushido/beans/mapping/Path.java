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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks setter for obtaining source data from
 * {@link #value()} method invocation sequence 
 * 
 * @author Victor Gubin
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Path {
	/**
	 * Specify path for obtaining source data. Path is a dot separated method
	 * names string.<br>
	 * 
	 * The sample for simple getter path looks like @{code getHost}.<br>
	 * 
	 * For a complex nesting type hierarchy path looks like @{code
	 * getService.getHostPort.getHost}
	 * 
	 * @return path to obtain source data
	 */
	String value();
}
