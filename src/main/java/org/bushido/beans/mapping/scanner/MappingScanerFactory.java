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
package org.bushido.beans.mapping.scanner;


/**
 * Creates {@link MappingScanner} instances
 * 
 * @author Victor Gubin
 * 
 */
public final class MappingScanerFactory {

	private MappingScanerFactory() {
	}

	/**
	 * Creates new import scanner
	 * 
	 * @return new instance of import scanner
	 */
	public static MappingScanner createImportScanner() {
		return new ImportMappingScanner();
	}

	/**
	 * Create new export scanner
	 * 
	 * @return new instance of export scanner
	 */
	public static MappingScanner createExportScanner() {
		return new ExportMappingScanner();
	}
}
