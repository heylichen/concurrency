package concurrency.ch3sharingobj;

public class VisibilityTest extends Thread {
	private boolean keepRunning = true;

	public boolean isKeepRunning() {
		return keepRunning;
	}

	public void setKeepRunning(boolean keepRunning) {
		this.keepRunning = keepRunning;
	}

	public void run() {
		System.out.println("thread running.");
		while (keepRunning) {
		}
		System.out.println("thread end.");
	}

	public static void main(String[] args) throws InterruptedException {
		VisibilityTest test = new VisibilityTest();
		test.start();
		Thread.sleep(1000);
		test.setKeepRunning(false);
		System.out.println("main thread signal keep running:" + test.isKeepRunning());

	}

}
