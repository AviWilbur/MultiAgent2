//Distributed constraint satisfaction problem (CSP) where multiple agents work together to find a solution while communicating via messages 
//using a Mailer system. Each agent is responsible for choosing values from its domain that satisfy constraints with other agents, 
//backtracking when conflicts arise. The agents run concurrently as threads, exchanging partial solutions (CPAs) and handling backtracks
//until a consistent solution is found or all possibilities are exhausted. The Main class runs this simulation across different configurations,
//repeatedly generating CSP instances and measuring the average number of assignments, backtracks, and constraint checks across 100 trials
//for varying constraint probabilities.

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
	
	private static Agent a = new Agent();

	public static void main(String[] args) throws InterruptedException {

		// extract parameters
		int n = Integer.valueOf(args[0]).intValue();
		int d = Integer.valueOf(args[1]).intValue();
		double p1 = Double.valueOf(args[2]).doubleValue();
		

		//for p2 in range [0.1,...,0.9]
		for (double p2 = 0.1; p2 <= 0.9; p2 += 0.1) {
			
			// generate CSP
			Generator gen = new Generator(n, d, p1, p2);
			
			//generate and solve 100 problems
			for (int N = 0; N <100; N++) {
				
				CSP csp = gen.generateDCSP();
		
				// initialize mailer
				Mailer mailer = new Mailer();
				for (int i = 0; i < n; i++) {
					mailer.put(i);
				}
				
				// create agents
				ArrayList<Thread> threads = new ArrayList<Thread>();
				for (int i = 0; i < n; i++) {
					// use the csp to extract the private information of each agent
					HashMap<Integer, ConsTable> private_information = csp.tablesOf(i);
					Thread t = new Thread(new Agent(i, mailer, private_information, n, d));
					threads.add(t);
				}

				// run agents as threads
				for (Thread t : threads) {
					t.start();
				}
		
				// wait for all agents to terminate
				for (Thread t : threads) {
					t.join();
				}				
			}
			
			System.out.println("for p2 = " + p2 + ":");
			//print necessary information
			System.out.println(a.getAss() / 100 + " assignments on average");
			System.out.println(a.getBTs() / 100 + " backtracks on average");
			System.out.println(a.getCCs() / 100 + " CCs on average");
			System.out.println();
		}
	}
}
