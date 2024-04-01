/*
package io.nicheblog.dreamdiary.global.util;

import javax0.license3j.License;
import javax0.license3j.io.IOFormat;
import javax0.license3j.io.LicenseReader;
import net.bixsoft.deployment.DeploymentApplication;
import net.bixsoft.exception.error.CommonErrorCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

public class LicenseUtil {
	private static String serverName = null;
	private static License license = null;
	private static final Log log = LogFactory.getLog(DeploymentApplication.class);
	private static byte [] key = new byte[] {
			(byte)0x52,
			(byte)0x53, (byte)0x41, (byte)0x00, (byte)0x30, (byte)0x82, (byte)0x01, (byte)0x22, (byte)0x30,
			(byte)0x0D, (byte)0x06, (byte)0x09, (byte)0x2A, (byte)0x86, (byte)0x48, (byte)0x86, (byte)0xF7,
			(byte)0x0D, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x05, (byte)0x00, (byte)0x03, (byte)0x82,
			(byte)0x01, (byte)0x0F, (byte)0x00, (byte)0x30, (byte)0x82, (byte)0x01, (byte)0x0A, (byte)0x02,
			(byte)0x82, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x8B, (byte)0x01, (byte)0x5E, (byte)0x17,
			(byte)0xF5, (byte)0xA9, (byte)0x28, (byte)0x54, (byte)0x26, (byte)0x5C, (byte)0x6F, (byte)0x2F,
			(byte)0x70, (byte)0xC0, (byte)0x05, (byte)0xFB, (byte)0x9A, (byte)0xB8, (byte)0xF3, (byte)0xFF,
			(byte)0xCE, (byte)0x7A, (byte)0x01, (byte)0x8E, (byte)0x3A, (byte)0xE2, (byte)0x84, (byte)0x99,
			(byte)0xE6, (byte)0xEB, (byte)0x48, (byte)0xFA, (byte)0xAE, (byte)0xCC, (byte)0x90, (byte)0x0E,
			(byte)0xBF, (byte)0xB6, (byte)0x2D, (byte)0x8A, (byte)0x6B, (byte)0x14, (byte)0x99, (byte)0xDA,
			(byte)0x78, (byte)0xAC, (byte)0x2F, (byte)0x0E, (byte)0xDF, (byte)0xDD, (byte)0xC3, (byte)0xAC,
			(byte)0x94, (byte)0x5F, (byte)0x98, (byte)0xF5, (byte)0x2D, (byte)0xE2, (byte)0x0E, (byte)0x22,
			(byte)0xDE, (byte)0xB2, (byte)0xA2, (byte)0xB7, (byte)0x53, (byte)0xFC, (byte)0x8E, (byte)0x9C,
			(byte)0x2D, (byte)0x49, (byte)0xDA, (byte)0xCC, (byte)0x19, (byte)0xD8, (byte)0x7D, (byte)0x22,
			(byte)0xC0, (byte)0xC2, (byte)0x26, (byte)0x9D, (byte)0xE0, (byte)0xF4, (byte)0x3A, (byte)0x5D,
			(byte)0xE1, (byte)0xBA, (byte)0xBB, (byte)0xA0, (byte)0x58, (byte)0xAF, (byte)0x88, (byte)0x71,
			(byte)0x63, (byte)0xD2, (byte)0x12, (byte)0x9F, (byte)0x67, (byte)0x96, (byte)0xF4, (byte)0x05,
			(byte)0x56, (byte)0xBE, (byte)0xAA, (byte)0x52, (byte)0x75, (byte)0x66, (byte)0x8E, (byte)0xB9,
			(byte)0x6A, (byte)0x89, (byte)0x59, (byte)0xC1, (byte)0xAF, (byte)0xD1, (byte)0x02, (byte)0x75,
			(byte)0xFA, (byte)0x3A, (byte)0x9A, (byte)0x78, (byte)0x18, (byte)0x39, (byte)0xE1, (byte)0xEA,
			(byte)0x8C, (byte)0xD3, (byte)0xCC, (byte)0x6F, (byte)0x39, (byte)0xFC, (byte)0x84, (byte)0x43,
			(byte)0x64, (byte)0x26, (byte)0x8B, (byte)0xE4, (byte)0x32, (byte)0xEC, (byte)0x32, (byte)0x05,
			(byte)0xF3, (byte)0x9B, (byte)0x4A, (byte)0x4E, (byte)0xCB, (byte)0xF4, (byte)0x13, (byte)0xC7,
			(byte)0xF0, (byte)0x5A, (byte)0x77, (byte)0xDA, (byte)0x3F, (byte)0x3D, (byte)0x41, (byte)0xDC,
			(byte)0xBA, (byte)0xC9, (byte)0x58, (byte)0xA1, (byte)0xC7, (byte)0x85, (byte)0x7C, (byte)0xDB,
			(byte)0x86, (byte)0x96, (byte)0x1E, (byte)0x78, (byte)0xB9, (byte)0xE1, (byte)0x4A, (byte)0x56,
			(byte)0x91, (byte)0x7B, (byte)0xBE, (byte)0x6A, (byte)0xD6, (byte)0xD5, (byte)0x44, (byte)0x7B,
			(byte)0x44, (byte)0xC0, (byte)0xC2, (byte)0x22, (byte)0x3B, (byte)0x36, (byte)0x24, (byte)0xC2,
			(byte)0xD9, (byte)0xA0, (byte)0x71, (byte)0xE5, (byte)0xEF, (byte)0x59, (byte)0x56, (byte)0xA2,
			(byte)0x0B, (byte)0x3B, (byte)0x7F, (byte)0x36, (byte)0x96, (byte)0x23, (byte)0x38, (byte)0xEB,
			(byte)0x6A, (byte)0x36, (byte)0x32, (byte)0xB0, (byte)0xCA, (byte)0xF9, (byte)0x31, (byte)0xC2,
			(byte)0xB5, (byte)0xC5, (byte)0x6C, (byte)0x00, (byte)0xA5, (byte)0x9B, (byte)0x29, (byte)0x50,
			(byte)0x1E, (byte)0x28, (byte)0xD9, (byte)0x4C, (byte)0x3D, (byte)0x2D, (byte)0xD1, (byte)0x7B,
			(byte)0xC7, (byte)0xE9, (byte)0x8C, (byte)0x3B, (byte)0x85, (byte)0x44, (byte)0xF3, (byte)0x39,
			(byte)0x1B, (byte)0x9F, (byte)0x4E, (byte)0x9C, (byte)0xAE, (byte)0xD4, (byte)0x90, (byte)0x0F,
			(byte)0x3A, (byte)0x1B, (byte)0xD6, (byte)0x9D, (byte)0x4E, (byte)0x50, (byte)0x3C, (byte)0x61,
			(byte)0xCB, (byte)0x77, (byte)0x03, (byte)0x89, (byte)0x02, (byte)0x03, (byte)0x01, (byte)0x00,
			(byte)0x01,
	};	

	public synchronized  static License getInstance() throws LicenseException, IOException {
		if(license == null) {
			String serverAddress = DeploymentApplication.getApplicationContext().getEnvironment().getProperty("server.address");
			
			try (LicenseReader reader = new LicenseReader("license/license.lic")) {
			    license = reader.read(IOFormat.STRING);
			    if(!license.isOK(key)){
			    	System.err.println("Error license file");
			    	log.fatal("Error license file ");
	                throw new LicenseException(CommonErrorCode.LICENSE_FAILURE);
			    }
			    String authenticatedServer = license.get("Authenticated Server").valueString();
			    
			    if(!"unlimited".equals(authenticatedServer) && authenticatedServer != null) {
			    	if(serverAddress != null) {
				    	if(authenticatedServer.indexOf(serverAddress) < 0) {
					    	System.err.println("Server Address not matched");
					    	log.fatal("Server Address not matched");
			                throw new LicenseException(CommonErrorCode.SERVER_ADDRESS_NOT_MATCHED);
				    	}
			    	} else {
						Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
						boolean matched = false;
						for (NetworkInterface netint : Collections.list(nets)) {
							Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
							for (InetAddress inetAddress : Collections.list(inetAddresses)) {
								if(authenticatedServer.indexOf(inetAddress.getHostAddress()) > -1) {
									matched = true;
									break;
								}
							}
							if(matched) break;
						}			    		
						if(!matched) {
					    	System.err.println("Server Address not matched");
					    	log.fatal("Server Address not matched");
			                throw new LicenseException(CommonErrorCode.SERVER_ADDRESS_NOT_MATCHED);
						}
			    	}
			    }		    
			} catch (Exception e) {
				System.err.println("Error reading license file ");
                throw new LicenseException(CommonErrorCode.LICENSE_FAILURE);
			} 
			Timer timer = new Timer();
			TimerTask task = new FileWatcher( new File("license/license.lic")) {
				@Override
				protected void onChange(File file)  {
					try (LicenseReader reader = new LicenseReader("license/license.lic")) {
					    license = reader.read(IOFormat.STRING);
					    if(!license.isOK(key)){
					    	timer.cancel();
					    	log.fatal("Error license file ");
					    	System.err.println("Error license file ");
			                throw new LicenseException(CommonErrorCode.LICENSE_FAILURE);
					    }
					    if(license.isExpired()){
					    	timer.cancel();
					    	System.err.println("License expired");
					    	log.fatal("\"License expired");
			                throw new LicenseException(CommonErrorCode.LICENSE_FAILURE);
					    }			    
					} catch (Exception e) {
						timer.cancel();
						log.fatal("Error license file ");
						System.err.println("Error license file");
					} 
				}
				@Override
				protected void onTrigger()  {
					try {
					    if(license.isExpired()){
					    	timer.cancel();
					    	System.err.println("License expired");
					    	log.fatal("\"License expired");
			                throw new LicenseException(CommonErrorCode.LICENSE_FAILURE);
					    }			    
					} catch (Exception e) {
						timer.cancel();
						log.fatal("Error license file ");
						System.err.println("Error license file");
					}
				}
			};
		    timer.schedule(task , new Date(), 1000 * 60);			
		} else {
		    if(!license.isOK(key)){
		    	System.err.println("Error license file ");
		    	log.fatal("Error license file ");
                throw new LicenseException(CommonErrorCode.LICENSE_FAILURE);
		    }
		    if(license.isExpired()){
		    	System.err.println("License expired");
		    	log.fatal("\"License expired");
                throw new LicenseException(CommonErrorCode.LICENSE_FAILURE);
		    }
		}
		return license;
	}
	public synchronized  static String getString(String key) {
		if(license == null) { 
			try (LicenseReader reader = new LicenseReader("license/license.lic")) {
				License l = reader.read(IOFormat.STRING);
				return l.get(key).getString();
			} catch (Exception e) {
				log.fatal("Error license file ");
				System.err.println("Error license file");
			} 
		}
		return license.get(key).getString();
	}	
	public synchronized  static boolean isOK() {
		if(license == null) { 
			try {
				getInstance();
			} catch (Exception e) {
			}
			return false;
		}
		return true;
	}
	public static void setServerName(String serverName) throws LicenseException, IOException {
		synchronized (LicenseUtil.serverName) {
			LicenseUtil.serverName = serverName;
		}
	}
	public static String getServerName() {
		String str= LicenseUtil.serverName;
		return str;
	}
}
*/
