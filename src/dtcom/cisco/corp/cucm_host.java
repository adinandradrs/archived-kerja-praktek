package dtcom.cisco.corp;

import javax.telephony.Provider;
import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import dtcom.util.corp.cucm_ini_configuration;
import dtcom.util.corp.cucm_log_maker;
import org.ini4j.Wini;

public class cucm_host {
    
    private Provider provider;
    private final cucm_log_maker clm = new cucm_log_maker();
    
    /*
     * 
     * CONSTRUCTOR CONNECT TO CUCM, CFGFILE IS THE LOCATION OF *.INI FILE
     * 
     * */
    public cucm_host(){
        String address= "";
        Wini ini = null;
        try{
            ini = new cucm_ini_configuration().get_ini();      
        }
        catch(Exception ex){
            System.out.println(ex);
        }
        try{
            JtapiPeer peer = JtapiPeerFactory.getJtapiPeer("");
            address = "";
            address += ini.get("provider","ipaddress");
            address += ";login=";
            address += ini.get("provider","login");
            address += ";passwd=";
            address += ini.get("provider","passwd");
            provider = peer.getProvider(address);
            String content = "get provider : success; " + address;
            clm.create_log(content, "srv >> ccm", "act", true);
        }
        catch(Exception ex){
            System.out.println(ex);
            try{
                clm.create_log("Get Provider : Failed; " + address, "srv >> ccm", "err", true);
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }

    /*
     * 
     * RETURN THE CONNECTION
     * 
     * */
    public Provider get_provider(){
        return provider;
    }
    
}
