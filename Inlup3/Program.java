// Peter Idestam-Almquist, 2020-02-04.
// [Replace this comment with your own name.]

// [Do necessary modifications of this file.]

package Inlup3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
// [You are welcome to add some import statements.]


public class Program {
	final static int NUM_WEBPAGES = 40;
	private static WebPage[] webPages = new WebPage[NUM_WEBPAGES];
	// [You are welcome to add some variables.]
	private static List<WebPage> listWebPages = new ArrayList<>(NUM_WEBPAGES);

	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void initialize() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {

			WebPage page = new WebPage(i, "http://www.site.se/page" + i + ".html");
			webPages[i] = page;
		}
	}

	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void presentResult() {
		for (WebPage webPage : listWebPages) {
			System.out.println(webPage);
		}
	}

	public static void main(String[] args) {
		// Initialize the list of webpages.
		initialize();

		// Start timing.
		long start = System.nanoTime();

		listWebPages = Stream.of(webPages).parallel().map(p -> {
			p.download();
			p.analyze();
			p.categorize();
			return p;
		}).collect(Collectors.toList());

		presentResult();

		// Stop timing.
		long stop = System.nanoTime();

		// Present the result.
		presentResult();

		// Present the execution time.
		System.out.println("Execution time (seconds): " + (stop - start) / 1.0E9);
	}
}
