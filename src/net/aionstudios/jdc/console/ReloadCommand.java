package net.aionstudios.jdc.console;

import java.util.List;

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
				List<ContentProcessor> cp = w.getProcessors();
				for(int i = 0; i < cp.size(); i++) {
					JDC j = JDCLoader.getSingleJDC(cp.get(i).getArchive(), cp.get(i).getMainClass());
					if(j!=null) {
						j.initialize();
						cp.get(i).setJDC(j);
						System.out.println("Reloaded content processor '"+cp.get(i).getName()+"' for site '"+w.getName()+"'");
					} else {
						System.err.println("Failed reloading site '"+w.getName()+"'! JDC missing for '"+cp.get(i).getName()+"'!");
					}
				}
			} else {
				System.err.println("Couldn't locate website '"+args[1]+"'!");
			}
		} else if(args.length>=3&&args[0].equals("processor")) {
			Website w = WebsiteManager.getWebsite(args[1]);
			if(w!=null) {
				List<ContentProcessor> cp = w.getProcessors();
				for(int i = 0; i < cp.size(); i++) {
					if(cp.get(i).getName().equals(args[2])){
						JDC j = JDCLoader.getSingleJDC(cp.get(i).getArchive(), cp.get(i).getMainClass());
						if(j!=null) {
							j.initialize();
							cp.get(i).setJDC(j);
							System.out.println("Reloaded content processor '"+cp.get(i).getName()+"' for site '"+w.getName()+"'");
						} else {
							System.err.println("Failed reloading content processor '"+cp.get(i).getName()+"' for site '"+w.getName()+"'! JDC missing!");
						}
						return;
					}
				}
				System.err.println("Failed reloading content processor '"+args[2]+"' for site '"+w.getName()+"'! No content processor!");
			} else {
				System.err.println("Couldn't locate website '"+args[1]+"'!");
			}
		} else if(args.length>0&&args[0].equals("all")) {
			for(Website w : WebsiteManager.websites) {
				List<ContentProcessor> cp = w.getProcessors();
				for(int i = 0; i < cp.size(); i++) {
					JDC j = JDCLoader.getSingleJDC(cp.get(i).getArchive(), cp.get(i).getMainClass());
					if(j!=null) {
						j.initialize();
						cp.get(i).setJDC(j);
						System.out.println("Reloaded content processor '"+cp.get(i).getName()+"' for site '"+w.getName()+"'");
					} else {
						System.err.println("Failed reloading site '"+w.getName()+"'! JDC missing for '"+cp.get(i).getName()+"'!");
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
