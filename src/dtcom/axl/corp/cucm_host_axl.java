package dtcom.axl.corp;

import dtcom.util.corp.cucm_ini_configuration;
import dtcom.util.corp.cucm_log_maker;
import dtcom.util.corp.cucm_xml_rw;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class cucm_host_axl extends cucm_host_axl_builder {
    
    private byte[] bArray = null;
    private Socket socket = null;
    private OutputStream out = null;
    private InputStream in = null;
    private String xmlresponse = null;
    private String authorization = "";
    private cucm_log_maker clm = new cucm_log_maker();
    private String the_response = "";
    private String the_xml = "";
    
    public cucm_host_axl(){
        String admin = new cucm_ini_configuration().get_ini().get("webservice", "login");
        String pass = new cucm_ini_configuration().get_ini().get("webservice", "passwd");
        authorization = admin + ":" + pass;
    }
    
    public String get_response(){
        return the_response;
    }
    /*
     * 
     * LIST COMMAND PACKAGE 
     * 
     */
    public void list_phone_by_term_name(String search_string){
        //build xml
        String sAXLSOAPRequest = "";
        String sAXLRequest = null;
        authorization = new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        String SOAPPost = this.AXLSOAPRequest(sAXLSOAPRequest, authorization);
        String xml = this.init_header_axl(sAXLRequest);
        xml += this.init_cmd_list_phone(sAXLRequest, search_string);
        xml += this.init_footer_axl(sAXLRequest);
        SOAPPost += xml.length();
        SOAPPost += "\r\n\r\n";
        SOAPPost += xml;
        //send to server
        this.send_to_cucm(SOAPPost);
        clm.create_log("server using SOAP list_phone_by_term_name", "srv >> ccm", "act", true);
    }
    
    public void update_display_name(String term,String ext,String display, String label, String desc){
        //build xml
        String sAXLSOAPRequest = "";
        String sAXLRequest = null;
        authorization = new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        String SOAPPost = this.AXLSOAPRequest(sAXLSOAPRequest, authorization);
        String xml = this.init_header_axl(sAXLRequest);
        xml += this.init_cmd_update_display(sAXLSOAPRequest, term, ext, display, label, desc);
        xml += this.init_footer_axl(sAXLRequest);
        SOAPPost += xml.length();
        SOAPPost += "\r\n\r\n";
        SOAPPost += xml;
        //send to server
        this.send_to_cucm(SOAPPost);
        clm.create_log("server using SOAP update_display_name", "srv >> ccm", "act", true);
        System.out.println(term);
        String response = new cucm_xml_rw().single_axl_val("xmlresponse", "axl:Error", "axl:code");
        if(response.equals("5007"))
            the_response = "Fault|Ext not found";
        else 
            the_response = "Success";
        the_xml = xml;
        clm.create_log("server adding xml : " + the_xml, "", "log", true);
    }   
    
    public void update_dnd(String term,String status, String option){
        //build xml
        String sAXLSOAPRequest = "";
        String sAXLRequest = null;
        authorization = new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        String SOAPPost = this.AXLSOAPRequest(sAXLSOAPRequest, authorization);
        String xml = this.init_header_axl(sAXLRequest);
        xml += this.init_cmd_update_dnd(sAXLSOAPRequest, term, status, option);
        xml += this.init_footer_axl(sAXLRequest);
        SOAPPost += xml.length();
        SOAPPost += "\r\n\r\n";
        SOAPPost += xml;
        //send to server
        this.send_to_cucm(SOAPPost);
        clm.create_log("server using SOAP update_dnd", "srv >> ccm", "act", true);
        String response = new cucm_xml_rw().single_axl_val("xmlresponse", "axl:Error", "axl:code");
        if(response.equals("5007"))
            the_response = "Fault|Ext not found";
        else 
            the_response = "Success";
        the_xml = xml;
        System.out.println(term);
        System.out.println(the_response);
        clm.create_log("server adding xml : " + the_xml, "", "log", true);
    }
    
    public void add_fac(String code,String desc, String authlevel){
        //build xml
        String sAXLSOAPRequest = "";
        String sAXLRequest = null;
        authorization = new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        String SOAPPost = this.AXLSOAPRequest(sAXLSOAPRequest, authorization);
        String xml = this.init_header_axl(sAXLRequest);
        xml += this.init_cmd_add_fac(sAXLSOAPRequest, desc, code, authlevel);
        xml += this.init_footer_axl(sAXLRequest);
        SOAPPost += xml.length();
        SOAPPost += "\r\n\r\n";
        SOAPPost += xml;
        //send to server
        this.send_to_cucm(SOAPPost);
        clm.create_log("server using SOAP add_fac", "srv >> ccm", "act", true);
        String response = new cucm_xml_rw().single_axl_val("xmlresponse", "axl:Error", "axl:code");
        if(response.equals("-239"))
            the_response = "Fault|Already Exist";
        else 
            the_response = "Success";
        the_xml = xml;
        clm.create_log("server adding xml : " + the_xml, "", "log", true);
    }
    
    public void update_fac(int level, String newname, String newcode,String code){
        //build xml
        String sAXLSOAPRequest = "";
        String sAXLRequest = null;
        authorization = new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        String SOAPPost = this.AXLSOAPRequest(sAXLSOAPRequest, authorization);
        String xml = this.init_header_axl(sAXLRequest);
        xml += this.init_cmd_upd_fac(sAXLSOAPRequest,level,newname,newcode, code);
        xml += this.init_footer_axl(sAXLRequest);
        SOAPPost += xml.length();
        SOAPPost += "\r\n\r\n";
        SOAPPost += xml;
        //send to server
        this.send_to_cucm(SOAPPost);
        clm.create_log("server using SOAP update_fac", "srv >> ccm", "act", true);
        //
        
        String response = new cucm_xml_rw().single_axl_val("xmlresponse", "return", "rowsUpdated");
        if(response.equals("0"))
            the_response = "Fault|FAC not found";
        else 
            the_response = "Success";
        the_xml = xml;
        clm.create_log("server adding xml : " + the_xml, "", "log", true);
    }
    
    public void rem_fac(String desc,String pin){
        //build xml
        String sAXLSOAPRequest = "";
        String sAXLRequest = null;
        authorization = new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        String SOAPPost = this.AXLSOAPRequest(sAXLSOAPRequest, authorization);
        String xml = this.init_header_axl(sAXLRequest);
        xml += this.init_cmd_rem_fac(sAXLSOAPRequest, desc, pin);
        xml += this.init_footer_axl(sAXLRequest);
        SOAPPost += xml.length();
        SOAPPost += "\r\n\r\n";
        SOAPPost += xml;
        //send to server
        this.send_to_cucm(SOAPPost);
        clm.create_log("server using SOAP add_fac : " + pin + " where user : " + desc, "srv >> ccm", "act", true);
        String response = new cucm_xml_rw().single_axl_val("xmlresponse", "return", "rowsUpdated");
        if(response.equals("0"))
            the_response = "Fault|FAC not found";
        else 
            the_response = "Success";
        the_xml = xml;
        clm.create_log("server adding xml : " + the_xml, "", "log", true);
    }
    
    public void add_ext(String pattern){
        //build xml
        String sAXLSOAPRequest = "";
        String sAXLRequest = null;
        authorization = new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        String SOAPPost = this.AXLSOAPRequest(sAXLSOAPRequest, authorization);
        String xml = this.init_header_axl(sAXLRequest);
        xml += this.init_cmd_add_ext(sAXLSOAPRequest, pattern);
        xml += this.init_footer_axl(sAXLRequest);
        SOAPPost += xml.length();
        SOAPPost += "\r\n\r\n";
        SOAPPost += xml;
        //send to server
        this.send_to_cucm(SOAPPost);
        clm.create_log("server using SOAP add_ext : " + pattern, "srv >> ccm", "act", true);
        String response = new cucm_xml_rw().single_axl_val("xmlresponse", "axl:Error", "axl:code");
        if(response.equals("4052"))
            the_response = "Fault|Extension Already Exist";
        else if(response.equals("25068"))
            the_response = "Fault|Not Valid Character";
        else 
            the_response = "Success";
        the_xml = xml;
        clm.create_log("server adding xml : " + the_xml, "", "log", true);
    }
    
    public void list_css(){
        String sAXLSOAPRequest = "";
        String sAXLRequest = null;
        authorization = new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        String SOAPPost = this.AXLSOAPRequest(sAXLSOAPRequest, authorization);
        String xml = this.init_header_axl(sAXLRequest);
        xml += this.init_cmd_list_css(sAXLSOAPRequest);
        xml += this.init_footer_axl(sAXLRequest);
        SOAPPost += xml.length();
        SOAPPost += "\r\n\r\n";
        SOAPPost += xml;
        //send to server
        this.send_to_cucm(SOAPPost);
        //write to xml file
        new cucm_xml_rw().xml_write("xmlcss", xmlresponse);
        clm.create_log("server using SOAP list_css", "srv >> ccm", "act", true);
        the_xml = xml;
        clm.create_log("server adding xml : " + the_xml, "", "log", true);
    }
    
    public void update_css(String terminal, String css){
        //build xml
        String sAXLSOAPRequest = "";
        String sAXLRequest = null;
        authorization = new sun.misc.BASE64Encoder().encode(authorization.getBytes());
        String SOAPPost = this.AXLSOAPRequest(sAXLSOAPRequest, authorization);
        String xml = this.init_header_axl(sAXLRequest);
        xml += this.init_cmd_upd_css(sAXLSOAPRequest, terminal, css);
        xml += this.init_footer_axl(sAXLRequest);
        SOAPPost += xml.length();
        SOAPPost += "\r\n\r\n";
        SOAPPost += xml;
        //send to server
        this.send_to_cucm(SOAPPost);
        clm.create_log("server is update css : " + css, "srv >> ccm", "act", true);
        String response = new cucm_xml_rw().single_axl_val("xmlresponse", "axl:Error", "axl:code");
        if(response.equals("5007"))
            the_response = "Fault|Ext not found";
        else 
            the_response = "Success";
//        int error = new cucm_xml_rw().list_axl_val("xmlresponse", "axl:Error", "axl:code").size();
//        if(error>0)
//            the_response = "Fault|Ext not found";
//        else
//            the_response = "Success";
        the_xml = xml;
        clm.create_log("server adding xml : " + the_xml, "", "log", true);
    }
    
    /*
     * 
     * SEND SOAP TO CUCM WEB SERVER
     * 
     */
    private void send_to_cucm(String SOAPPost){
        try {
            String ipaddress = new cucm_ini_configuration().get_ini().get("provider", "ipaddress");
            String port = new cucm_ini_configuration().get_ini().get("provider", "port");
            cucm_host_axl cha = new cucm_host_axl();
            X509TrustManager xtm = cha.new MyTrustManager();
            TrustManager[] mytm = { xtm };
            SSLContext ctx = SSLContext.getInstance("SSL");
            ctx.init(null, mytm, null);
            SSLSocketFactory sslFact = (SSLSocketFactory) ctx.getSocketFactory();
            socket = (SSLSocket) sslFact.createSocket(ipaddress, Integer.parseInt(port));
            in = socket.getInputStream();
            StringBuilder sb = new StringBuilder(2048);
            bArray = new byte[2048];
            int ch = 0;
            int sum = 0;
            out = socket.getOutputStream();
            out.write(SOAPPost.getBytes());
            while ((ch = in.read(bArray)) != -1) {
               sum += ch;
               sb.append(new String(bArray, 0, ch));
            }
            socket.close();
            new cucm_log_maker().create_log("server is sending SOAP Envelope", "srv >> ccm", "act", true);
            cucm_xml_rw cxr =  new cucm_xml_rw();
            xmlresponse = sb.toString();
            cxr.xml_write("xmlresponse", xmlresponse);
            clm.create_log("cucm response : " + cxr.getSoap_response(), "", "log", true);
        } catch (Exception e) {
            System.out.println(e);
        }
        finally{
           try {
               if (socket != null)
                   socket.close();
           } catch (Exception exc) {
               System.out.println(exc);
           }
        }
    }
    
    /*
     *
     * SSL HTTPS CERTIFIED
     * 
     */
    public class MyTrustManager implements X509TrustManager {
        
        MyTrustManager() {
            
        }
        
        public void checkClientTrusted(X509Certificate chain[], String authType)
            throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate chain[], String authType) throws CertificateException {
        
        }
        
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    
    
}
