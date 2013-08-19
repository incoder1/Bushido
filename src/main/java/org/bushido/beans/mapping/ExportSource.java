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

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportSource {
	/**
	 * Identify that source class and destination class methods signatures are
	 * compliant.<br>
	 * For example {@code SrcBean.getName} can be mapped to
	 * {@code DestBean.setName}. Whether {@code compliant} is set to
	 * {@code true} {@link Exporter} will automatically solve source and
	 * destinations without {@link ExportSetter} annotation.<br>
	 * 
	 * To add an exception for getter use {@link Transient} annotation
	 * 
	 * @return whether signatures are compliant
	 */
	boolean compliant() default false;
}
