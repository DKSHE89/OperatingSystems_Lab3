import java.util.Random;
public abstract class ProcessModel 
{
    protected final int VMSIZE = (int)Math.pow(2,24);
    protected int size;  //size of required memory. 
    static public Random myRnd = new Random();
    public ProcessModel(int size) 
    {
	this.size = size;
 	myRnd.setSeed(4711); 
   } ;
    // here one can implement a new model for the behaviour of a process. 
    public abstract int getNextAccess();
}