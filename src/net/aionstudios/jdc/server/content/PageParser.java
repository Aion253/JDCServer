package net.aionstudios.jdc.server.content;

import java.io.File;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.aionstudios.jdc.content.JDCHeadElement;
import net.aionstudios.jdc.server.JDCServerInfo;

public class PageParser {
	
	public static String parseGeneratePage(Website w, HttpExchange he, Map<String, String> post, Map<String, String> get, File page) {
		Document doc = Jsoup.parse(JDCServerInfo.readFile(page));
		for(Element e : doc.getElementsByAttribute("javaexecute")) {
			e.html(w.locateElementProcessor(e.attr("javaexecute")).getContent(new JDCHeadElement(), he, post, get));
			e.removeAttr("javaexecute");
		}
		return doc.outerHtml();
	}

}
