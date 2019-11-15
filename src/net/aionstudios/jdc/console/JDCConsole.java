package net.aionstudios.jdc.console;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class JDCConsole {
	
	private static JDCConsole self;
	private static Thread consoleThread;
	private static boolean running;
	
	private List<Command> commands;
	
	private JDCConsole() {
		commands = new ArrayList<>();
		running = false;
		consoleThread = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean useConsole = true;
				Console c = System.console();
				Scanner s = null;
				if(c==null){
					useConsole = false;
					s = new Scanner(System.in);
				}
				while(running) {
					String[] command;
					if(useConsole) command = c.readLine("> ").split("\\s+");
					else command = s.nextLine().split("\\s+");
					boolean found = false;
					if(command[0].equals("?")||command[0].equals("help")) {
						String helpString = "Commands and usage:\r\n";
						for(Command co : commands) {
							helpString = helpString.concat("'"+co.getCommand()+"' "+co.getHelp()+"\r\n");
						}
						System.out.println(helpString);
					} else {
						for(Command co : commands) {
							if(co.getCommand().equals(command[0])) {
								co.execute(Arrays.copyOfRange(command, 1, command.length));
								found=true;
							}
							if(found) break;
						}
						if(!found) {
							System.out.println("Couldn't find command '"+command[0]+"'. Try '?' for help.");
						}
					}
				}
				if(s!=null) {
					s.close();
				}
			}
			
		});
		consoleThread.setName("JDCConsole-Thread");
	}
	
	public static JDCConsole getInstance() {
		if(self==null) {
			self = new JDCConsole();
		}
		return self;
	}
	
	public void startConsoleThread() {
		if(!consoleThread.isAlive()) {
			running = true;
			consoleThread.start();
		}
	}
	
	public void stopConsoleThread() {
		running = false;
	}
	
	public boolean isAlive() {
		return consoleThread!=null&&consoleThread.isAlive();
	}
	
	public void registerCommand(Command c) {
		for(Command s : commands) {
			if(c==s||c.getCommand().equals(s.getCommand())){
				System.out.println("Duplicate command failed! '"+c.getCommand()+"'");
				return;
			}
		}
		commands.add(c);
	}

}
