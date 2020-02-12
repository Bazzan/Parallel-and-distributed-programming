package Inlup3;

import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

// Sebastian Åkerlund, 2020-02-04.
// [Replace this comment with your own name.]

// [Do necessary modifications of this file.]

// package paradis.assignment3;

// [You are welcome to add some import statements.]

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.BlockingQueue.*;

public class Program {
	final static int NUM_WEBPAGES = 40;
	private static WebPage[] webPages = new WebPage[NUM_WEBPAGES];

	private static ExecutorService threadPool = ForkJoinPool.commonPool();
	private static BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(40);
	// [You are welcome to add some variables.]

	static class Task {

		public Task(WebPage webPage) {

		}
	}

	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void initialize() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			webPages[i] = new WebPage(i, "http://www.site.se/page" + i + ".html");
		}
	}

	// [Do modify this sequential part of the program.]
	private static void downloadWebPages() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			int j= i;

			Runnable runnable = () -> {
				
				webPages[j].download();
			};
			try {
				System.out.println("download");

				queue.put(runnable);
				// webPages[i].download();
				threadPool.submit(queue.take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			//  WebPage web = queue.take();
			//  Runnable runnable = web.download();
			//  threadPool.execute(web.download()) 

		
		}
	}

	// [Do modify this sequential part of the program.]
	private static void analyzeWebPages() {
		System.out.println("analyze");
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			int j= i;
			Runnable runnable = () -> {
				
				webPages[j].analyze();
			};
			try {
				queue.put(runnable);
				// webPages[i].download();
				threadPool.submit(queue.take());
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// [Do modify this sequential part of the program.]
	private static void categorizeWebPages() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {

			int j= i;
			Runnable runnable = () -> {
				
				webPages[j].categorize();
			};
			try {
				queue.put(runnable);
				// webPages[i].download();
				threadPool.submit(queue.take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void presentResult() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			System.out.println(webPages[i]);
			
		}
	}

	// private static void produce() {
	// 	for (int i = 0; i <= webPages.length; i++) {
	// 		try {
	// 			// Task task = new Task(webPages[i].download());
	// 			queue.put(-> webPage.download());
	// 		} catch (InterruptedException e) {
	// 			// TODO Auto-generated catch block
	// 			e.printStackTrace();
	// 		}
	// 	}
	// }


	// private static Runnable consume() {
	// 	for (WebPage webpage : webPages) {
	// 		threadPool.execute(queue.take()-> webPage.download());
	// 	}
	// }

	public static void main(String[] args) {
		// Initialize the list of webpages.
		initialize();

		// Start timing.
		long start = System.nanoTime();




		// for (WebPage webpage : webPages) {

		
		// 	// Runnable task = () -> webpage.download();
		// 	// try {
		// 	// 	queue.put(task);
		// 	// 	threadPool.execute(queue.take());

		// 	// } catch (InterruptedException e) {
		// 	// 	// TODO Auto-generated catch block
		// 	// 	e.printStackTrace();
		// 	// }
			

		// }
		// System.out.println(queue.size());

		// Do the work.
		downloadWebPages();
		analyzeWebPages();
		categorizeWebPages();

		// Stop timing.
		long stop = System.nanoTime();

		// Present the result.
		presentResult();

		// Present the execution time.
		System.out.println("Execution time (seconds): " + (stop - start) / 1.0E9);

	}

}
