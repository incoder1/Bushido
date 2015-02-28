import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bushido.collections.range.Range;
import org.bushido.collections.range.RangeMap;
import org.bushido.collections.range.avl.AVLTreeRangeMap;


public class AvlTreeRangeMapDemo  {
	
	public static void main(String[] args) throws Exception {
		final RangeMap<Long, String> map = new AVLTreeRangeMap<Long,String>();
		//map.put(Range.create(-100, 0), "-100,0");
		// map.put(Range.create(0L, 100L), "0,100");
		map.insert(Range.create(-100L, 0L),"-100,0");
		map.insert(Range.create(200L, 300L), "200,300");
		map.insert(Range.create(100L,200L),"100,200");
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for(int i=0; i < 1000; i++) {
			pool.submit(new Runnable() {
				public void run() {
					map.find(-100L);
					map.find(-1L);
					map.find(0L);
					map.find(1L);
					map.insert(Range.create(0L, 100L), "0,100");
					System.out.println(map.find(77L).value());
					map.find(250L);
					System.out.println(map.remove(Range.create(0L, 100L)));
				}
			});
		}
		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.DAYS);
	}
	
}
