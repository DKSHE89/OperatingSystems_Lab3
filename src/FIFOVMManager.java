public class FIFOVMManager extends VMManager {
	private int oldestFrame = 0;
	private int[] referencedFrames;

	public FIFOVMManager() {
		super();
		referencedFrames = new int[FRAMES];
	}

	@Override
	public boolean access(int PID, int address) {
		accesses[PID]++;
		boolean fault = false;
		boolean found = false;
		int i = 0;
		int pageIndex = address /FRAMESIZE;

		if (referencedFrames.length == 0) {
			referencedFrames[0] = 1;
			page[0] = address;
			owner[0] = PID;
			oldestFrame = 0;
		} else {

			// check if the page exist in the memory
			for (i = 0; i < referencedFrames.length && !found; i++) {
				if ((owner[i] == PID) && (page[i] == pageIndex))
					found = true;
			}

			// if the page is in memory, change the pointer to next accessed
			if (found && (i < FRAMES)) {
				updateOldestFrame();

			// if page is not found, register a fault and look for an empty frame
			} else {
				if (i < FRAMES) {
					referencedFrames[i] = 1;
					owner[i] = PID;
					page[i] = pageIndex;
					updateOldestFrame();

			// if no empty frame is found, replace the oldest frame with a new page
				} else {
					fault = true;
					faults[PID]++; 
					owner[oldestFrame] = PID;
					page[oldestFrame] = pageIndex;
					updateOldestFrame();
				}
			}
		}
		return fault;
	}

	private void updateOldestFrame() {
		if (oldestFrame < referencedFrames.length - 1) {
			oldestFrame++;
		} else {
			oldestFrame = 0;
		}
	}
}