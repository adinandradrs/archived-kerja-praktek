package dtcom.server.corp;

import java.util.ArrayList;
import java.util.List;
import javax.telephony.Address;
import javax.telephony.AddressObserver;
import javax.telephony.Provider;
import javax.telephony.ProviderObserver;
import javax.telephony.CallObserver;
import javax.telephony.Terminal;
import javax.telephony.TerminalObserver;
import javax.telephony.events.*;
import com.cisco.cti.util.Condition;
import com.cisco.jtapi.extensions.CiscoAddrInServiceEv;
import com.cisco.jtapi.extensions.CiscoAddrOutOfServiceEv;
import com.cisco.jtapi.extensions.CiscoAddress;
import com.cisco.jtapi.extensions.CiscoCall;
import com.cisco.jtapi.extensions.CiscoJtapiVersion;
import com.cisco.jtapi.extensions.CiscoTerminal;
import dtcom.cisco.corp.cucm_host;
import dtcom.cisco.corp.cucm_host_devices_access;
import dtcom.util.corp.cucm_log_maker;

public class cucm_server_main extends Thread {
	
	
	//================ARRAY OF LIST BUFFER===========//
	List<String>[] arr_list_buffer = new ArrayList[10];
	//===============================================//
	private String string = "";
	private List<String> list_log = new ArrayList<String>();
	private static final cucm_server_main cs = new cucm_server_main();
	private final cucm_log_maker clm = new cucm_log_maker();
	private cucm_host_devices_access chda = new cucm_host_devices_access();
	private cucm_server_socket_thread_child csstc [] = new cucm_server_socket_thread_child[5];	
	private cucm_server_socket css = new cucm_server_socket();
	
