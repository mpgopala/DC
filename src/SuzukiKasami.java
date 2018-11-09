import java.util.*;


/**
 * The main class that implements the SuzukiKasami algorithm.
 */
class SuzukiKasami
{

	/**
	 * Class that holds the request for a critical section from a node.
	 */
	static class CSRequest
	{
		/**
		 * The Node id.
		 */
		int nodeId;
		/**
		 * The time at which the critical section is requested.
		 */
		int time;

		/**
		 * Instantiates a new Cs request.
		 *
		 * @param id   the id of the process requesting the CS
		 * @param time the time at which the critical section is requested.
		 */
		CSRequest(int id, int time)
		{
			this.nodeId = id;
			this.time = time;
		}
	}

	/**
	 * Helper class to implement sorting on <code>CSRequest</code>.
	 */
	class SortById implements Comparator<CSRequest>
	{
		@Override
		public int compare(CSRequest o1, CSRequest o2)
		{
			return o1.time - o2.time;
		}
	}

	/**
	 * Number of processes in the system.
	 */
	private static int numProcesses = 5;
	/**
	 * The default duration a critical section held by any process.
	 */
	private static final int criticalSectionDuration = 3;
	private ArrayList<Node> nodes = new ArrayList<>();
	private int timeCounter = 0;

	/**
	 * The Cs request.
	 */
	private ArrayList<CSRequest> csRequest = null;

	private void setCsRequest(ArrayList<CSRequest> csRequest)
	{
		this.csRequest = csRequest;
		this.csRequest.sort(new SortById());
	}

	/**
	 * The type Message.
	 */
	class Message
	{
		/**
		 * The Node id of the sender process.
		 */
		int nodeId;

		/**
		 * The message to be broadcasted.
		 */
		RequestMessage msg;

		/**
		 * Instantiates a new Message object.
		 *
		 * @param id  The node id of the sender process.
		 * @param msg The actual message to send.
		 */
		Message(int id, RequestMessage msg)
		{
			nodeId = id;
			this.msg = msg;
		}
	}

	private ArrayList<Message> msgList = new ArrayList<>();

	private SuzukiKasami()
	{
		for(int i = 0; i < numProcesses; i++)
		{
			nodes.add(new Node(i));
		}

		nodes.get(0).setToken(new Token());
		nodes.get(0).RN[0] = 1;


		for(Node node : nodes)
		{
			System.out.println(node.toString());
		}
	}

	/**
	 * Gets the number of processes in the system
	 *
	 * @return the number of processes
	 */
	private static int getNumProcesses()
	{
		return numProcesses;
	}

	private static SuzukiKasami instance = new SuzukiKasami();

	/**
	 * Gets the instance of SuzukiKasami object.
	 *
	 * @return the instance of SuzukiKasami object.
	 */
	static SuzukiKasami getInstance()
	{
		return instance;
	}

	/**
	 * Send token to a particular process with id <code>nodeId</code>.
	 *
	 * @param token  token to send
	 * @param nodeId id of the destination process
	 */
	private void sendToken(Token token, int nodeId)
	{
		System.out.println("Sending token to " + nodes.get(nodeId));
		nodes.get(nodeId).receiveToken(token);
	}

	/**
	 * Broadcast message <code>msg</code> to all other processes in the system.
	 *
	 * @param nodeId The id of the source process that wants to broadcast
	 * @param msg    the msg to be broadcasted.
	 */
	private void broadcastMessage(int nodeId, RequestMessage msg)
	{
		for(int i = 0; i < numProcesses; i++)
		{
			if(i == nodeId)
				continue;
			msgList.add(new Message(i, msg));
		}
	}

	/**
	 * The main execution loop. This method runs a ticker with 1 second interval. <br>
	 * In the beginning, any requests for token from processes is sent as requested by the input.<br>
	 * Wait for one second.<br>
	 * Send all the messages to all the processes.<br>
	 * Run tick on each process so that the internal timer of the node gets updated and any process in CS would
	 * either stay in CS or pass the token around.
	 */
	private void execute()
	{
		while(true)
		{
			requestToken();

			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			timeCounter++;
			System.out.println("Current time: " + timeCounter);
			sendMessages();
			tick();
		}
	}

