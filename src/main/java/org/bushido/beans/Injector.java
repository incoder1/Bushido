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

/**
 * Implementor inject (import of export) data from {@code Src} bean to the
 * {@code Dest} bean
 * 
 * @author Victor_Gubin
 * 
 * @param <Src>
 *            source bean type
 * @param <Dest>
 *            destination bean type
 */
public interface Injector<Src, Dest> {

	/**
	 * Inject (import) data obtained from {@code src} bean instance to the
	 * {@code dest} bean instance
	 * 
	 * @param src
	 *            source bean
	 * @param dest
	 *            destination bean
	 */
	public abstract void inject(Src src, Dest dest);

}