package lab.multithreads.bq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @version 1.01 2012-01-26
 * @author Cay Horstmann
 */
public class FutureTest {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out
				.print("Enter base directory (e.g.  /usr/local/jdk5.0/src): ");
		// String directory = in.nextLine();
		String directory = "E:\\Lab_WS\\ywpt\\jsyw\\src";

		System.out.println("Enter keyword (e.g. volatile): ");
		// String keyword = in.nextLine();
		String keyword = "xml";

		MatchCounter counter = new MatchCounter(new File(directory), keyword);
		FutureTask<Integer> task = new FutureTask<>(counter);
		Thread t = new Thread(task);
		t.start();
		try {
			System.out.println(task.get() + " matching files.");
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		}
	}
}

/**
 * This task counts the files in a directory and its
subdirectories that contain a given keyword.
 */
class MatchCounter implements Callable<Integer> {
	private File directory;
	private String keyword;
	private int count;

	/**
	* Constructs a MatchCounter.
	* @param directory the directory in which to start the search
	* @param keyword the keyword to look for
	*/
	public MatchCounter(File directory, String keyword) {
		this.directory = directory;
		this.keyword = keyword;
	}

	public Integer call() {
		count = 0;
		try {
			File[] files = directory.listFiles();
			List<Future<Integer>> results = new ArrayList<>();

			for (File file : files)
				if (file.isDirectory()) {
					MatchCounter counter = new MatchCounter(file, keyword);
					FutureTask<Integer> task = new FutureTask<>(counter);
					results.add(task);
					Thread t = new Thread(task);
					t.start();
				} else {
					if (search(file))
						count++;
				}

			for (Future<Integer> result : results)
				try {
					count += result.get();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
		} catch (InterruptedException e) {
		}
		return count;
	}

	/**
	* Searches a file for a given keyword.
	* @param file the file to search
	* @return true if the keyword is contained in the file
	*/
	public boolean search(File file) {
		try {
			try (Scanner in = new Scanner(file)) {
				boolean found = false;
				while (!found && in.hasNextLine()) {
					String line = in.nextLine();
					if (line.contains(keyword))
						found = true;
				}
				return found;
			}
		} catch (IOException e) {
			return false;
		}
	}
}