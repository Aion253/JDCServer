# JDCServer

A Java web server designed to generate dynamic pages with minimal application overhead.


## Installation

Download the latest [release](https://github.com/Aion253/JDCServer/releases/latest) of JDC Server to an empty folder.

Use your system's command line to start JDC.
```bash
java -jar jdcserver.jar
```

### Setup
After creating the necessary configuration files, including an empty website, ```default```, the application will crash. This behavior is, for now, intended but should not persist in future releases. The default website is intended only to illustrate how to set up your own, but you can use it by removing the processor definition in the ```processors.json``` file in the ```/websites/default/``` directory.
```
{"processors": []}
```

### Known Issues
A few other issues may also cause JDC to crash during startup. 

**Address Already In Use**

If you're already running another server, such as Apache HTTP Server, the default ports ```80``` and ```443``` may not be available, consider stopping the conflicting application or changing the ports used by JDC in ```config.json```.

**JVM Brotli**

Brotli compression is enabled by default in JDC's ```config.json``` file. The library being used to support it, however, does not work on Java versions below 8. If you're using an earlier Java version you'll need to upgrade or disable brotli compression.

### What's next?
Create your own websites of the same structure and ship your own content processors for them with [JDCLib](https://github.com/Aion253/JDCLib).

---
## Java KeyStore (HTTPS)

While JDC Server supports HTTPS, it requires some set up to get to that point.

JDC uses Java KeyStore (JKS) certificate files to encrypt content, so we'll need to make one.

To generate a compatible JKS certificate it is recommended to get valid cert/key files from a certificate authority
and use OpenSSL to convert them to a PKCS12 certificate. After that use the Java Keytool to convert the PKCS12 to a JKS
certificate.

```bash
openssl pkcs12 -export -in <certfile> -inkey <keyfile> -out <keystorefile> -name <alias> -CAfile <cacertfile> -caname root
keytool -importkeystore -deststorepass <keystorepass> -destkeypass <keystorepass> -destkeystore <jksfile> -srckeystore <keystorefile> -srcstoretype PKCS12 -srcstorepass <keystorepass> -alias <alias>
```

JDC will only generate the ```certs.json``` configuration file and directory if at least one active website has it's ```ssl_enabled``` flag in ```sites.json``` set to ```true```. The certificate file generated by the commands above should be saved in the ```certs``` folder. And the ```certs.json``` file should be modified to use this JKS file correctly.

```json
{
  "enable_ssl_server": true,
  "jks_certificate": "jksfile.jks",
  "store_password": "keystorepass",
  "key_password": "keystorepass",
  "cert_alias": "alias"
}
```

It is safe to delete the PKCS12 file that was used during this process but not recommend to delete either the cert or key file provided by your certificate authority as some will not allow you to receive them multiple times.

### Known Issues
A few problems may arise during this process or with certificate validity in testing.

**OpenSSL**
Each of the files required for the PKCS12 export are not normally provided to certificate holders. Namely you will likely have to find the Certificate Authority's certificate file on your own.

Also make sure that your alias does not cause a file system naming conflict or match the CA name, which can be root, for safety.

**KeyTool**
For JDC's certificate management to work the store and key password for the destination JKS file must match exactly.

The password for the source keystore should be used if one was set though the instructions above do not. If one was not set anything can be passed after the ```srcstorepass``` argument, or it can be removed.

Ensure the alias matches exactly to the one used in creating the PKCS12 file.

**File Types**
Make sure you're including the file type at the end of all file arguments in the above commands. The don't inherently have to match to the file type of their contents, though they should, and OpenSSL and KeyTool will not assume they do.

**Invalid Certificate**
Certificates that were generated locally or as part of a certificate chain may fail at different times for different reasons.

Certificates that are locally generated, while they will securely encrypt traffic, are not trusted by end-user's browsers. A valid certificate authority must sign the issues certificate otherwise most major browsers will alert the user that their connection is not secure.

Certificates generated as part of a certificate chain require that the entire chain, regardless of the number of intermediate certificates, be complete to be valid. The root chain on your system will be able to securely encrypt traffic, but is not trusted by end-user's browsers. This problem is especially prevalent for users accessing locally without routing through a network like Cloudflare, which retains a portion of their attached websites certificate chain.

These problems can simply be ignored during development by expanding a browser's error message and accepting that the content may not be secure, although in this case it is.
