import java.text.*;
import java.util.Random;

public class Evaluation {
	// get the common constants
	protected final int MAXPROC = 10;
    protected final int FRAMESIZE =(int)Math.pow(2, 8);
    protected final int VMSIZE = (int)Math.pow(2,24);
    protected final int MEMSIZE = (int)Math.pow(2,16);
    protected final int FRAMES =MEMSIZE/FRAMESIZE ;

	public VMManager vm;

	public static void main(String args[]) {

		Evaluation pm = new Evaluation();

	}

	public Evaluation() {
		int faults = 0, accesses = 0;
		int i, pid, address;
		ProcessModel pm[] = new ProcessModel[MAXPROC + 1];

		// create a new VMManager
		 //vm = new FIFOVMManager(); // change this line for evaluating other
		// implemenations
		 vm = new RandomVMManager(); // change this line for evaluating other
		// implemenations
		//vm = new LRUVMManager(); // change this line for evaluating other
		// implemenations
		//vm = new ClockVMManager(); // change this line for evaluating other
		// implemenations

		// initialise the processes with appropriate models
		// Note that these are _Models_ of real processes only
		for (i = 1; i <= MAXPROC; i++) {

			// with the index i we increase the requested memory,
			// the number of "hot spots" and the average
			// distance of an access to a hot spot.
			pm[i] = new LocalPM((i * VMSIZE) / 100, // memory required
					(i / 3) + 1, // no. of hot spots
					(i * FRAMESIZE / 4) + 1); // average distance to a hot spot
		}

		printFrames();

		int current = 1, curAccesses = 0; // now start the simulation
		for (i = 0; i < 10000000; i++) { // Yeah, we can do 10 million steps...

			curAccesses++; // count the no of accesses of the current process
			accesses++;

			// ask the VMManager whether next access is a page fault
			boolean fault = vm.access(current + 1,
					pm[current + 1].getNextAccess());
			if (fault) { // oops, a page fault
				faults++; // remember that
				curAccesses = 0; // reset of this counter
				current = (current + 1) % MAXPROC; // switch to the next
				// process, for simplicity
				// we use a round robin
				// scheduler
			} else if (curAccesses == 100) { // otherwise we check whether the
				// time slice (of 100 memory
				// accesses) is over
				curAccesses = 0;
				current = (current + 1) % MAXPROC;// switch to the next process,
				// round robin scheduler
			}
		}

		// Done, do some output
		printFrames();
		for (i = 1; i <= MAXPROC; i++)
			printProcess(i);

		System.out.println(" Accesses " + accesses + " Faults " + faults
				+ "   Percentage : " + (faults * 100.0) / accesses);

	}

	// print the current allocation of frames by pages of processes
	// uses a matrix representation and hex numbers
	public void printFrames() {
		int i, j, frame;
		DecimalFormat myOwner = new DecimalFormat("  00  ");

		System.out.print("       ");
		for (j = 0; j < 16; j++) {
			System.out.print(Integer.toHexString(0x100 | j).substring(1)
					.toUpperCase()
					+ "    ");
		}
		System.out.println();
		for (i = 0; i < (FRAMES / 16); i++) {
			System.out.print(Integer.toHexString(0x100 | i * 16).substring(1)
					.toUpperCase()
					+ " : ");
			// System.out.print((new Integer(i)).toHexString(i*16) + " : ");
			// print the owners
			for (j = 0; j < 16; j++) {
				frame = i * 16 + j;
				if (vm.owner(frame) == -1) {
					System.out.print("      ");
				} else
					System.out
					.print(myOwner.format(new Integer(vm.owner(frame))));
			}
			System.out.println();
			// print the pages
			System.out.print("     ");
			for (j = 0; j < 16; j++) {
				frame = i * 16 + j;
				if (vm.owner(frame) == -1) {
					System.out.print("   ");
				} else
					System.out.print(" "
							+ Integer.toHexString(0x10000 | vm.page(frame))
							.substring(1).toUpperCase() + " ");
			}
			System.out.println();
		}
	}

	// prints some statistics about a given process in the model
	public void printProcess(int PID) {
		DecimalFormat myformP = new DecimalFormat("00.00");
		DecimalFormat myformI = new DecimalFormat("00");
		DecimalFormat myformL = new DecimalFormat("0000000");
		int i, size = 0;
		System.out.print(" Resident Set of Process " + myformI.format(PID)
				+ " : ");
		for (i = 0; i < FRAMES; i++) {
			if (vm.owner(i) == PID) {
				size++;
			}
		}
		System.out.print(myformI.format(size) + " pages ("
				+ myformP.format((size * 100.0) / FRAMES) + "%)");

		System.out.print(" Accesses: " + myformL.format(vm.accesses(PID))
				+ " Faults: " + myformL.format(vm.faults(PID)));
		if (vm.accesses(PID) > 0)
			System.out.println(" ("
					+ myformP.format((vm.faults(PID) * 100.0)
							/ vm.accesses(PID)) + "%)");
		else
			System.out.println();

	}

}