	private boolean requestToken()
	{
		if(csRequest.size() == 0)
			return false;
		while(csRequest.size() > 0 && csRequest.get(0).time == timeCounter)
		{
			nodes.get(csRequest.get(0).nodeId).requestToken();
			csRequest.remove(0);
		}

		return true;
	}

	private void tick()
	{
		for(Node node: nodes)
		{
			node.tick();
		}
	}

	private void sendMessages()
	{
		for(Message msg: msgList)
		{
//			System.out.println("Sending message (" + msg.msg + ") to node :" + nodes.get(msg.nodeId));
			nodes.get(msg.nodeId).receiveMessage(msg.msg);
		}

		msgList.clear();
	}

	private static CSRequest getCSRequest(int nodeId, int time)
	{
		return new CSRequest(nodeId, time);
	}

	/**
	 * The entry point of application.<br>
	 * Run the app as numProcesses numRequests {processId requestedTime}+<br>
	 * Where<br>
	 * <strong>numProcesses</strong> is the number of processes in the system<br>
	 * <strong>numRequests</strong> is the number of CS requests that need to be executed.<br>
	 * <strong>{processId requestedTime}</strong> is the actual CS request. The number of CS requests should be same as numRequests<br>
	 *      <strong>processId</strong> is the id of the process requesting for the CS<br>
	 *      <strong>requestedTime</strong> is the time at which the process requests for the CS<br>
	 *
	 * @param args the input arguments.
	 */
	public static void main(String[] args)
	{
		SuzukiKasami sk = SuzukiKasami.getInstance();
		ArrayList<CSRequest> csRequests = new ArrayList<>();

		if(args.length < 5)
		{
			System.out.println("Please supply required arguments");
			System.out.println("Arguments: numProcesses numRequests {processId requestedTime}+");
			System.out.println("Using default argument set");

			csRequests.add(getCSRequest(0, 0));
			csRequests.add(getCSRequest(1, 2));
			csRequests.add(getCSRequest(2, 7));
			csRequests.add(getCSRequest(3, 7));
			csRequests.add(getCSRequest(4, 8));
		}
		else
		{
			int numProcesses = Integer.parseInt(args[0]);
			System.out.println("numProcesses: " + numProcesses);
			int numRequests = Integer.parseInt(args[1]);

			for(int i = 0; i < numRequests; i++)
			{
				int processId = Integer.parseInt(args[2 + i * 2]);
				int requestedTime = Integer.parseInt(args[2 + i * 2 + 1]);
				System.out.println("csRequest: (" + processId + ", " + requestedTime + ")");
				csRequests.add(getCSRequest(processId, requestedTime));
			}
		}
		sk.setCsRequest(csRequests);
		sk.execute();
	}

	/**
	 * This class represents a process in a distributed system.
	 */
	class Node
	{
		private int id;

		/**
		 * The RN from SuzukiKasami algorithm.
		 */
		int[] RN;

		/**
		 * The Has token.
		 */
		private boolean hasToken = false;

		private boolean inCriticalSection = false;

		private Token token = null;

		private int criticalSectionTimer = 0;

		/**
		 * Sets token.
		 *
		 * @param token the token
		 */
		void setToken(Token token)
		{
			this.token = token;
			this.hasToken = true;
		}

		/**
		 * Instantiates a new Node.
		 *
		 * @param id the id of this process
		 */
		Node(int id)
		{
			RN = new int[SuzukiKasami.numProcesses];
			for(int i = 0; i < RN.length; i++)
				RN[i] = 0;
			this.id = id;
		}

		@Override
		public String toString()
		{
			return "Node: " + this.id;
		}

