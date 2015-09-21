public class ClockVMManager extends VMManager {
	private boolean[] used = null; // an array storing the used frame bits
	private int nextFrame = 0; // next frame pointer

	public ClockVMManager() {
		used = new boolean[FRAMESIZE];
	}

	@Override
	public boolean access(int PID, int address) {
		accesses[PID]++;
		int pageIndex = address / FRAMESIZE;

		// check if the page exist in the memory
		for (int frameIndex = 0; frameIndex < FRAMESIZE; frameIndex++)
			if (page[frameIndex] == pageIndex && owner[frameIndex] == PID) {
				used[frameIndex] = true;
				nextFrame = (frameIndex + 1) % FRAMESIZE;
				return false;
			}

		faults[PID]++;

		// if page is not found, start simple clock policy by
		// circulating the frames array looking for a frame whose
		// used bit is not set while reseting the used bit of each frame
		// which already had an used bit set.
		// keep doing this in a circular manner until an unset used bit is found
		// then load the page into this frame
		boolean isDone = false;
		while (!isDone) {
			if (used[nextFrame] == false) {
				page[nextFrame] = pageIndex;
				owner[nextFrame] = PID;
				used[nextFrame] = true;
				isDone = true;
			} else {
				used[nextFrame] = false;
				nextFrame = (nextFrame + 1) % FRAMESIZE;
			}
		}
		return true;
	}

}
