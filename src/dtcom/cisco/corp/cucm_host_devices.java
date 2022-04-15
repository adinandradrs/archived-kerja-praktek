package dtcom.cisco.corp;

import javax.telephony.Address;
import javax.telephony.Terminal;

public class cucm_host_devices {
    
    private Address address = null;
    private Terminal[] terminal = null;
    private String dialno = null;

    public void setAddress(Address address){
        this.address = address;
    }

    public Address getAddress(){
        return this.address;
    }

    public void setTerminal(Terminal[] terminal){
        this.terminal = terminal;
    }

    public Terminal[] getTerminal(){
        return this.terminal;
    }

    public void setDialno(String dialno){
        this.dialno = dialno;
    }

    public String getDialno(){
        return this.dialno;
    }
    
}
