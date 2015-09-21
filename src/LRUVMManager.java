public class LRUVMManager extends VMManager {
	private long[] referencedFrames = null;
	private long referenced = 0;

	public LRUVMManager() {
		super();
		referencedFrames = new long[FRAMESIZE];
	}

	@Override
	public boolean access(int PID, int address) {
		referenced++;
		accesses[PID]++;

		int pageIndex = address / FRAMESIZE;

		// check if the page exist in the memory
		for (int i = 0; i < FRAMESIZE; i++)
			if (page[i] == pageIndex && owner[i] == PID) {
				referencedFrames[i] = referenced;
				return false;
			}

		// if page is not found, register a fault
		faults[PID]++;

		// find oldest referenced
		long oldestReferenced = referenced;
		for (int i = 0; i < FRAMESIZE; i++) {
			oldestReferenced = Math.min(referencedFrames[i], oldestReferenced);
		}

		// replace the least recently used frame with a new page
		for (int i = 0; i < FRAMESIZE; i++) {
			if (referencedFrames[i] == oldestReferenced) {
				page[i] = pageIndex;
				owner[i] = PID;
				referencedFrames[i] = referenced;
				break;
			}
		}
		return true;
	}

}
