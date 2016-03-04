
/**
 * Scheduling Algorithms (HW2) COP 4600 - Operating Systems
 * @author Danish Waheed
 * Due date: Saturday March 5th, 6:00 AM
 */
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class scheduler {
	static ArrayList<process> processeslist = new ArrayList<process>();

	static class process {
		public process() {
		}

		public String name;
		public int arrival, burstTime, wait, turnaround, parallelBurstTime;

	}

	/**
	 * The main method will call on each of the RR, FCFS or SJF algorithms based
	 * on the input it receives from the user in the form of an input file.
	 * 
	 * @param monkey
	 *            (this is obviously a joke, of course you're not a monkey!)
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unused")
	public static void main(String[] monkey) throws FileNotFoundException, UnsupportedEncodingException {
		// The name of the file that is to be tested.
		File file = new File("set4_process.in");

		int numOfProcesses = 0, quantum = -1, runLength = -1;
		String type = "";

		Scanner in = null;

		try {
			in = new Scanner(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Declaring string variables to use while parsing the data
		String newLine, splitLine[];

		// And now we parse the data
		while (in.hasNextLine()) {
			newLine = in.nextLine();
			// Put the file into an array list where we remove all of the white
			// space.
			splitLine = newLine.split(" ");

			for (int i = 0; i < splitLine.length; i++) {
				// # means the rest of the line is a comment so we can ignore
				// and move to the next line.
				if (splitLine[i].equals("#")) {
					break;
				}
				// 'processcount' is a key word and the following int is the
				// number of
				// numOfProcesses
				if (splitLine[i].equals("processcount")) {
					i++;
					numOfProcesses = Integer.parseInt(splitLine[i]);
				}
				// 'runfor' is a key word and the following int is the runLength
				// variable
				if (splitLine[i].equals("runfor")) {
					i++;
					runLength = Integer.parseInt(splitLine[i]);
				}
				// 'quantum' is a key word and the following int is the quantum
				// for round
				// robin
				if (splitLine[i].equals("quantum")) {
					i++;
					quantum = Integer.parseInt(splitLine[i]);
				}
				// 'use' is a keyword and the following string is the type
				if (splitLine[i].equals("use")) {
					i++;
					type = splitLine[i];
				}
				// Getting the numOfProcesses and the information from each
				if (splitLine[i].equals("process") && splitLine[i + 1].equals("name")) {
					for (int x = 0; x < numOfProcesses; x++) {
						if (splitLine[i].equals("end"))
							break;
						process proc = new process();
						proc.name = splitLine[i + 2];
						proc.arrival = Integer.parseInt(splitLine[i + 4]);
						proc.burstTime = Integer.parseInt(splitLine[i + 6]);
						proc.parallelBurstTime = proc.burstTime;
						processeslist.add(proc);
						i += 6;
						break;
					}
				}
			}
		}
		in.close();
		// Now that we have all the data we can call the methods accordingly
		if (type.equals("fcfs"))
			firstComeFirstServe(numOfProcesses, runLength);
		if (type.equals("rr"))
			roundRobin(numOfProcesses, runLength, quantum);
		if (type.equals("sjf"))
			shortestJobFirst(numOfProcesses, runLength);

	}

	/**
	 * This is the implementation of the Round Robin algorithm
	 * 
	 * @param numProcesses
	 * @param runLength
	 * @param quantum
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void roundRobin(int numProcesses, int runLength, int quantum)
			throws FileNotFoundException, UnsupportedEncodingException {
		boolean cpuBusy;
		int time = 0;
		int quant = 0;
		int holder = 0;
		ArrayList<process> roundRobinQueue = new ArrayList<process>();
		PrintWriter output = new PrintWriter("process.out", "UTF-8");

		output.println(numProcesses + " Processes");
		output.println("Using Round Robin");
		output.println("Quantum: " + quantum);
		output.println();
		process running = null;

		while (time <= runLength) {
			cpuBusy = false;
			for (int i = 0; i < processeslist.size(); i++)
				if (processeslist.get(i).arrival == time) {
					output.println("Time: " + time + " Process " + processeslist.get(i).name + " has arrived.");
					roundRobinQueue.add(processeslist.get(i));
					cpuBusy = true;
				}

			if (running != null && running.burstTime == 0) {
				output.println("Time: " + time + " Process " + running.name + " has finished.");
				running.turnaround = time - running.arrival;
				running.wait = running.turnaround - running.parallelBurstTime;
				running = null;
				holder++;
			}
			// Get the next process to run that is not complete need to go down
			// the list
			if (running != null && running.burstTime != 0 && quant == quantum) {
				int i = 0;
				while (i < roundRobinQueue.size()) {
					if (roundRobinQueue.size() == 1) {
						output.println("Time: " + time + " Process " + running.name + " has been selected. (Burst "
								+ running.burstTime + ")");
						break;
					}
					holder++;
					if (roundRobinQueue.size() <= holder)
						holder = 0;

					if (roundRobinQueue.get(holder).burstTime == 0)
						holder++;

					else if (roundRobinQueue.get(holder).burstTime != 0) {
						running.turnaround += quantum;
						running = roundRobinQueue.get(holder);
						output.println("Time: " + time + " Process " + running.name + " has been selected. (Burst "
								+ running.burstTime + ")");
						break;
					}
					i++;
				}
			}
			if (running == null && roundRobinQueue.size() != 0) {
				int i = 0;
				while (i < roundRobinQueue.size()) {
					holder++;
					if (roundRobinQueue.size() <= holder)
						holder = 0;

					if (roundRobinQueue.get(holder).burstTime == 0)
						holder++;

					else if (roundRobinQueue.get(holder).burstTime != 0) {
						running = roundRobinQueue.get(holder);
						output.println("Time: " + time + " Process " + running.name + " has been selected. (Burst "
								+ running.burstTime + ")");
						break;
					}
					i++;
				}
			}
			if (running != null)
				running.burstTime--;
			else if (!cpuBusy && time != runLength)
				output.println("Time: " + time + " IDLE");
			if (quant == quantum)
				quant = 1;
			else
				quant++;
			time++;
		}

		output.println("Finished at Time: " + (time - 1));
		output.println();
		for (int i = 0; i < numProcesses; i++) {
			output.println(processeslist.get(i).name + " wait " + processeslist.get(i).wait + " turnaround "
					+ processeslist.get(i).turnaround);
		}
		output.close();
		return;
	}

	/**
	 * This is the implementation of the First Come First Serve Algorithm
	 * 
	 * @param numProcesses
	 * @param runLength
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void firstComeFirstServe(int numProcesses, int runLength)
			throws FileNotFoundException, UnsupportedEncodingException {
		boolean cpuBusy;
		int time = 0;

		PrintWriter output = new PrintWriter("process.out", "UTF-8");

		output.println(numProcesses + " Processes");
		output.println("Using First Come First Serve");
		output.println();

		process running = null;
		Queue<process> readyQueue = new LinkedList<process>();
		do {
			cpuBusy = false;
			for (int i = 0; i < processeslist.size(); i++)
				if (processeslist.get(i).arrival == time) {
					output.println("Time: " + time + " Process " + processeslist.get(i).name + " has arrived.");
					readyQueue.add(processeslist.get(i));
					cpuBusy = true;
				}
			if (running != null && running.burstTime == 0) {
				output.println("Time: " + time + " Process " + running.name + " has finished.");
				running.turnaround = time - running.arrival;
				running = null;
			}
			if (running == null && !readyQueue.isEmpty()) {
				running = readyQueue.remove();
				output.println("Time: " + time + " Process " + running.name + " has been selected. (Burst "
						+ running.burstTime + ")");
				running.wait = time - running.arrival;
				cpuBusy = true;
			}
			if (running != null)
				running.burstTime--;
			else if (!cpuBusy && time != runLength)
				output.println("Time: " + time + " IDLE");
			// Time goes up by 1 to simulate running processor.
			time++;
		} while (time <= runLength);
		// Print the wait and turnaround time for the first come first serve
		// algorithm
		output.println("Finished at time: " + (time - 1));
		output.println();
		for (int i = 0; i < numProcesses; i++) {
			output.println(processeslist.get(i).name + " wait " + processeslist.get(i).wait + " turnaround "
					+ processeslist.get(i).turnaround);
		}
		output.close();
		return;
	}

	/**
	 * This is the implementation of the Shortest Job First algorithm
	 * 
	 * @param numProcesses
	 * @param runLength
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void shortestJobFirst(int numProcesses, int runLength)
			throws FileNotFoundException, UnsupportedEncodingException {
		boolean cpuBusy;
		int time = 0;
		ArrayList<process> shortestFirst = new ArrayList<process>();
		PrintWriter output = new PrintWriter("process.out", "UTF-8");
		process running = null;

		output.println(numProcesses + " Processes");
		output.println("Shortest Job First");
		output.println();

		while (time <= runLength) {
			cpuBusy = false;
			for (int i = 0; i < processeslist.size(); i++)
				if (processeslist.get(i).arrival == time) {
					output.println("Time: " + time + " Process " + processeslist.get(i).name + " has arrived.");
					if (shortestFirst.size() == 0) {
						shortestFirst.add(processeslist.get(i));
					} else {
						boolean flag = true;
						for (int x = 0; x < shortestFirst.size(); x++) {
							if (processeslist.get(i).burstTime <= shortestFirst.get(x).burstTime) {
								if (x == 0)
									running = null;
								shortestFirst.add(x, processeslist.get(i));
								flag = false;
								break;
							}
						}
						if (flag)
							shortestFirst.add(shortestFirst.size(), processeslist.get(i));
					}
				}

			if (running != null && running.burstTime == 0) {
				running.turnaround = time - running.arrival;
				running.wait = running.turnaround - running.parallelBurstTime;
				output.println("Time: " + time + " Process " + running.name + " has finished.");
				running = null;
				shortestFirst.remove(0);

			}

			if (running == null && shortestFirst.size() != 0) {
				running = shortestFirst.get(0);
				output.println("Time: " + time + " Process " + running.name + " has been selected. (Burst "
						+ running.burstTime + ")");
			}

			if (running != null) {
				cpuBusy = true;
				running.burstTime--;
			}

			if (!cpuBusy && time < runLength)
				output.println("Time: " + time + " IDLE");

			time++;
		}
		output.println("Finished at time: " + (time - 1));
		output.println();

		for (int i = 0; i < numProcesses; i++) {
			output.println(processeslist.get(i).name + " wait " + processeslist.get(i).wait + " turnaround "
					+ processeslist.get(i).turnaround);
		}

		output.close();

		return;
	}
}
