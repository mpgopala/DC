import com.gsharma.dc.Process;

import java.util.ArrayList;

public class Main
{
	public static void main(String[] args)
	{
		ArrayList<Process> processes = new ArrayList<Process>();
		for(int i = 0; i < 10; i++)
		{
			processes.add(new Process(String.valueOf(i)));
		}

		for(Process process : processes)
		{
			System.out.println(process.toString());
		}
	}
}
