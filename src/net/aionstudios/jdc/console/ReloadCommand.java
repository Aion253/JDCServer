package net.aionstudios.jdc.console;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.aionstudios.jdc.JDC;
import net.aionstudios.jdc.server.content.ContentProcessor;
import net.aionstudios.jdc.server.content.JDCLoader;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;

/**
 * A {@link Command} that reloads content processors for the application, website, or single processors.
 * @author Winter Roberts
 */
public class ReloadCommand extends Command {

	public ReloadCommand() {
		super("reload");
	}

	@Override
	public void execute(String... args) {
		if(args==null||args.length==0||(args.length>=1&&args[0].equals("help"))) {
			System.out.println(getHelp());
		} else if(args.length>=2&&args[0].equals("site")) {
			Website w = WebsiteManager.getWebsite(args[1]);
			if(w!=null) {
				Set<Entry<String, ContentProcessor>> cps = w.getProcessors().entrySet();
				for(Entry<String, ContentProcessor> cp : cps) {
					JDC j = JDCLoader.getSingleJDC(cp.getValue().getArchive(), cp.getValue().getMainClass());
					if(j!=null) {
						j.initialize();
						cp.getValue().setJDC(j);
						System.out.println("Reloaded content processor '"+cp.getValue().getName()+"' for site '"+w.getName()+"'");
					} else {
						System.err.println("Failed reloading site '"+w.getName()+"'! JDC missing for '"+cp.getValue().getName()+"'!");
					}
				}
			} else {
				System.err.println("Couldn't locate website '"+args[1]+"'!");
			}
		} else if(args.length>=3&&args[0].equals("processor")) {
			Website w = WebsiteManager.getWebsite(args[1]);
			if(w!=null) {
				if(w.getProcessors().containsKey(args[2])){
					ContentProcessor cp = w.getProcessors().get(args[2]);
					JDC j = JDCLoader.getSingleJDC(cp.getArchive(), cp.getMainClass());
					if(j!=null) {
							j.initialize();
						cp.setJDC(j);
						System.out.println("Reloaded content processor '"+cp.getName()+"' for site '"+w.getName()+"'");
					} else {
						System.err.println("Failed reloading content processor '"+cp.getName()+"' for site '"+w.getName()+"'! JDC missing!");
					}
					return;
				}
				System.err.println("Failed reloading content processor '"+args[2]+"' for site '"+w.getName()+"'! No content processor!");
			} else {
				System.err.println("Couldn't locate website '"+args[1]+"'!");
			}
		} else if(args.length>0&&args[0].equals("all")) {
			for(Entry<String, Website> w : WebsiteManager.websites.entrySet()) {
				for(Entry<String, ContentProcessor> cp : w.getValue().getProcessors().entrySet()) {
					JDC j = JDCLoader.getSingleJDC(cp.getValue().getArchive(), cp.getValue().getMainClass());
					if(j!=null) {
						j.initialize();
						cp.getValue().setJDC(j);
						System.out.println("Reloaded content processor '"+cp.getValue().getName()+"' for site '"+w.getValue().getName()+"'");
					} else {
						System.err.println("Failed reloading site '"+w.getValue().getName()+"'! JDC missing for '"+cp.getValue().getName()+"'!");
					}
				}
			}
		} else {
			System.out.println("Incorrect usage for 'reload'!");
			System.out.println(getHelp());
		}
	}

	@Override
	public String getHelp() {
		return "Reloads content processors for the application, website, or single processor.\r\n"
				+ "    USAGE: reload [all | site <website> | processor <website> <processor>]";
	}

}
