import java.util.*;

class CSRequest
{
	public int nodeId;
	public int time;
}

class SortById implements Comparator<CSRequest>
{
	@Override
	public int compare(CSRequest o1, CSRequest o2)
	{
		return o1.time - o2.time;
	}
}

public class SuzukiKasami
{
	public static int numProcesses = 5;
	public static final int criticalSectionDuration = 3;
	private ArrayList<Node> nodes = new ArrayList<>();
	private int timeCounter = 0;

	ArrayList<CSRequest> csRequest = null;

	public void setCsRequest(ArrayList<CSRequest> csRequest)
	{
		this.csRequest = csRequest;
		this.csRequest.sort(new SortById());
	}

	class Message
	{
		public int nodeId;
		public RequestMessage msg;

		public Message(int i, RequestMessage msg)
		{
			nodeId = i;
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

		nodes.get(0).token = new Token();
		nodes.get(0).hasToken = true;
		nodes.get(0).RN[0] = 1;


		for(Node node : nodes)
		{
			System.out.println(node.toString());
		}
	}

	public static int getNumProcesses()
	{
		return numProcesses;
	}

	private static SuzukiKasami instance = new SuzukiKasami();

	public static SuzukiKasami getInstance()
	{
		return instance;
	}

	public void sendToken(Token token, int nodeId)
	{
		System.out.println("Sending token to " + nodes.get(nodeId));
		nodes.get(nodeId).receiveToken(token);
	}

	public void broadcastMessage(int nodeId, RequestMessage msg)
	{
		for(int i = 0; i < numProcesses; i++)
		{
			if(i == nodeId)
				continue;
			msgList.add(new Message(i, msg));
		}
	}

	public void execute()
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
		CSRequest request = new CSRequest();
		request.nodeId = nodeId;
		request.time = time;
		return request;
	}

	public static void main(String[] args)
	{
		SuzukiKasami sk = SuzukiKasami.getInstance();
		ArrayList<CSRequest> csRequests = new ArrayList<>();

		if(args.length < 5)
		{
			System.out.println("Please supply required arguments");
			System.out.println("Arguments: <numProcesses> <numRequests> {<processId> <requestedTime>}+");
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
}

class Node
{
	private int id;
	public int[] RN;
	public boolean hasToken = false;
	private boolean inCriticalSection = false;
	Token token = null;
	int criticalSectionTimer = 0;

	public Node(int id)
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

	public void requestToken()
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

	public void receiveMessage(RequestMessage msg)
	{
//		System.out.println("Message " + msg + " received in " + this);
		RN[msg.getSourceNode()] = Math.max(RN[msg.getSourceNode()], msg.getSerialNumber());
		if(hasToken && !inCriticalSection)
		{
			if(RN[msg.getSourceNode()] == token.LN[msg.getSourceNode()] + 1)
				sendToken(msg.getSourceNode());
		}
	}

	public void tick()
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

	public void receiveToken(Token token)
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

class RequestMessage
{
	private int sourceNode;
	private int serialNumber;

	public int getSourceNode()
	{
		return sourceNode;
	}

	public void setSourceNode(int sourceNode)
	{
		this.sourceNode = sourceNode;
	}

	public int getSerialNumber()
	{
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	@Override
	public String toString()
	{
		return "RequestMessage(" + sourceNode + ", " + serialNumber + ")";
	}
}

class Token
{
	public Queue<Integer> Q = new LinkedList<>();
	public int[] LN = new int[SuzukiKasami.numProcesses];
}