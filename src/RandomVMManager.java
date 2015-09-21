import java.util.Random;

public class RandomVMManager extends VMManager {
	private Random randomGenerator = new Random();
	
	public RandomVMManager() {
		super();
	}

	@Override
	public boolean access(int PID, int address) {
		accesses[PID]++;
		int pageIndex = address / FRAMESIZE;

		// check if the page exist in the memory
		for (int i = 0; i < FRAMESIZE; i++) {
			if (page[i] == pageIndex && owner[i] == PID) {
				return false;
			}
		}

		// if page is not found, register a fault and look for an empty frame
		faults[PID]++;
		for (int i = 0; i < FRAMES; i++) {
			if (owner[i] == -1) {
				page[i] = pageIndex;
				owner[i] = PID;
				return true;
			}
		}

		// if no empty frame is found, replace the random frame with a new page
		int random = randomGenerator.nextInt(FRAMES);	
		page[random] = pageIndex;
		owner[random] = PID;
		return true;
	}

}
