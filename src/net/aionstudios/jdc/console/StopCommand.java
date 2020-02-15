package net.aionstudios.jdc.console;

import net.aionstudios.jdc.server.content.ContentProcessor;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;

public class StopCommand extends Command {

	public StopCommand() {
		super("stop");
	}

	@Override
	public void execute(String... args) {
		if(args==null||args.length!=0||(args.length>=1&&args[0].equals("help"))) {
			System.out.println(getHelp());
		} else if(args.length==0) {
			System.out.println("Shutting down server...");
			for(Website w : WebsiteManager.websites) {
				for(ContentProcessor cp : w.getProcessors()) {
					cp.getJDC().onShutdown();
				}
			}
			System.exit(0);
		} else {
			System.out.println("Incorrect usage for 'stop'!");
			System.out.println(getHelp());
		}
	}

	@Override
	public String getHelp() {
		return "Stops JDC Server.\r\n"
				+ "    USAGE: stop";
	}
	
}