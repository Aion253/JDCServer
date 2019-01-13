package net.aionstudios.jdc.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsParameters;

import net.aionstudios.jdc.context.SecureContextHandler;

/**
 * An HTTPS enabled instance of the {@link JDCServer}, to handle secured requests.
 * @author Winter
 *
 */
public class JDCSecureServer {
	
	private static boolean started = false;
	private static HttpsServer server;
	private static int port = 443;
	
	/*
	 * To generate a compatible JKS certificate it is recommended to get the default cert/key files from a certificate authority
	 * and use OpenSSL to convert it to a PKCS12 certificate. Followed then by using the Java Keytool to convert that to a JKS
	 * certificate with commands as follows:
	 * 
	 * openssl pkcs12 -export -in <certfile> -inkey <keyfile> -out <keystorefile> -name <alias> -CAfile <cacertfile> -caname root
	 * 
	 * keytool -importkeystore -deststorepass <keystorepass> -destkeypass <keystorepass> -destkeystore <jksfile> -srckeystore <keystorefile> -srcstoretype PKCS12 -srcstorepass <keystorepass> -alias <alias>
	 * 
	 */
	
	/**
	 * Starts a new {@link HttpsServer} after adding a valid direct or chain JKS certificate file to the key and trust SunX509 managers.
	 * @param certificate		A string representing the path to the certificate file below the relative path ./certs/
	 * @param storePassword		The JKS {@link KeyStore}'s password.
	 * @param keyPassword		The password for a single {@link Certificate} in the JKS {@link KeyStore}.
	 * @param certificateAlias	The JKS {@link KeyStore}'s named certificate.
	 */
	public static void startServer(String certificate, String storePassword, String keyPassword, String certificateAlias) {
		if(!started) {
			boolean noError = false;
			try {
				// load certificate
				String keystoreFilename = "./certs/" + certificate;
				char[] storepass = storePassword.toCharArray();
				char[] keypass = keyPassword.toCharArray();
				String alias = certificateAlias;
				FileInputStream fIn = new FileInputStream(keystoreFilename);
				KeyStore keystore = KeyStore.getInstance("JKS");
				keystore.load(fIn, storepass);
				// display certificate
				Certificate cert = keystore.getCertificate(alias);
				// setup the key manager factory
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(keystore, keypass);
				// setup the trust manager factory
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				tmf.init(keystore);
				//initialize server
				server = HttpsServer.create(new InetSocketAddress(port), 0);
				// create ssl context
				SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
				// setup the HTTPS context and parameters
				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
				         public void configure(HttpsParameters params) {
				                 try {
				                          // initialise the SSL context
				                          SSLContext c = SSLContext.getDefault();
				                          SSLEngine engine = c.createSSLEngine();
				                          params.setNeedClientAuth(false);
				                          params.setCipherSuites(engine.getEnabledCipherSuites());
				                          params.setProtocols(engine.getEnabledProtocols());
				                          // get the default parameters
				                          SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
				                          params.setSSLParameters(defaultSSLParameters);
				                 } catch (Exception ex) {
				                          ex.printStackTrace();
				                          System.out.println("Failed to create HTTPS server");
				                 }
				         }
				});
				noError = true;
			} catch (IOException e) {
				System.err.println("Failed to start HTTPS Server!");
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				System.err.println("Failed to start HTTPS Server!");
				e.printStackTrace();
			} catch (KeyStoreException e) {
				System.err.println("Failed to start HTTPS Server!");
				e.printStackTrace();
			} catch (CertificateException e) {
				System.err.println("Failed to start HTTPS Server!");
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				System.err.println("Failed to start HTTPS Server!");
				e.printStackTrace();
			} catch (KeyManagementException e) {
				System.err.println("Failed to start HTTPS Server!");
				e.printStackTrace();
			}
			if(noError) {
				server.createContext("/", new SecureContextHandler());
				server.setExecutor(Executors.newCachedThreadPool());
				server.start();
				System.out.println("Secure server started on port " + port);
			}
		}
	}

}