	public static void main(String [] args){		
            System.out.println("CUCM Version : "+new CiscoJtapiVersion().getVersion());
            System.out.println("PT. DATACOM SOLUSINDO\n::DEVELOPMENT VERSION::");
            /*
             * 
             * START THE CUCM_SERVER_MAIN THREAD
             * 
             * */
            cs.start();

            /*
             * 
             * DEFINE THE ARRAY LIST OF BUFFER
             * 
             * */
            cs.arr_list_buffer[0] = new ArrayList<String>();
            cs.arr_list_buffer[1] = new ArrayList<String>();
            cs.arr_list_buffer[2] = new ArrayList<String>();
            cs.arr_list_buffer[3] = new ArrayList<String>();
            cs.arr_list_buffer[4] = new ArrayList<String>();
            cs.arr_list_buffer[5] = new ArrayList<String>();

            /*
             * 
             * START THE CUCM_SERVER_MAIN CONNECTION TO CUCM
             * 
             * */
            cucm_host cn = new cucm_host();
            Provider provider = cn.get_provider();
            final Condition inService = new Condition();

            cs.clm.create_log("get provider : ON", "srv >> ccm", "act", true);
            
            /*
             * 
             * PROVIDER OBSERVER (STATUS IN SERVICE)
             * 
             * */
            try{
                final String provider_name = provider.getName()+"          ";
                provider.addObserver(new ProviderObserver() {
                    @Override
                    public void providerChangedEvent (ProvEv [] eventList) {
                        if (eventList == null) return;
                        for (int i = 0; i < eventList.length; ++i){
                            if (eventList[i] instanceof ProvInServiceEv){
                                String time = cs.clm.get_time_milis();
                                cs.string = provider_name.substring(0,20)+" | " + eventList[i];
                                cs.list_log.add(time + " | " + provider_name.substring(0,20) +" | OP(Prst) : "+cs.string);
                                cs.string = cs.string = provider_name.substring(0,15) + " | " + eventList[i].getID();
                                cs.arr_list_buffer[0].add(cs.string);
                                inService.set();
                            }
                        }
                    }
                });
                inService.waitTrue();
                cs.clm.create_log("get provider's service condition : SET", "ccm >> srv", "act", true);
            }
            catch(Exception ex){
                cs.clm.create_log("get provider's service condition : NOT SET", "ccm >> srv", "err", true);
            }

            int counter = cs.chda.count_device();

            cs.clm.create_log("prepare set observer", "srv >> ccm", "act", true);
            
            /*
             * 
             * ADDRESS OBSERVER 
             * 
             * */
            try{
                Address address []= new Address[counter];
                final List<String> list_address = cs.chda.list_address();
                for (int i = 0; i<address.length;i++){	
                    address[i] = provider.getAddress(list_address.get(i));
                    final String ad = address[i].getName()+"                    ";
                    
                    /*
                     * 
                     * STATUS CALL AT ADDRESS
                     * 
                     */
                    address[i].addCallObserver(new CallObserver() {
                        @Override
                        public void callChangedEvent(CallEv[] eventList){
                            int n = eventList.length;
                            for(int j = 0; j < n;j++){
                                String time = cs.clm.get_time_milis();
                                cs.string = ad.substring(0,20)+" | "+eventList[j].toString();
                                cs.list_log.add(time + " | " + ad.substring(0,20) +" | OA(Call) : "+cs.string);
                                cs.string = ad.substring(0,15) + " | " + eventList[j].getID();
                                cs.arr_list_buffer[1].add(cs.string);
                            }
                        }
                    });
                    cs.clm.create_log("set call address observer to : " + address[i], "ccm >> srv", "act", true);

                    /*
                     * 
                     * STATUS ADDRESS
                     * 
                     */
                    address[i].addObserver(new AddressObserver(){
                        public void addressChangedEvent(AddrEv[] addrevs){
                            for(int k = 0; k<addrevs.length;k++){
                                cs.string = ad.substring(0,20) + " | " + addrevs[k];
                                String time = cs.clm.get_time_milis();
                                cs.list_log.add(time + " | " + ad.substring(0,20)+ " | OA(Addr) : "+cs.string); 
                                CiscoAddress caddress = null;
                                //SEE ACTIVE (IN SERVICE) EXTENSION
                                if(addrevs[k] instanceof CiscoAddrInServiceEv)
                                    caddress = (CiscoAddress) addrevs[k].getAddress();
                                //SEE INACTIVE (OUT OF SERVICE) EXTENSION
                                else if(addrevs[k] instanceof CiscoAddrOutOfServiceEv)
                                    caddress = (CiscoAddress) addrevs[k].getAddress();
                                cs.string = ad.substring(0,15) + " | " + caddress.getState();
                                cs.arr_list_buffer[2].add(cs.string);
                            }
                        };
                    });
                    cs.clm.create_log("set status address observer to : " + address[i] +" *in service only", "ccm >> srv", "act", true);
                }
            }
            catch(Exception ex){
                cs.clm.create_log("set address observer : NOT SET", "ccm >> srv", "err", true);
            }

            /*
             * 
             * TERMINAL OBSERVER
             * 
             * */
            try{
                Terminal terminal [] = new Terminal[counter];
                final List<String> list_terminal = cs.chda.list_terminal();
                for(int i = 0; i<terminal.length;i++){
                    terminal[i] = provider.getTerminal(list_terminal.get(i));
                    final String tm = terminal[i].getName()+"                    ";

                    /*
                     * 
                     * OBSERVER STATUS CALL AT TERMINAL
                     * 
                     * */
                    terminal[i].addCallObserver(new CallObserver() {	
                        @Override
                        public void callChangedEvent(CallEv[] eventList) {
                            for(int j = 0;j<eventList.length;j++){
                                cs.string = tm.substring(0,20) + " | " + eventList[j];
                                String time = cs.clm.get_time_milis();
                                cs.list_log.add(time + " | " + tm.substring(0,20)+ " | OT(Call) : "+cs.string);
                                cs.string = tm.substring(0,15) + " | " + eventList[j].getID();
                                cs.arr_list_buffer[3].add(cs.string);
                            }						
                        }
                    });
                    cs.clm.create_log("set call terminal observer to : " + terminal[i], "ccm >> srv", "act", true);

                    /*
                     * 
                     * OBSERVER STATUS TERMINAL
                     * 
                     * */
                    terminal[i].addObserver(new TerminalObserver() {
                        @Override
                        public void terminalChangedEvent(TermEv[] eventList) {
                            for(int k = 0; k<eventList.length; k++){
                                cs.string = tm.substring(0,20) + " | " + eventList[k];
                                String time = cs.clm.get_time_milis();
                                cs.list_log.add(time + " | " + tm.substring(0,20)+ " | OT(Term) : "+cs.string);

                                CiscoTerminal cterminal = (CiscoTerminal)eventList[k].getTerminal();
                                cs.string = tm.substring(0,15) + " | " + cterminal.getState();
                                cs.arr_list_buffer[4].add(cs.string);
                            }
                        }
                    });
                    cs.clm.create_log("set status terminal observer to : " + terminal[i], "ccm >> srv", "act", true);

                    /*
                     * 
                     * THE CISCO METHOD FOR CHECK DEVICE STATE 
                     * (JUST WORK IF YOU'VE ADD THE OBSERVER TO TERMINAL)
                     * 
                     * */
                    CiscoTerminal cterminal = (CiscoTerminal)terminal[i];
                    cs.string = tm.substring(0,15) + " | " + cterminal.getDeviceState();
                    cs.arr_list_buffer[5].add(cs.string);
                }
            }
            catch(Exception ex){
                    cs.clm.create_log("set terminal observer : NOT SET", "ccm >> srv", "err", true);
            }

            /*
             * 
             * 1) RUN THREAD CUCM_SERVER_MAIN_THREAD (STATE IS SLEEP)
             * 2) RUN CUCM_SERVER_SOCKET -> START SOCKET (STATE IS LISTENING)
             * 
             * */
            new cucm_server_main_thread_child(cs.arr_list_buffer, cs.css).start();
            cs.css.start_socket(cs.csstc, cs.chda);
	}
	
	/*
	 * 
	 * METHOD RUN THREAD CUCM_SERVER_MAIN
	 * 
	 * */
	protected int it = 0;
        
	@Override
	public void run(){
            cs.clm.create_log("thread parent run : cucm_server_main", "srv >> srv", "act", true);
            while(true){
                if(cs.list_log.size() != 0){
                    for(String string : cs.list_log){
                        String content = string;
                        try{
                            clm.create_log(content, "","log",false);
                            cs.list_log.set(it, "");
                            it++;
                        }
                        catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    for(int i=it-1;i>=0;i--){
                        if(cs.list_log.get(i).equals(""))
                        cs.list_log.remove(i);
                        it--;
                    }
                }	
                try{
                    sleep(1000);
                }
                catch(Exception e){

                }
            }	
	}
}

