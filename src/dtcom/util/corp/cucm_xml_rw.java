package dtcom.util.corp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class cucm_xml_rw {
    
    private String soap_response;
    
    public String getSoap_response(){
        return this.soap_response;
    }
    
    /*
     * 
     * WRITE AXL SOAP/XML TEXT RESPONSE TO XML FILE
     * 
     */
    public void xml_write(String filename,String xmlresponse){
        int drop = 0;
        for (int i = 0; i < xmlresponse.length(); i++) {
            if(xmlresponse.charAt(i)=='<'){
                drop = i;
                break;
            }
        }
        try {
            xmlresponse = xmlresponse.substring(drop);
            FileWriter fstream = new FileWriter(filename+".xml");
            BufferedWriter outs = new BufferedWriter(fstream);
            outs.write(xmlresponse);
            outs.close();
            soap_response = xmlresponse;
            new cucm_log_maker().create_log("server is writing the xml response", "srv >> ccm", "act", true);
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }
    
    /*
     * 
     * READ VALUE FROM AXL SOAP/XML TAG <axl:xxx>(value)</axl:xxx> IN A LIST
     * 
     */
    public List<String>list_axl_val(String filename,String partag, String chtag){
        List<String>xml_val = new ArrayList<String>();
        try {
            File xml_file = new File(filename+".xml");
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(xml_file);
            doc.getDocumentElement().normalize();
            NodeList node = doc.getElementsByTagName(partag);
            for (int temp = 0; temp < node.getLength(); temp++) {
               Node chnodetag = node.item(temp);
               if (chnodetag.getNodeType() == Node.ELEMENT_NODE) {
                  Element element = (Element) chnodetag;
                  xml_val.add(get_tag_value(chtag, element));
               }
               else
                   xml_val.add("");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml_val;
    }
    
    /*
     * 
     * READ VALUE FROM AXL SOAP/XML TAG <axl:xxx>(value)</axl:xxx> IN A SINGLE VALUE
     * 
     */
    public String single_axl_val(String filename,String partag, String chtag){
        String xml_val = "";
        try {
            File xml_file = new File(filename+".xml");
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
            Document doc = dbuilder.parse(xml_file);
            doc.getDocumentElement().normalize();
            NodeList node = doc.getElementsByTagName(partag);
            for (int temp = 0; temp < node.getLength(); temp++) {
               Node chnodetag = node.item(temp);
               if (chnodetag.getNodeType() == Node.ELEMENT_NODE) {
                  Element element = (Element) chnodetag;
                  xml_val = get_tag_value(chtag, element);
               }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml_val;
    }
    
    /*
     * 
     * XML FUNCTION TO RETURN VALUE TAG
     * 
     */
    private String get_tag_value(String tag, Element element) {
	NodeList nodetag = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node) nodetag .item(0);
	return value.getNodeValue();
    }
    
}
