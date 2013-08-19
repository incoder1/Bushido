package org.bushido.samples.beans;

import org.bushido.beans.Injector;
import org.bushido.beans.InjectorFactory;
import org.bushido.beans.mapping.ImportDestination;

public class ComplientSample {
	
	
	static class Source {
		private final String id;
		private final String name;
		public Source(String id, String name) {
			this.id = id;
			this.name = name;
		}
		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Destination [id=" + id + ", name=" + name + "]";
		}
	}
	
	@ImportDestination(compliant=true)
	static class Destination {
		private String id;
		private String name;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return "Destination [id=" + id + ", name=" + name + "]";
		}
		
	}
	
	public static void main(String[] args) {
		Source src = new Source("myID", "My Name");
		System.out.println(src);
		final InjectorFactory factory = InjectorFactory.newFactory();
		Injector<Source, Destination> importer = factory.createImporter(Source.class, Destination.class);
		
		Destination dest = new Destination();
		System.out.println("original dest: "+dest);
		importer.inject(src, dest);
		System.out.println("injected dest: "+dest);
		
	}
	
}
