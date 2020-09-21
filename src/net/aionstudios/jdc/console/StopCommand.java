package net.aionstudios.jdc.console;

import java.util.Map.Entry;

import net.aionstudios.jdc.server.content.ContentProcessor;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;

/**
 * A {@link Command} that safely shuts down the server.
 * @author Winter Roberts
 */
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
			for(Entry<String, Website> w : WebsiteManager.websites.entrySet()) {
				for(Entry<String, ContentProcessor> cp : w.getValue().getProcessors().entrySet()) {
					cp.getValue().getJDC().onShutdown();
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