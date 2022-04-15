package dtcom.axl.corp;

public class cucm_host_axl_builder {
    
    /*
     * 
     * SOAP HEADER
     * 
     */
    protected String AXLSOAPRequest(String sAXLSOAPRequest, String authorization){
        sAXLSOAPRequest = "POST /axl/ HTTP/1.0\r\n";
        sAXLSOAPRequest += "Host:192.168.15.11:8443\r\n";
        sAXLSOAPRequest += "Authorization: Basic " + authorization + "\r\n";
        sAXLSOAPRequest += "Accept: text/*\r\n";
        sAXLSOAPRequest += "Content-type: text/xml\r\n";
        sAXLSOAPRequest += "SOAPAction: \"CUCM:DB ver=7.0\"\r\n";
        sAXLSOAPRequest += "Content-length: ";
        return sAXLSOAPRequest;
    }
    
    /*
     * 
     * XML AXL HEADER FOOTER
     * 
     */
    protected String init_header_axl(String XMLSoap){
        XMLSoap = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ";
        XMLSoap += "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
        XMLSoap += "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> ";
        XMLSoap += "<SOAP-ENV:Body>";
        return XMLSoap;
    }
    
    protected String init_footer_axl(String XMLSoap){
        XMLSoap += "</SOAP-ENV:Body> ";
        XMLSoap += "</SOAP-ENV:Envelope>";
        return XMLSoap;
    }
    
    /*
     * 
     * XML AXL COMMAND
     * 
     */
    
    protected  String init_cmd_list_phone(String XMLSoap, String param){
        XMLSoap += "     <axl:listPhoneByName ";
        XMLSoap += "         xmlns:axl=\"http://www.cisco.com/AXL/1.0\" ";
        XMLSoap += "         xsi:schemaLocation=\"http://www.cisco.com/AXL/1.0 http://ccmserver/schema/axlsoap.xsd\" ";
        XMLSoap += "         sequence=\"9\"> ";
        XMLSoap += "     <searchString>%"+param+"%</searchString> ";
        XMLSoap += "     </axl:listPhoneByName> ";
        return XMLSoap;
    }
    
    protected String init_cmd_update_display(String XMLSoap, String term, String ext,String display, String label, String desc){
        XMLSoap += "     <axl:updatePhone";
        XMLSoap += "         xmlns:axl=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "         xsi:schemaLocation=\"http://www.cisco.com/AXL/API/8.0 ";
        XMLSoap += "         http://ccmserver/schema/axlsoap.xsd\" ";
        XMLSoap += "         sequence=\"1234\"> ";
        XMLSoap += "     <name>"+term+"</name> ";
        XMLSoap += "     <lines><line><index>1</index><dirn><pattern>"+ext+"</pattern></dirn>";
        if (!display.equals(""))    
            XMLSoap += "     <display>"+display+"</display> ";
        if (!label.equals(""))
            XMLSoap += "     <label>"+label+"</label>"; 
        if (!display.equals("") || !label.equals(""))
            XMLSoap += "     </line></lines>"; 
        if (!desc.equals(""))
            XMLSoap += "     <description>"+desc+"</description>";
        XMLSoap += "     </axl:updatePhone> ";
        return XMLSoap;
    }
    
    protected String init_cmd_update_dnd(String XMLSoap,String term, String status, String option){
        XMLSoap += "    <axl:updatePhone";
        XMLSoap += "        xmlns:axl=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "        xsi:schemaLocation=\"http://www.cisco.com/AXL/API/8.0 ";
        XMLSoap += "        http://ccmserver/schema/axlsoap.xsd\" ";
        XMLSoap += "        sequence=\"1234\"> ";
        XMLSoap += "    <name>"+term+"</name> ";
        XMLSoap += "    <dndStatus>"+status+"</dndStatus>";
        if(!option.equals(""))
            XMLSoap += "    <dndOption>"+option+"</dndOption>";
        XMLSoap += "    </axl:updatePhone> ";
        return XMLSoap;
    }
    
