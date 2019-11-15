package net.aionstudios.jdc.console;

public abstract class Command {
	
	private String command;
	
	public Command(String command) {
		this.command = command;
		JDCConsole.getInstance().registerCommand(this);
	}
	
	public String getCommand() {
		return command;
	}
	
	public abstract void execute(String... args);
	public abstract String getHelp();
	
}