		/**
		 * Request token from other processes. This method executes the REQUEST(id, RN) from
		 * SuzukiKasami algorithm.
		 */
		void requestToken()
		{
			System.out.println("Requesting token from " + this);
			if(inCriticalSection)
				return;

			if(hasToken)
			{
				System.out.println(this + "has token. Entering CS");
				enterCriticalSection();
			}
			else
			{
				System.out.println("Broadcasting message from " + this);
				RN[id] = RN[id] + 1;
				RequestMessage msg = new RequestMessage();
				msg.setSourceNode(id);
				msg.setSerialNumber(RN[id]);
				broadcastMessage(msg);
			}
		}

		/**
		 * Receives the message from other processes.
		 *
		 * @param msg the message
		 */
		void receiveMessage(RequestMessage msg)
		{
	//		System.out.println("Message " + msg + " received in " + this);
			RN[msg.getSourceNode()] = Math.max(RN[msg.getSourceNode()], msg.getSerialNumber());
			if(hasToken && !inCriticalSection)
			{
				if(RN[msg.getSourceNode()] == token.LN[msg.getSourceNode()] + 1)
					sendToken(msg.getSourceNode());
			}
		}

		/**
		 * Timer function. When the global timer increments, this process will check if it is in CS.
		 * If it is in CS, checks if it is time to exit the CS. If it is, it would try to send the token
		 * to the next process in the queue as per SuzukiKasami algorithm.
		 */
		void tick()
		{
			if(inCriticalSection)
			{
				if(criticalSectionTimer > 0)
				{
					criticalSectionTimer--;
					if(criticalSectionTimer == 0)
					{
						inCriticalSection = false;
						exitCriticalSection();
					}
				}
			}
		}

		/**
		 * Receive token from some other process. After this, the process can
		 * enter into a CS.
		 *
		 * @param token the token
		 */
		void receiveToken(Token token)
		{
			this.token = token;
			hasToken = true;
			enterCriticalSection();
		}

		private void sendToken(int nodeId)
		{
			Token tempToken = token;
			hasToken = false;
			token = null;
			SuzukiKasami.getInstance().sendToken(tempToken, nodeId);
		}

		private void exitCriticalSection()
		{
			System.out.println(this + " is exiting CS");
			token.LN[id] = RN[id];

			for(int i = 0; i < SuzukiKasami.getNumProcesses(); i++)
			{
				if(i == id)
					continue;
				if(RN[i] == token.LN[i] + 1)
				{
					if(!token.Q.contains(i))
						token.Q.add(i);
				}
			}

			if(token.Q.size() > 0)
			{
				int nodeId = token.Q.remove();
				sendToken(nodeId);
			}
		}

		private void enterCriticalSection()
		{
			if(inCriticalSection)
				return;

			System.out.println(this + " is entering CS");
			inCriticalSection = true;
			criticalSectionTimer = SuzukiKasami.criticalSectionDuration;
		}

		private void broadcastMessage(RequestMessage msg)
		{
			SuzukiKasami.getInstance().broadcastMessage(id, msg);
		}
	}

	/**
	 * The type Request message from SuzukiKasami algorithm.
	 */
	class RequestMessage
	{
		private int sourceNode;
		private int serialNumber;

		/**
		 * Gets source node.
		 *
		 * @return the source node
		 */
		int getSourceNode()
		{
			return sourceNode;
		}

		/**
		 * Sets source node.
		 *
		 * @param sourceNode the source node
		 */
		void setSourceNode(int sourceNode)
		{
			this.sourceNode = sourceNode;
		}

		/**
		 * Gets serial number.
		 *
		 * @return the serial number
		 */
		int getSerialNumber()
		{
			return serialNumber;
		}

		/**
		 * Sets serial number.
		 *
		 * @param serialNumber the serial number
		 */
		void setSerialNumber(int serialNumber)
		{
			this.serialNumber = serialNumber;
		}

		@Override
		public String toString()
		{
			return "RequestMessage(" + sourceNode + ", " + serialNumber + ")";
		}
	}

	/**
	 * The type Token.
	 */
	class Token
	{
		/**
		 * The Q.
		 */
		Queue<Integer> Q = new LinkedList<>();
		/**
		 * The LN.
		 */
		int[] LN = new int[SuzukiKasami.numProcesses];
	}

}