    protected String init_cmd_add_fac(String XMLSoap,String desc,String code,String authlevel){
        XMLSoap += "    <axl:addFACInfo";
        XMLSoap += "        xmlns:axl=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "        xsi:schemaLocation=\"http://www.cisco.com/AXL/API/8.0 ";
        XMLSoap += "        http://ccmserver/schema/axlsoap.xsd\" ";
        XMLSoap += "        sequence=\"1234\"> ";
        XMLSoap += "    <name>"+desc+"</name>";
        XMLSoap += "    <code>"+code+"</code> ";
        XMLSoap += "    <authorizationLevel>"+authlevel+"</authorizationLevel>";
        XMLSoap += "    </axl:addFACInfo> ";
        return XMLSoap;
    }
    
    protected String init_cmd_upd_fac(String XMLSoap,int level,String nname,String ncode ,String code){
        XMLSoap += "     <axlapi:executeSQLUpdate ";
        XMLSoap += "         xmlns:axlapi=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "         xmlns:axl=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
        XMLSoap += "         xsi:schemaLocation=\"http://www.cisco.com/AXL/API/8.0 ";
        XMLSoap += "         axlsoap.xsd\" ";
        XMLSoap += "         sequence=\"1234\"> ";
        XMLSoap += "     <sql>update facinfo set "; 
        if(level >=0)
            XMLSoap += "                authorizationlevel="+level;
        if(!nname.equals(""))
            XMLSoap += "                ,name='"+nname+"'";
        if(!code.equals(""))
            XMLSoap += "                ,code='"+ncode+"'";
        XMLSoap += "                where code="+code+";";
        XMLSoap += "     </sql> ";
        XMLSoap += "     </axlapi:executeSQLUpdate> ";
        return XMLSoap;
    }
    
    protected String init_cmd_rem_fac(String XMLSoap, String desc, String pin){
        XMLSoap += "     <axlapi:executeSQLUpdate ";
        XMLSoap += "         xmlns:axlapi=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "         xmlns:axl=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
        XMLSoap += "         xsi:schemaLocation=\"http://www.cisco.com/AXL/API/8.0 ";
        XMLSoap += "         axlsoap.xsd\" ";
        XMLSoap += "         sequence=\"1234\"> ";
        XMLSoap += "     <sql>delete from facinfo where name='";
        XMLSoap +=              desc +"'";
        XMLSoap += "          and code=";
        XMLSoap += pin;
        XMLSoap += "     </sql> ";
        XMLSoap += "     </axlapi:executeSQLUpdate> ";
        return XMLSoap;
    }
    
    protected String init_cmd_add_ext(String XMLSoap, String pattern){
        XMLSoap += "    <axl:addLine";
        XMLSoap += "        xmlns:axl=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "        xsi:schemaLocation=\"http://www.cisco.com/AXL/API/8.0 ";
        XMLSoap += "        http://ccmserver/schema/axlsoap.xsd\" ";
        XMLSoap += "        sequence=\"1234\"> ";
        XMLSoap += "    <pattern>"+pattern+"</pattern> ";
        XMLSoap += "    </axl:addLine> ";
        return XMLSoap;
    }
    
    protected String init_cmd_list_css(String XMLSoap){
        XMLSoap += "    <axlapi:executeSQLQuery ";
        XMLSoap += "        xmlns:axlapi=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "        xmlns:axl=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
        XMLSoap += "        xsi:schemaLocation=\"http://www.cisco.com/AXL/API/8.0 ";
        XMLSoap += "        axlsoap.xsd\" ";
        XMLSoap += "        sequence=\"1234\"> ";
        XMLSoap += "    <sql>select name from callingsearchspace</sql> ";
        XMLSoap += "    </axlapi:executeSQLQuery>";
        return XMLSoap;
    }
    
    protected String init_cmd_upd_css(String XMLSoap,String term,String css){
        XMLSoap += "    <axl:updatePhone";
        XMLSoap += "        xmlns:axl=\"http://www.cisco.com/AXL/API/8.0\" ";
        XMLSoap += "        xsi:schemaLocation=\"http://www.cisco.com/AXL/API/8.0 ";
        XMLSoap += "        http://ccmserver/schema/axlsoap.xsd\" ";
        XMLSoap += "        sequence=\"1234\"> ";
        XMLSoap += "    <name>"+term+"</name> ";
        XMLSoap += "    <callingSearchSpaceName>"+css+"</callingSearchSpaceName>";
        XMLSoap += "    </axl:updatePhone>";
        return XMLSoap;
    }
    
}
