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
package org.bushido.beans;

import org.bushido.beans.mapping.Mapping;
import org.bushido.beans.mapping.scanner.MappingScanerFactory;
import org.bushido.beans.mapping.scanner.MappingScanner;

/**
 * Creates injector instances for importing/exporting data from bean to bean
 * 
 * @author Victor Gubin
 * 
 */
public final class InjectorFactory {

	/**
	 * Create new {@code InjectorFactory}
	 * 
	 * @return new importer factory
	 */
	public static InjectorFactory newFactory() {
		return new InjectorFactory();
	}

	private InjectorFactory() {
	}

	/**
	 * Creates {@link Injector} instance for import (inject) data into
	 * {@code src} bean to the {@code dest} bean
	 * 
	 * @param src
	 *            source bean class
	 * @param dest
	 *            destination bean class
	 * @return new importer
	 */
	public <Source, Desination> Injector<Source, Desination> createImporter(
			final Class<Source> src, final Class<Desination> dest) {
		final MappingScanner scanner = MappingScanerFactory
				.createImportScanner();
		final Mapping mapping = scanner.scan(src, dest);
		Compiler.compileClass(Importer.class);
		return new Importer<Source, Desination>(mapping);
	}

	/**
	 * Creates {@link Injector} instance for export (inject) data into
	 * {@code src} bean to the {@code dest} bean
	 * 
	 * @param src
	 *            source bean class
	 * @param dest
	 *            destination bean class
	 * @return new importer
	 */
	public <Source, Desination> Injector<Source, Desination> createExporter(
			final Class<Source> src, final Class<Desination> dest) {
		final MappingScanner scanner = MappingScanerFactory
				.createExportScanner();
		final Mapping mapping = scanner.scan(src, dest);
		Compiler.compileClass(Exporter.class);
		return new Exporter<Source, Desination>(mapping);
	}
}
