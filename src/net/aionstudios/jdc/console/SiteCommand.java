package net.aionstudios.jdc.console;

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.jdc.content.ResponseCode;
import net.aionstudios.jdc.server.JDCServerInfo;
import net.aionstudios.jdc.server.content.ContentProcessor;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;
import net.aionstudios.jdc.server.util.FormatUtils;
import net.aionstudios.jdc.server.util.LinkedJSONObject;

public class SiteCommand extends Command {

	public SiteCommand() {
		super("site");
	}

	@Override
	public void execute(String... args) {
		if(args==null||args.length==0||(args.length>=1&&args[0].equals("help"))) {
			System.out.println(getHelp());
		} else if(args.length>=2) {
			Website website = args[1].equals("create")?null:WebsiteManager.getWebsite(args[0]);
			if (website!=null||args[1].equals("create")) {
				if (args[1].equals("create")) {
					try {
						File sitesJsonFile = new File("./sites.json");
						JSONObject sitesJson = JDCServerInfo.readConfig(sitesJsonFile, true);
						JSONArray sites = sitesJson.getJSONArray("websites");
						for (int i = 0; i < sites.length(); i++) {
							if (sites.getJSONObject(i).getString("name").equals(args[0])) {
								sites.remove(i);
								break;
							}
						}
						JSONObject newSite = new LinkedJSONObject();
						newSite.put("name", args[0]);
						newSite.put("addresses", new JSONArray());
						newSite.put("ssl_enabled", args.length>2?args[2].equals("true"):true);
						sites.put(newSite);
						new Website(args[0], new String[0], args.length>2?args[2].equals("true"):true);
						JDCServerInfo.writeConfig(sitesJson, sitesJsonFile);
					} catch (JSONException e) {
						System.err.println("An unhandled exception caused the operation to fail.");
						e.printStackTrace();
					}
				} else if (args[1].equals("address")) {
					//CHANGES ORDER BUT I DONT WANT TO DEAL WITH IT RN TODO
					try {
						if (args.length>=4&&args[2].equals("add")) {
							File sitesJsonFile = new File("./sites.json");
							JSONObject sitesJson = JDCServerInfo.readConfig(sitesJsonFile, true);
							JSONArray sites = sitesJson.getJSONArray("websites");
							for (int i = 0; i < sites.length(); i++) {
								if (sites.getJSONObject(i).getString("name").equals(website.getName())) {
									for (int j = 3; j < args.length; j++) {
										JSONArray addrs = sites.getJSONObject(i).getJSONArray("addresses");
										addrs.put(args[j]);
										website.addAddress(args[j]);
									}
								}
							}
							JDCServerInfo.writeConfig(sitesJson, sitesJsonFile);
						} else if (args.length>=4&&args[2].equals("remove")) {
							File sitesJsonFile = new File("./sites.json");
							JSONObject sitesJson = JDCServerInfo.readConfig(sitesJsonFile, true);
							JSONArray sites = sitesJson.getJSONArray("websites");
							for (int i = 0; i < sites.length(); i++) {
								if (sites.getJSONObject(i).getString("name").equals(website.getName())) {
									for (int j = 3; j < args.length; j++) {
										JSONArray addrs = sites.getJSONObject(i).getJSONArray("addresses");
										for (int k = 0 ; k < addrs.length(); k++) {
											if (addrs.getString(k).equals(args[j])) {
												addrs.remove(k);
												website.removeAddress(args[j]);
												break;
											}
										}
									}
								}
							}
							JDCServerInfo.writeConfig(sitesJson, sitesJsonFile);
						} else {
							printHelpMessage("Missing arguments for 'address'!");
						}
					} catch (JSONException e) {
						System.err.println("An unhandled exception caused the operation to fail.");
						e.printStackTrace();
					}
				} else if (args[1].equals("delete")) {
					//THIS ONLY REMOVES THE LISTING NOT THE FILES. FOR SAFETY
					try {
						File sitesJsonFile = new File("./sites.json");
						JSONObject sitesJson = JDCServerInfo.readConfig(sitesJsonFile, true);
						JSONArray sites = sitesJson.getJSONArray("websites");
						for (int i = 0; i < sites.length(); i++) {
							if (sites.getJSONObject(i).getString("name").equals(website.getName())) {
								sites.remove(i);
								break;
							}
						}
						WebsiteManager.websites.remove(website.getName());
						JDCServerInfo.writeConfig(sitesJson, sitesJsonFile);
					} catch (JSONException e) {
						System.err.println("An unhandled exception caused the operation to fail.");
						e.printStackTrace();
					}
				} else if (args[1].equals("proxy")) {
					try {
						File proxiesJsonFile = new File("./websites/"+website.getName()+"/proxies.json");
						JSONObject proxiesJson = JDCServerInfo.readConfig(proxiesJsonFile, true);
						if (args.length>=5&&args[2].equals("set")) {
							String argBase = args[3];
							String argRedirect = args[4];
							JSONArray proxies = proxiesJson.getJSONArray("proxies");
							for(int i = 0; i < proxies.length(); i++) {
								JSONObject proxy = proxies.getJSONObject(i);
								if (proxy.getString("context").equals(argBase)) {
									proxies.remove(i);
									break;
								}
							}
							JSONObject proxyNew = new LinkedJSONObject();
							proxyNew.put("context", argBase);
							proxyNew.put("proxy_url", argRedirect);
							proxies.put(proxyNew);
							JDCServerInfo.writeConfig(proxiesJson, proxiesJsonFile);
							System.out.println("Proxy set successfully, reloading proxy config for site '"+args[0]+"'.");
							website.readProxiesConfig();
						} else if (args.length>=4&&args[2].equals("unset")) {
							String argBase = args[3];
							JSONArray proxies = proxiesJson.getJSONArray("proxies");
							for(int i = 0; i < proxies.length(); i++) {
								JSONObject proxy = proxies.getJSONObject(i);
								if (proxy.getString("context").equals(argBase)) {
									proxies.remove(i);
									break;
								}
							}
							JDCServerInfo.writeConfig(proxiesJson, proxiesJsonFile);
							System.out.println("Proxy removed successfully, reloading proxy config for site '"+args[0]+"'.");
							website.readProxiesConfig();
						} else {
							printHelpMessage("Missing arguments for 'proxy'!");
						}
					} catch (JSONException e) {
						System.err.println("An unhandled exception caused the operation to fail.");
						e.printStackTrace();
					}
				} else if (args[1].equals("processor")) {
					try {
						File processorsJsonFile = new File("./websites/"+website.getName()+"/processors.json");
						JSONObject processorsJson = JDCServerInfo.readConfig(processorsJsonFile, true);
						if (args.length>=6&&args[2].equals("add")) {
							String pName = args[3];
							File argJar = new File(args[4]);
							String jdcPath = args[5];
							if (argJar.exists()) {
								JSONArray procs = processorsJson.getJSONArray("processors");
								for(int i = 0; i < procs.length(); i++) {
									JSONObject proc = procs.getJSONObject(i);
									if (proc.getString("name").equals(pName)) {
										procs.remove(i);
										break;
									}
								}
								JSONObject newProc = new LinkedJSONObject();
								newProc.put("jdc_class", jdcPath);
								newProc.put("name", pName);
								newProc.put("jar", args[4]);
								procs.put(newProc);
								JDCServerInfo.writeConfig(processorsJson, processorsJsonFile);
								System.out.println("Processor added successfully. Use 'reload' to register it to the site in this server.");
								website.readProcessorsConfig();
							} else {
								printHelpMessage("File not found! "+args[4]);
							}
						} else if (args.length>=4&&args[2].equals("remove")) {
							String pName = args[3];
							JSONArray procs = processorsJson.getJSONArray("processors");
							for(int i = 0; i < procs.length(); i++) {
								JSONObject proc = procs.getJSONObject(i);
								if (proc.getString("name").equals(pName)) {
									procs.remove(i);
									break;
								}
							}
							JDCServerInfo.writeConfig(processorsJson, processorsJsonFile);
							System.out.println("Processor removed successfully. Use 'reload' to unregister it for the site in this server.");
							website.readProcessorsConfig();
						}
					} catch (JSONException e) {
						System.err.println("An unhandled exception caused the operation to fail.");
						e.printStackTrace();
					}
				} else if (args[1].equals("errorpage")) {
					try {
						File errorsJsonFile = new File("./websites/"+website.getName()+"/errors.json");
						JSONObject errorsJson = JDCServerInfo.readConfig(errorsJsonFile, true);
						if (args.length>=5&&args[2].equals("set")) {
							int argCode = Integer.parseInt(args[3]);
							File argPath = new File(args[4]);
							for (ResponseCode rc : ResponseCode.values()) {
								if (rc.getCode()==argCode) {
									if (argPath.exists()) {
										JSONArray errs = errorsJson.getJSONArray("errors");
										for(int i = 0; i < errs.length(); i++) {
											JSONObject err = errs.getJSONObject(i);
											if (err.getInt("error_code")==argCode) {
												errs.remove(i);
												break;
											}
										}
										JSONObject errNew = new LinkedJSONObject();
										errNew.put("error_code", argCode);
										errNew.put("error_file", args[4]);
										errNew.put("enable_override", true);
										errs.put(errNew);
										JDCServerInfo.writeConfig(errorsJson, errorsJsonFile);
										System.out.println("Errorpage set successfully, reloading error config for site '"+args[0]+"'.");
										website.readErrorsConfig();
									} else {
										printHelpMessage("File not found! "+args[4]);
									}
								}
							}
						} else if (args.length>=4&&args[2].equals("unset")) {
							int argCode = Integer.parseInt(args[3]);
							for (ResponseCode rc : ResponseCode.values()) {
								if (rc.getCode()==argCode) {
									JSONArray errs = errorsJson.getJSONArray("errors");
									for(int i = 0; i < errs.length(); i++) {
										JSONObject err = errs.getJSONObject(i);
										if (err.getInt("error_code")==argCode) {
											errs.remove(i);
											break;
										}
									}
									JDCServerInfo.writeConfig(errorsJson, errorsJsonFile);
									System.out.println("Errorpage removed successfully, reloading error config for site '"+args[0]+"'.");
									website.readErrorsConfig();
								}
							}
						} else {
							printHelpMessage("Missing arguments for 'errorpages'!");
						}
					} catch (NumberFormatException e) {
						printHelpMessage("Errorpage code must be an integer!");
					} catch (JSONException e) {
						System.err.println("An unhandled exception caused the operation to fail.");
						e.printStackTrace();
					}
				} else if (args[1].equals("ssl")) {
					JSONObject jo = new LinkedJSONObject();
				} else if (args[1].equals("view")) {
					if (args[2].equals("addresses")) {
						for (String addr : website.getAddresses()) {
							System.out.println(addr);
						}
					} else if (args[2].equals("proxies")) {
						website.getProxyManager().printProxies();
					} else if (args[2].equals("processors")) {
						website.printProcessors();
					} else if (args[2].equals("errorpages")) {
						website.printErrorMapping();
					} else if (args[2].equals("security")) {
						System.out.println("SSL ENABLED: "+(website.isSslOn()?"TRUE":"FALSE"));
					} else {
						printIncorrect();
					}
				} else {
					printIncorrect();
				}
			} else {
				System.err.println("Couldn't locate website '" + args[0] + "'!");
			}
//			JSONObject defWeb = FormatUtils.getLinkedJsonObject();
//			defWeb.put("name", "default");
//			JSONArray defWebAddrs = new JSONArray();
//			defWebAddrs.put("localhost");
//			defWeb.put("addresses", defWebAddrs);
//			defWeb.put("ssl_enabled", false);
//			ws.put(defWeb);
		} else {
			printIncorrect();
		}
	}

	@Override
	public String getHelp() {
		return "Create a site and modify or show site properties.\r\n"
				+ "    USAGE:\r\n"
				+ "      site <website> create [enable_ssl]\r\n"
				+ "      site <website> address {add | remove} <address> ...\r\n"
				+ "      site <website> delete\r\n"
				+ "      site <website> proxy {set <base> <redirect> | unset <base>}\r\n"
				+ "      site <website> processor {add <name> <jarpath> <jdc_entrypoint> | remove <name>}\r\n"
				+ "      site <website> errorpage {set <code> <path> | unset <code>}\r\n"
				+ "      site <website> ssl {enable | disable}\r\n"
				+ "      site <website> view {addresses | proxies | processors | errorpages | security}";
	}
	
	public void printIncorrect() {
		printHelpMessage("Incorrect usage for 'site'!");
	}
	
	public void printHelpMessage(String hm) {
		System.out.println(hm);
		System.out.println(getHelp());
	}

}
