// Sebastian Åkerlund, 2020-02-04.
// [Replace this comment with your own name.]

// [Do necessary modifications of this file.]

package Inlup3ProducerConsumerMyExecutor;

import java.util.concurrent.*;
// [You are welcome to add some import statements.]

public class Program3 {
	final static int NUM_WEBPAGES = 40;
	private static WebPage[] webPages = new WebPage[NUM_WEBPAGES];
	private static BlockingQueue<WebPage> queue = new ArrayBlockingQueue<>(NUM_WEBPAGES);
	private static BlockingQueue<WebPage> finalQueue = new ArrayBlockingQueue<>(NUM_WEBPAGES);

	// private static ExecutorService executor = ForkJoinPool.commonPool();
	private static MyExecutor myExecutor = new MyExecutor();
	// [You are welcome to add some variables.]

	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void initialize() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {

			WebPage page = new WebPage(i, "http://www.site.se/page" + i + ".html");
			webPages[i] = page;
			try {
				queue.put(page);
			} catch (InterruptedException e) {
				System.out.println(e);
				e.printStackTrace();
			}

		}
	}

	// [Do modify this sequential part of the program.]
	private static void downloadWebPages() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			myExecutor.execute(() -> {
				try {
					WebPage page = queue.take();
					page.download();
					analyzeWebPages(page);
				} catch (Exception e) {
					System.out.println(e);
				}
			});
		}
	}

	// [Do modify this sequential part of the program.]
	private static void analyzeWebPages(WebPage page) {
		page.analyze();
		categorizeWebPages(page);

	}

	// [Do modify this sequential part of the program.]
	private static void categorizeWebPages(WebPage page) {
		page.categorize();

		try {
			finalQueue.put(page);
		} catch (InterruptedException e) {
			System.out.println(e);
			e.printStackTrace();

		}
	}

	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void presentResult() {

		for (int i = 0; i < NUM_WEBPAGES; i++) {
			try {
				System.out.println(finalQueue.take());
			} catch (InterruptedException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		// Initialize the list of webpages.
		initialize();

		// Start timing.
		long start = System.nanoTime();

		// Do the work.
		downloadWebPages();

		myExecutor.shutDown();
		try {
			while (myExecutor.awaitShutDown()) {
			}
			System.out.println(myExecutor.awaitShutDown());
		} catch (Exception e) {
			System.out.println(e);
		}

		// Stop timing.

		long stop = System.nanoTime();

		// Present the result.
		presentResult();

		// Present the execution time.
		System.out.println("Execution time (seconds): " + (stop - start) / 1.0E9);
	}
}
