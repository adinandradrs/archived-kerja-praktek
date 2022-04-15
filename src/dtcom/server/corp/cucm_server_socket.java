package dtcom.server.corp;

import dtcom.cisco.corp.cucm_host_devices_access;
import dtcom.util.corp.cucm_ini_configuration;
import dtcom.util.corp.cucm_log_maker;
import java.net.*;

public class cucm_server_socket extends Thread {
	
    private ServerSocket server_socket;
    private Socket client_socket;
    private cucm_log_maker clm = new cucm_log_maker();
    private int i = 0;
    private int count;
    private cucm_host_devices_access chda;
    private cucm_server_socket_thread_child _csstc [];

    /*
     * 
     * GET COUNT OF THE USER EXIST, THE GETTER FUNCTION REFERENCE (SEE AT THE CUCM_SERVER_MAIN)
     * 
     * */
    public int getCount(){
        return count;
    }
	
    /*
     * 
     * SET COUNT OF THE USER EXIST, THE SETTER FUNCTION (SEE AT THE CUCM_SERVER_SOCKET_THREAD_CHILD)
     * IS TRIGGERING THE COUNT OF USER EXIST's VALUE, SET ON THE USER EXIT/KILL
     * 
     * */
    public void setCount(int count){
        this.count = count;
    }
	
    /*
     * 
     * METHOD FOR ACCEPT THE CLIENT CONNECTION WHILE THE TOTAL OF QUEUE IS NOT 5
     * IS TRIGGERING THE THREAD CUCM_SERVER_SOCKET_THREAD_CHILD (SEE AT THE CSCTC's ARRAY SHAPE)
     * 
     * */
    public void start_socket(cucm_server_socket_thread_child [] csstc, cucm_host_devices_access chda){
        try{
            this._csstc = csstc;
            this.chda = chda;
            int port = new cucm_ini_configuration().get_ini().get("server", "port", Integer.class);
            clm.create_log("is opening socket port : " + port, "srv >> clt", "act", true);
            server_socket = new ServerSocket(port);
            while(true){
                i++; 
                client_socket = server_socket.accept();
                for (int i = 0; i < csstc.length; i++) {
                    if (_csstc[i] == null) {
                        clm.create_log("create client at room - " + i, "srv >> clt", "act", true);
                        new Thread((csstc[i] = new cucm_server_socket_thread_child(this,client_socket, csstc, this.chda))).start();
                        int k = 0;
                        for(int j = 0;j<csstc.length;j++)
                            if(csstc[j]!=null)
                                k++;
                        break;
                    }
                    else{
                        count--;
                    }
                }
            }
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }
	
    
}