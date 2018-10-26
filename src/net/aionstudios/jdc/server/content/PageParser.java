package net.aionstudios.jdc.server.content;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.aionstudios.jdc.content.JDCHeadElement;
import net.aionstudios.jdc.content.RequestVariables;
import net.aionstudios.jdc.content.ResponseCode;
import net.aionstudios.jdc.server.JDCServerInfo;
import net.aionstudios.jdc.server.util.ConsoleErrorUtils;

public class PageParser {
	
	public static GeneratorResponse parseGeneratePage(Website w, HttpExchange he, RequestVariables vars, File page) {
		ResponseCode rc = ResponseCode.OK;
		if(!page.exists()) {
			return new GeneratorResponse("", ResponseCode.NOT_FOUND);
		}
		Map<String, Object> pageVariables = new HashMap<String, Object>();
		String fileContent = JDCServerInfo.readFile(page);
		if(fileContent==null) {
			return new GeneratorResponse("", ResponseCode.NO_CONTENT);
		}
		Document doc = doc = Jsoup.parse(fileContent);
		for(Element e : doc.getElementsByTag("jdc")) {
			if(rc.getCode()!=200) {
				return new GeneratorResponse("", rc);
			}
			if(e.hasAttr("javaexecute")) {
				try {
					w.locateProcessor(rc, e.attr("javaexecute")).startCompute(he, vars, pageVariables);
				} catch (Exception e1) {
					ConsoleErrorUtils.printServerError(ResponseCode.INTERNAL_SERVER_ERROR, e.attr("javaexecute"), e1);
					return new GeneratorResponse("", ResponseCode.INTERNAL_SERVER_ERROR);
				}
			}
			if(e.hasAttr("javagenerate")) {
				JDCHeadElement head = new JDCHeadElement();
				try {
					e.before(w.locateElementProcessor(rc, e.attr("javagenerate")).getContent(head, he, vars, pageVariables));
				} catch (Exception e1) {
					ConsoleErrorUtils.printServerError(ResponseCode.INTERNAL_SERVER_ERROR, e.attr("javagenerate"), e1);
					return new GeneratorResponse("", ResponseCode.INTERNAL_SERVER_ERROR);
				}
				for(int i = 0; i < head.getAttributes().size(); i++) {
					e.attr((String) head.getAttributes().keySet().toArray()[i], head.getAttributes().get(head.getAttributes().keySet().toArray()[i]));
				}
			}
			e.remove();
		}
		for(Element e : doc.getElementsByAttribute("javagenerate")) {
			if(rc.getCode()!=200) {
				return null;
			}
			JDCHeadElement head = new JDCHeadElement();
			try {
				e.html(w.locateElementProcessor(rc, e.attr("javagenerate")).getContent(head, he, vars, pageVariables));
			} catch (Exception e1) {
				ConsoleErrorUtils.printServerError(ResponseCode.INTERNAL_SERVER_ERROR, e.attr("javagenerate"), e1);
				return new GeneratorResponse("", ResponseCode.INTERNAL_SERVER_ERROR);
			}
			for(int i = 0; i < head.getAttributes().size(); i++) {
				e.attr((String) head.getAttributes().keySet().toArray()[i], head.getAttributes().get(head.getAttributes().keySet().toArray()[i]));
			}
			e.removeAttr("javagenerate");
		}
		return new GeneratorResponse(doc.outerHtml(), rc);
	}

}
