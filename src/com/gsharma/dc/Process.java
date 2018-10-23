package com.gsharma.dc;

public class Process
{
	public String name;

	public Process(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "Process: " + this.name;
	}
}
