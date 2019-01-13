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

/**
 * Locates pages, reads them, and completes processing and modification via {@link Processor}s and {@link ElementProcessor}s when they are found.
 * @author Winter
 *
 */
public class PageParser {
	
	/**
	 * Parses a page, passing each next tag to its respective {@link Processor} of {@link ElementProcessor}.
	 * @param w		The {@link Website} on which the request was made.
	 * @param he	The {@link HttpExchange} through which the request will be completed.
	 * @param vars	The {@link RequestVariables} incorporating request and response variables to create dynamic responses.
	 * @param page	The file name of the requested page.
	 * @return A {@link GeneratorReponse} having completed processing.
	 */
	public static GeneratorResponse parseGeneratePage(Website w, HttpExchange he, RequestVariables vars, File page) {
		vars.setResponseCode(ResponseCode.OK);
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
			if(vars.getResponseCode().getCode()!=200) {
				return new GeneratorResponse("", vars.getResponseCode());
			}
			if(e.hasAttr("javaexecute")) {
				try {
					w.locateProcessor(vars.getResponseCode(), e.attr("javaexecute")).startCompute(he, vars, pageVariables);
				} catch (Exception e1) {
					ConsoleErrorUtils.printServerError(ResponseCode.INTERNAL_SERVER_ERROR, e.attr("javaexecute"), e1);
					return new GeneratorResponse("", ResponseCode.INTERNAL_SERVER_ERROR);
				}
			}
			if(e.hasAttr("javagenerate")) {
				JDCHeadElement head = new JDCHeadElement(e.html());
				try {
					e.before(w.locateElementProcessor(vars.getResponseCode(), e.attr("javagenerate")).getContent(head, he, vars, pageVariables));
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
			if(vars.getResponseCode().getCode()!=200) {
				return new GeneratorResponse("", vars.getResponseCode());
			}
			JDCHeadElement head = new JDCHeadElement(e.html());
			try {
				e.html(w.locateElementProcessor(vars.getResponseCode(), e.attr("javagenerate")).getContent(head, he, vars, pageVariables));
			} catch (Exception e1) {
				ConsoleErrorUtils.printServerError(ResponseCode.INTERNAL_SERVER_ERROR, e.attr("javagenerate"), e1);
				return new GeneratorResponse("", ResponseCode.INTERNAL_SERVER_ERROR);
			}
			for(int i = 0; i < head.getAttributes().size(); i++) {
				e.attr((String) head.getAttributes().keySet().toArray()[i], head.getAttributes().get(head.getAttributes().keySet().toArray()[i]));
			}
			e.removeAttr("javagenerate");
		}
		return new GeneratorResponse(doc.outerHtml(), vars.getResponseCode());
	}

}
