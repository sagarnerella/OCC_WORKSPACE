
package com.osi.agent.webservicestub;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import com.osi.manager.common.CommonUtilities;
import com.osi.manager.dao.IManagerDao;
import com.osi.manager.dao.ManagerDao;
import com.osi.manager.snmp.adapter.SNMPManagerSender;
import com.osi.manager.vo.MSAgent;
import com.osi.manager.webservice.CallAgentWebService;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "CheckConnectionImplService", targetNamespace = "http://webservice.agent.osi.com/", wsdlLocation = "")
public class CheckConnectionImplService
    extends Service
{

    private final static URL CHECKCONNECTIONIMPLSERVICE_WSDL_LOCATION;
    private final static Logger LOGGER = Logger.getLogger(com.osi.agent.webservicestub.CheckConnectionImplService.class.getName());
    private static String ipAddres;
    static {
        URL url = null;
        String ipAddress="";
        try {
            URL baseUrl;
            baseUrl = com.osi.agent.webservicestub.CheckConnectionImplService.class.getResource(".");
           /* IManagerDao iManagerDao = null;
    			iManagerDao = (IManagerDao)new ManagerDao();
    			List<MSAgent> msAgentsList = iManagerDao.getAgentsList();
    			for (MSAgent msAgent : msAgentsList) {*/
    				ipAddress=CallAgentWebService.getIpAddress();//msAgent.getIpAddress();
            LOGGER.info("Manager url "+"http://"+CommonUtilities.getProperty("CONSUME_AGENT_PUBLISHED_WEBSERVICE_URL")+"/WS/SnmpTableDetails?wsdl");
            url = new URL(baseUrl, "http://"+ipAddress+":9000/WS/SnmpTableDetail?wsdl");
            LOGGER.info("url http://"+ipAddress+":9000/WS/SnmpTableDetail?wsdl");
    			//}
        } catch (MalformedURLException e) {
        	LOGGER.warning("Failed to create URL for the wsdl Location: 'http://"+ipAddres+":9000/WS/SnmpTableDetails?wsdl', retrying as a local file");
        	LOGGER.warning(e.getMessage());
        }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        CHECKCONNECTIONIMPLSERVICE_WSDL_LOCATION = url;
    }
    public static void setIpAddress(String ipAddress){
    	ipAddres=ipAddress;
    	LOGGER.info("static ipAddress "+ipAddress);
    }

    public CheckConnectionImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CheckConnectionImplService() {
        super(CHECKCONNECTIONIMPLSERVICE_WSDL_LOCATION, new QName("http://webservice.agent.osi.com/", "CheckConnectionImplService"));
    }

    /**
     * 
     * @return
     *     returns CheckConnectionIntf
     */
    @WebEndpoint(name = "CheckConnectionImplPort")
    public CheckConnectionIntf getCheckConnectionImplPort() {
        return super.getPort(new QName("http://webservice.agent.osi.com/", "CheckConnectionImplPort"), CheckConnectionIntf.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CheckConnectionIntf
     */
    @WebEndpoint(name = "CheckConnectionImplPort")
    public CheckConnectionIntf getCheckConnectionImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://webservice.agent.osi.com/", "CheckConnectionImplPort"), CheckConnectionIntf.class, features);
    }

}
