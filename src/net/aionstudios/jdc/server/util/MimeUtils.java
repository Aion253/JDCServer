package net.aionstudios.jdc.server.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for working with file mime types.
 * @author Winter Roberts
 */
public class MimeUtils {
	
	private Map<String, String> mimeExtensionMap = new HashMap<String, String>();
	private static MimeUtils self;
	
	/**
	 * A singleton constructor which registers system-defined mime types.
	 */
	private MimeUtils() {
		self = this;
		mimeExtensionMap.putIfAbsent("html", "text/html");
		mimeExtensionMap.putIfAbsent("htm", "text/html");
		mimeExtensionMap.putIfAbsent("htmls", "text/html");
		mimeExtensionMap.putIfAbsent("shtml", "text/html");
		mimeExtensionMap.putIfAbsent("xhtml", "application/xhtml+xml");
		mimeExtensionMap.putIfAbsent("htx", "text/html");
		mimeExtensionMap.putIfAbsent("htt", "text/webviewhtml");
		mimeExtensionMap.putIfAbsent("jdc", "text/html");
		mimeExtensionMap.putIfAbsent("js", "application/javascript");
		mimeExtensionMap.putIfAbsent("css", "text/css");
		mimeExtensionMap.putIfAbsent("svg", "image/svg+xml");
		mimeExtensionMap.putIfAbsent("txt", "text/plain");
		mimeExtensionMap.putIfAbsent("jpg", "image/jpeg");
		mimeExtensionMap.putIfAbsent("jpeg", "image/jpeg");
		mimeExtensionMap.putIfAbsent("jpe", "image/jpeg");
		mimeExtensionMap.putIfAbsent("jfif", "image/jpeg");
		mimeExtensionMap.putIfAbsent("jfif-tbnl", "image/jpeg");
		mimeExtensionMap.putIfAbsent("png", "image/png");
		mimeExtensionMap.putIfAbsent("class", "application/java");
		mimeExtensionMap.putIfAbsent("crl", "application/pkcs-crl");
		mimeExtensionMap.putIfAbsent("avi", "video/avi");
		mimeExtensionMap.putIfAbsent("bmp", "image/bmp");
		mimeExtensionMap.putIfAbsent("cer", "application/x-x509-ca-cert");
		mimeExtensionMap.putIfAbsent("crt", "application/x-x509-ca-cert");
		mimeExtensionMap.putIfAbsent("doc", "application/msword");
		mimeExtensionMap.putIfAbsent("gif", "image/gif");
		mimeExtensionMap.putIfAbsent("gtar", "application/x-gtar");
		mimeExtensionMap.putIfAbsent("gz", "application/x-compressed");
		mimeExtensionMap.putIfAbsent("gzip", "application/x-gzip");
		mimeExtensionMap.putIfAbsent("ico", "image/x-icon");
		mimeExtensionMap.putIfAbsent("java", "text/x-java-source");
		mimeExtensionMap.putIfAbsent("kar", "audio/midi");
		mimeExtensionMap.putIfAbsent("m1v", "video/mpeg");
		mimeExtensionMap.putIfAbsent("m2a", "audio/mpeg");
		mimeExtensionMap.putIfAbsent("m2v", "video/mpeg");
		mimeExtensionMap.putIfAbsent("m3u", "audio/x-mpequrl");
		mimeExtensionMap.putIfAbsent("mid", "audio/midi");
		mimeExtensionMap.putIfAbsent("midi", "audio/midi");
		mimeExtensionMap.putIfAbsent("mime", "www/mime");
		mimeExtensionMap.putIfAbsent("mjpg", "video/x-motion-jpeg");
		mimeExtensionMap.putIfAbsent("mm", "application/base64");
		mimeExtensionMap.putIfAbsent("mme", "application/base64");
		mimeExtensionMap.putIfAbsent("mod", "audio/mod");
		mimeExtensionMap.putIfAbsent("moov", "video/quicktime");
		mimeExtensionMap.putIfAbsent("mov", "video/quicktime");
		mimeExtensionMap.putIfAbsent("movie", "video/x-sgi-movie");
		mimeExtensionMap.putIfAbsent("mp2", "audio/mpeg");
		mimeExtensionMap.putIfAbsent("mp3", "audio/mpeg3");
		mimeExtensionMap.putIfAbsent("mpa", "audio/mpeg");
		mimeExtensionMap.putIfAbsent("mpeg", "video/mpeg");
		mimeExtensionMap.putIfAbsent("mpg", "video/mpeg");
		mimeExtensionMap.putIfAbsent("mpga", "video/mpeg");
		mimeExtensionMap.putIfAbsent("p10", "application/pkcs10");
		mimeExtensionMap.putIfAbsent("p12", "application/pkcs-12");
		mimeExtensionMap.putIfAbsent("pdf", "application/pdf");
		mimeExtensionMap.putIfAbsent("pot", "application/mspowerpoint");
		mimeExtensionMap.putIfAbsent("ppa", "application/vnd.ms-powerpoint");
		mimeExtensionMap.putIfAbsent("pps", "application/mspowerpoint");
		mimeExtensionMap.putIfAbsent("ppt", "application/powerpoint");
		mimeExtensionMap.putIfAbsent("pptx", "application/powerpoint");
		mimeExtensionMap.putIfAbsent("ppz", "application/mspowerpoint");
		mimeExtensionMap.putIfAbsent("qif", "image/x-quicktime");
		mimeExtensionMap.putIfAbsent("qt", "video/quicktime");
		mimeExtensionMap.putIfAbsent("qtc", "image/x-quicktime");
		mimeExtensionMap.putIfAbsent("qti", "image/x-quicktime");
		mimeExtensionMap.putIfAbsent("qtif", "image/x-quicktime");
		mimeExtensionMap.putIfAbsent("rt", "text/richtext");
		mimeExtensionMap.putIfAbsent("rtf", "application/rtf");
		mimeExtensionMap.putIfAbsent("rtx", "application/rtf");
		mimeExtensionMap.putIfAbsent("tgz", "applicaiton/gnutar");
		mimeExtensionMap.putIfAbsent("tif", "image/tiff");
		mimeExtensionMap.putIfAbsent("tiff", "image/tiff");
		mimeExtensionMap.putIfAbsent("wav", "audio/wav");
		mimeExtensionMap.putIfAbsent("w6w", "application/msword");
		mimeExtensionMap.putIfAbsent("docx", "application/msword");
		mimeExtensionMap.putIfAbsent("wiz", "application/msword");
		mimeExtensionMap.putIfAbsent("word", "application/msword");
		mimeExtensionMap.putIfAbsent("xl", "application/excel");
		mimeExtensionMap.putIfAbsent("xla", "application/excel");
		mimeExtensionMap.putIfAbsent("xlb", "application/excel");
		mimeExtensionMap.putIfAbsent("xlc", "application/excel");
		mimeExtensionMap.putIfAbsent("xld", "application/excel");
		mimeExtensionMap.putIfAbsent("xlk", "application/excel");
		mimeExtensionMap.putIfAbsent("xll", "application/excel");
		mimeExtensionMap.putIfAbsent("xlm", "application/excel");
		mimeExtensionMap.putIfAbsent("xls", "application/excel");
		mimeExtensionMap.putIfAbsent("xlsx", "application/excel");
		mimeExtensionMap.putIfAbsent("xlt", "application/excel");
		mimeExtensionMap.putIfAbsent("xlv", "application/excel");
		mimeExtensionMap.putIfAbsent("xlw", "application/excel");
		mimeExtensionMap.putIfAbsent("xml", "application/xml");
		mimeExtensionMap.putIfAbsent("z", "application/x-compressed");
		mimeExtensionMap.putIfAbsent("zip", "application/x-compressed");
		mimeExtensionMap.putIfAbsent("json", "application/json");
		mimeExtensionMap.putIfAbsent("aac", "audio/aac");
		mimeExtensionMap.putIfAbsent("csv", "text/csv");
		mimeExtensionMap.putIfAbsent("ics", "text/calendar");
		mimeExtensionMap.putIfAbsent("jar", "application/java-archive");
		mimeExtensionMap.putIfAbsent("mjs", "text/javascript");
		mimeExtensionMap.putIfAbsent("odp", "application/vnd.oasis.opendocument.presentation");
		mimeExtensionMap.putIfAbsent("ods", "application/vnd.oasis.opendocument.spreadsheet");
		mimeExtensionMap.putIfAbsent("odt", "application/vnd.oasis.opendocument.text");
		mimeExtensionMap.putIfAbsent("oga", "audio/ogg");
		mimeExtensionMap.putIfAbsent("ogg", "audio/ogg");
		mimeExtensionMap.putIfAbsent("ogv", "video/ogg");
		mimeExtensionMap.putIfAbsent("ogx", "application/ogg");
		mimeExtensionMap.putIfAbsent("otf", "font/otf");
		mimeExtensionMap.putIfAbsent("eot", "application/vnd.ms-fontobject");
		mimeExtensionMap.putIfAbsent("epub", "application/epub+zip");
		mimeExtensionMap.putIfAbsent("php", "application/php");
		mimeExtensionMap.putIfAbsent("rar", "application/x-rar-compressed");
		mimeExtensionMap.putIfAbsent("sh", "application/x-sh");
		mimeExtensionMap.putIfAbsent("swf", "application/x-shockwave-flash");
		mimeExtensionMap.putIfAbsent("tar", "application/x-tar");
		mimeExtensionMap.putIfAbsent("ts", "video/mp2t");
		mimeExtensionMap.putIfAbsent("ttf", "font/ttf");
		mimeExtensionMap.putIfAbsent("weba", "audio/webm");
		mimeExtensionMap.putIfAbsent("webm", "video/webm");
		mimeExtensionMap.putIfAbsent("webp", "image/webp");
		mimeExtensionMap.putIfAbsent("woff", "font/woff");
		mimeExtensionMap.putIfAbsent("woff2", "font/woff2");
		mimeExtensionMap.putIfAbsent("3gp", "video/3gpp");
		mimeExtensionMap.putIfAbsent("3g2", "video/3gpp");
		mimeExtensionMap.putIfAbsent("7z", "application/x-7z-compressed");
		mimeExtensionMap.putIfAbsent("flac", "audio/flac");
		mimeExtensionMap.putIfAbsent("m4a", "audio/m4a");
		mimeExtensionMap.putIfAbsent("m4v", "video/m4a");
	}
	
	/**
	 * @return A singleton instance of {@link MimeUtils}.
	 */
	public static MimeUtils getInstance() {
		return self != null ? self : new MimeUtils();
	}
	
	/**
	 * Adds a mime type if it's not already been defined by the application.
	 * @param extension The unique file type for which this mime type should be assigned.
	 * @param mimeString The mime type String which which the extension will be assigned.
	 */
	public void addMimeType(String extension, String mimeString) {
		mimeExtensionMap.putIfAbsent(extension, mimeString);
	}
	
	/**
	 * @param extension The extension for a file.
	 * @return The mime type string if this extension is registered
	 * or application/octet-stream otherwise.
	 */
	public String getMimeString(String extension) {
		return mimeExtensionMap.getOrDefault(extension, "application/octet-stream");
	}

}
