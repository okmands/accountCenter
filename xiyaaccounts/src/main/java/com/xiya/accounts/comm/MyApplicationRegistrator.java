package com.xiya.accounts.comm;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.yaml.snakeyaml.Yaml;

import de.codecentric.boot.admin.client.registration.Application;
import de.codecentric.boot.admin.client.registration.ApplicationFactory;
import de.codecentric.boot.admin.client.registration.ApplicationRegistrator;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import javax.management.Query;

@Configuration
public class MyApplicationRegistrator implements ApplicationRegistrator {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyApplicationRegistrator.class);

	private final ConcurrentHashMap<String, LongAdder> attempts = new ConcurrentHashMap<>();

	private final AtomicReference<String> registeredId = new AtomicReference<>();

	@SuppressWarnings("unused")
	private final ApplicationFactory applicationFactory;

	private final String[] adminUrls;

	private final boolean registerOnce;

	private final RegistrationClient registrationClient;

	private final String projectname;
	private final Object client;
	private final String url;
	private final Object instance;
	private final String instance_name;
	private final String instance_service_base_url_orgin;
	private String instance_service_base_url;
	private String localip;
	private String localport;
	
	@EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
		//springboot直接运行
		localport = String.valueOf(event.getWebServer().getPort());
		if(instance_service_base_url_orgin.substring(instance_service_base_url_orgin.length() - 1).equals("/")) {
			instance_service_base_url = instance_service_base_url_orgin.substring(0, instance_service_base_url_orgin.length() - 1);
		}
		instance_service_base_url = instance_service_base_url.replace("${localhost}", localip).replace("${server.port}", localport);
    }
    
	//获取本机IP地址
	public static String getIpAddress() {
	    try {
	      Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
	      InetAddress ip = null;
	      while (allNetInterfaces.hasMoreElements()) {
	        NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
	        if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
	          continue;
	        } else {
	          Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
	          while (addresses.hasMoreElements()) {
	            ip = addresses.nextElement();
	            if (ip != null && ip instanceof Inet4Address) {
	              return ip.getHostAddress();
	            }
	          }
	        }
	      }
	    } catch (Exception e) {
	    }
	    return "";
	  }

	//获取tomcat的服务端口
    private int getTomcatPort() {
		try {
			MBeanServer server;
			if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
				server = MBeanServerFactory.findMBeanServer(null).get(0);
			} else {
				return -1;
			}

			Set<?> names = server.queryNames(new ObjectName("Catalina:type=Connector,*"),
					Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));

			Iterator<?> iterator = names.iterator();
			if (iterator.hasNext()) {
				ObjectName name = (ObjectName) iterator.next();
				return Integer.parseInt(server.getAttribute(name, "port").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
    }

	@SuppressWarnings("unchecked")
	public MyApplicationRegistrator(ApplicationFactory applicationFactory, RegistrationClient registrationClient, String[] adminUrls
			) throws IOException {
		// TODO Auto-generated constructor stub
		this.applicationFactory = applicationFactory;
		this.registerOnce = true;
		this.registrationClient = registrationClient;
		Yaml yaml = new Yaml();
		InputStream in = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream("application.yml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
		}
		Map<String, Object> map = yaml.loadAs(in, Map.class);
		in.close();
		projectname =
			((Map<String, Object>)(
			((Map<String, Object>)
				map.get("spring"))
				.get("application")))
				.get("name").toString();
		client =
			((Map<String, Object>)(
			((Map<String, Object>)(
			((Map<String, Object>)
				map.get("spring"))
				.get("boot")))
				.get("admin")))
				.get("client");
		url = ((Map<String, Object>)client).get("url").toString();
		this.adminUrls = new String[]{this.url + "/instances"};
		instance = ((Map<String, Object>)client).get("instance");
		instance_name = ((Map<String, Object>)instance).get("name").toString();
		localip = InetAddress.getByName(InetAddress.getLocalHost().getHostName()).getHostAddress();
		if(localip.equals("127.0.0.1")) {
			localip = getIpAddress();
		}
		
		instance_service_base_url_orgin = ((Map<String, Object>)instance).get("service-base-url").toString();
		int tomcatPort = getTomcatPort();
		//通过tomcat加载运行
		if(tomcatPort != -1) {
			localport = String.valueOf(tomcatPort);
			instance_service_base_url = instance_service_base_url_orgin + (instance_service_base_url_orgin.substring(instance_service_base_url_orgin.length() - 1).equals("/") ? "" : "/") + projectname;
			instance_service_base_url = instance_service_base_url.replace("${localhost}", localip).replace("${server.port}", localport);
		}
	}

	/**
	 * Registers the client application at spring-boot-admin-server.
	 * @return true if successful registration on at least one admin server
	 */
	@Override
	public boolean register() {
		Application application = Application
			.create(this.instance_name)
			.serviceUrl(this.instance_service_base_url)
			.managementUrl(this.instance_service_base_url + "/actuator")
			.healthUrl(this.instance_service_base_url + "/actuator/health")
			.build();

		boolean isRegistrationSuccessful = false;

		for (String adminUrl : this.adminUrls) {
			LongAdder attempt = this.attempts.computeIfAbsent(adminUrl, (k) -> new LongAdder());
			boolean successful = register(application, adminUrl, attempt.intValue() == 0);

			if (!successful) {
				attempt.increment();
			}
			else {
				attempt.reset();
				isRegistrationSuccessful = true;
				if (this.registerOnce) {
					break;
				}
			}
		}

		return isRegistrationSuccessful;
	}

	protected boolean register(Application application, String adminUrl, boolean firstAttempt) {
		try {
			String id = this.registrationClient.register(adminUrl, application);
			if (this.registeredId.compareAndSet(null, id)) {
				LOGGER.info("Application registered itself as {}", id);
			}
			else {
				LOGGER.debug("Application refreshed itself as {}", id);
			}
			return true;
		}
		catch (Exception ex) {
			if (firstAttempt) {
				LOGGER.warn(
						"Failed to register application as {} at spring-boot-admin ({}): {}. Further attempts are logged on DEBUG level",
						application, this.adminUrls, ex.getMessage());
			}
			else {
				LOGGER.debug("Failed to register application as {} at spring-boot-admin ({}): {}", application,
						this.adminUrls, ex.getMessage());
			}
			return false;
		}
	}

	@Override
	public void deregister() {
		String id = this.registeredId.get();
		if (id == null) {
			return;
		}

		for (String adminUrl : this.adminUrls) {
			try {
				this.registrationClient.deregister(adminUrl, id);
				this.registeredId.compareAndSet(id, null);
				if (this.registerOnce) {
					break;
				}
			}
			catch (Exception ex) {
				LOGGER.warn("Failed to deregister application (id={}) at spring-boot-admin ({}): {}", id, adminUrl,
						ex.getMessage());
			}
		}
	}

	@Override
	public String getRegisteredId() {
		return this.registeredId.get();
	}

}
