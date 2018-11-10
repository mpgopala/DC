import java.util.*;

/**
 * The main public class that implements the ChandyMisraHaas algorithm.
 */
public class ChandyMisraHaas
{
	private static int numProcesses = 5;
	private boolean[][] wfg;

	/**
	 * Initializes the object.
	 *
	 * @param wfgGraph     the wait-for-graph
	 */
	private void init(boolean[][] wfgGraph)
	{
		this.wfg = wfgGraph;
	}

	/**
	 * A method that would take the initiator process and check the probe messages to see if the system is deadlocked.
	 *
	 * @param initProcess the initiator process
	 * @return Whether the system is deadlocked or not.
	 */
	private boolean isDeadlocked(int initProcess)
	{
		boolean isDeadlocked = false;
		System.out.println("Initiating probe...");
		List<Message> messageList=new ArrayList<>();
		int count=0;
		for(int i=0;i<numProcesses;i++)
		{
			for(int j=0;j<numProcesses;j++)
			{
				if(wfg[i][j])
				{
					Message m=new Message(initProcess,i,j);
					messageList.add(m);
					count++;
				}
			}
		}
		System.out.println(messageList);

		for(int i=0;i<count;i++)
		{
			for(int j=0;j<count;j++)
			{
				if(messageList.get(i).init==messageList.get(j).to)
					isDeadlocked = true;
			}
		}
		return isDeadlocked;
	}

	/**
	 * Method to print the input parameters to this program.
	 */
	private void printInput()
	{
		System.out.println("Usage:");
		System.out.println("NumProcesses WaitForGraph InitiatorProcess");
		System.out.println();
		System.out.println("NumProcesses: number of processes in the system");
		System.out.println("WaitForGraph space separated wait for graph. This should be a " +
				"square matrix where 1 denotes a process in row depends on a process in column. " +
				"0 otherwise.");
		System.out.println("InitiatorProcess: The id of the process that initiates the deadlock detection");
	}

	/**
	 * The entry point of application. <br>
	 * <br>
	 * Usage: <br>
	 * NumProcesses WaitForGraph InitiatorProcess <br>
	 * <br>
	 * NumProcesses: number of processes in the system <br>
	 * WaitForGraph space separated wait for graph. This should be a square matrix where 1 denotes a process in row depends on a process in column. 0 otherwise. <br>
	 * InitiatorProcess: The id of the process that initiates the deadlock detection <br>
	 * <br>
	 * @param args the input arguments.<br>
	 */
	public static void main(String[] args)
	{
		ChandyMisraHaas obj = new ChandyMisraHaas();
		int iter = 0;
		System.out.println("In ChandyMisraHaas Algorithm");

		if(args.length == 0)
		{
			obj.printInput();
			System.exit(0);
		}

		try
		{
			ChandyMisraHaas.numProcesses = Integer.parseInt(args[iter++]);
		}
		catch(Exception e)
		{
			obj.printInput();
			System.exit(0);
		}
		if(args.length < (ChandyMisraHaas.numProcesses * ChandyMisraHaas.numProcesses + 1))
		{
			obj.printInput();
			System.exit(0);
		}

		System.out.println("Number of processes is " + numProcesses);

		boolean[][] wfg = new boolean[numProcesses][numProcesses];

		System.out.println("WFG is:");

		for(int i = 0; i < numProcesses * numProcesses; i++)
		{
			boolean dependent = false;

			try
			{
				dependent = Integer.parseInt(args[iter++]) > 0;
			}
			catch (Exception e) {}

			wfg[i / numProcesses][i%numProcesses] = dependent;
		}
		for(int i = 0; i < numProcesses; i++)
		{
			for(int j = 0; j < numProcesses; j++)
			{
				System.out.print(wfg[i][j]?"1 ":"0 ");
			}
			System.out.println();
		}

		obj.init(wfg);

		int initProcess = Integer.parseInt(args[iter]);
		System.out.println("Initiator process is " + initProcess);

		if(obj.isDeadlocked(initProcess))
			System.out.println("The Deadlock has been detected...");
		else
			System.out.println("No Deadlock has been detected...");

		System.exit(0);
	}

	/**
	 * The probe message in ChandyMisraHaas algorithm.
	 */
	class Message
	{
		/**
		 * The Initiator.
		 */
		int init;
		/**
		 * The From.
		 */
		int from;
		/**
		 * The To.
		 */
		int to;

		/**
		 * Instantiates a new Message.
		 *
		 * @param i initiator
		 * @param j from
		 * @param k to
		 */
		Message(int i, int j, int k)
		{
			init = i;
			from = j;
			to = k;
		}

		@Override
		public String toString()
		{
			return "("+init+","+from+","+to+")";
		}
	}
}