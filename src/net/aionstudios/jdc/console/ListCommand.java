package net.aionstudios.jdc.console;

import java.util.List;

import net.aionstudios.jdc.JDC;
import net.aionstudios.jdc.server.content.ContentProcessor;
import net.aionstudios.jdc.server.content.JDCLoader;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;

public class ListCommand extends Command {

	public ListCommand() {
		super("list");
	}

	@Override
	public void execute(String... args) {
		if(args==null||args.length==0||(args.length>=1&&args[0].equals("help"))) {
			System.out.println(getHelp());
		} else if(args.length>=1&&args[0].equals("sites")) {
			String s = "Sites:";
			for(Website w : WebsiteManager.websites) {
				s = s.concat("\r\n    "+w.getName());
			}
			System.out.println(s);
		} else if(args.length>=2&&args[0].equals("processors")) {
			Website w = WebsiteManager.getWebsite(args[1]);
			if(w!=null) {
				String s = "Processors:";
				for(ContentProcessor cp : w.getProcessors()) {
					s = s.concat("\r\n    "+cp.getName());
				}
				System.out.println(s);
			} else {
				System.err.println("Couldn't locate website '"+args[1]+"'!");
			}
		} else {
			System.out.println("Incorrect usage for 'list'!");
			System.out.println(getHelp());
		}
	}

	@Override
	public String getHelp() {
		return "List content processors or sites.\r\n"
				+ "    USAGE: list [sites | processors <website>]";
	}
	
}