package dtcom.cisco.corp;

import com.cisco.cti.util.Condition;
import com.cisco.jtapi.extensions.CiscoCall;
import com.cisco.jtapi.extensions.CiscoConnection;
import com.cisco.jtapi.extensions.CiscoTerminal;
import dtcom.util.corp.cucm_log_maker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.telephony.Address;
import javax.telephony.Call;
import javax.telephony.CallObserver;
import javax.telephony.Provider;
import javax.telephony.ProviderObserver;
import javax.telephony.Terminal;
import javax.telephony.TerminalConnection;
import javax.telephony.callcontrol.CallControlCall;
import javax.telephony.events.CallEv;
import javax.telephony.events.ProvEv;
import javax.telephony.events.ProvInServiceEv;

public class cucm_host_devices_access {
    
    private List<cucm_host_devices> device_list = null;
    private cucm_log_maker clm = new cucm_log_maker();
    
    private void collect_device(){
        cucm_host cn = new cucm_host();
        Provider provider = cn.get_provider();
        device_list = new ArrayList<cucm_host_devices>();
        try {
            Thread.sleep(500);
            for (int i = 0; i < provider.getTerminals().length; i++) {
                cucm_host_devices device = new cucm_host_devices();
                device.setAddress(provider.getAddresses()[i]);
                device.setTerminal(provider.getTerminals());
                device_list.add(device);             
            }
            clm.create_log("collect device : success", "srv >> ccm", "act", true);
        } 
        catch (Exception e) {
            try{
                clm.create_log("collect device : error", "srv >> ccm", "err", true);
            }
            catch(Exception ex){
                System.out.println(ex);
            }
            System.out.println(e);
        }
        provider.shutdown();
    }
    
    public int count_device(){
        this.collect_device();
        try{
            clm.create_log("collect device", "srv >> ccm", "act", true);
        }catch(Exception ex){
            System.out.println(ex);
        }
        return device_list.size();
    }
    
    private List<cucm_host_devices> list_device(){
        try{
            clm.create_log("list device", "srv >> ccm", "act", true);
        }catch(Exception ex){
            System.out.println(ex);
        }
        return device_list;
    }
    
    public List<String> list_address(){
        List<String> list = new ArrayList<String>();
        for (cucm_host_devices dev : this.list_device()) 
            list.add(dev.getAddress().getName());
        Collections.sort(list);
        try{
            clm.create_log("list address", "srv >> ccm", "act", true);
        }catch(Exception ex){
            System.out.println(ex);
        }
        return list;
    }
	
    public List<String> list_terminal(){
    	List<String> list = new ArrayList<String>();
    	int i = 0;
    	for(cucm_host_devices dev : this.list_device()){
            String terminal = dev.getAddress().getTerminals()[0].getName();
            list.add(terminal);
            i++;
    	}
    	Collections.sort(list);
    	try{
            clm.create_log("list terminal", "srv >> ccm", "act", true);
    	}
    	catch(Exception ex){
            System.out.println(ex);
    	}
    	return list;
    }
    
    public List<String>[] list_all(){
    	List<String>[] list = new ArrayList[2];
        list[0] = new ArrayList<String>();
        list[1] = new ArrayList<String>();
    	for(cucm_host_devices dev : this.list_device()){
            String terminal = dev.getAddress().getTerminals()[0].getName();
            String address = dev.getAddress().getName();
            list[0].add(terminal);
            list[1].add(address);
    	}
    	try{
            clm.create_log("list all data", "srv >> ccm", "act", true);
    	}
    	catch(Exception ex){
            System.out.println(ex);
    	}
    	return list;
    }
    
    public String search_terminal(){
        String terminal = "";
        cucm_host cn = new cucm_host();
        Provider provider = cn.get_provider();
        try {    
            Terminal term = provider.getAddress("1004").getTerminals()[0];
            terminal = term.getName();
        } catch (Exception ex) {
        }
        return terminal;
    }
    
    private CiscoCall cc = null;
    Provider prov = null;
    //======================================================================//
    public void make_call(String source, String dest) throws Exception{
        prov = new cucm_host().get_provider();
        final Condition	inService = new Condition();
        prov.addObserver(new ProviderObserver() {
            public void providerChangedEvent (ProvEv [] eventList) {
                if (eventList == null) return;
                for (int i = 0; i < eventList.length; ++i) {
                    if (eventList[i] instanceof ProvInServiceEv) {
                        inService.set();
                    }
                }
            }
        });
        inService.waitTrue();
 
        Address srcAddr = prov.getAddress(source);
        srcAddr.addCallObserver(new CallObserver() {
            public void callChangedEvent (CallEv [] eventList) {

            }
        });
        Terminal term_src =srcAddr.getTerminals()[0];
        try {
            cc = (CiscoCall) prov.createCall();
            cc.connect(term_src, srcAddr, dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void drop_call() throws Exception{
        CallControlCall ctc = cc;
        ctc.drop();
    }
    
    public void specified_drop_call(final String target) throws Exception{
        prov = new cucm_host().get_provider();
        final Condition	inService = new Condition();
        int counter = count_device();
        Address address []= new Address[counter];
        final List<String> list_address = list_address();
        
        prov.addObserver(new ProviderObserver() {
            public void providerChangedEvent (ProvEv [] eventList) {
                if (eventList == null) return;
                for (int i = 0; i < eventList.length; ++i) {
                    if (eventList[i] instanceof ProvInServiceEv) {
                        inService.set();
                    }
                }
            }
        });
        inService.waitTrue();
        
        for (int i = 0; i<address.length;i++){	
            address[i] = prov.getAddress(list_address.get(i));
            address[i].addCallObserver(new CallObserver() {
                @Override
                public void callChangedEvent(CallEv[] eventList){
                    for (CallEv call : eventList) {
                        try {
                            if(call.getCall().getProvider().getAddress(target).getName().equals(target)){
                                CiscoCall cc = (CiscoCall) call.getCall();
                                CallControlCall ccc = cc;
                                ccc.drop();
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        inService.waitTrue();
    }
    
}
