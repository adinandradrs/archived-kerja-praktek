package dtcom.server.corp;

import dtcom.axl.corp.cucm_host_axl;
import dtcom.cisco.corp.cucm_host_devices_access;
import dtcom.util.corp.cucm_ini_configuration;
import dtcom.util.corp.cucm_log_maker;
import dtcom.util.corp.cucm_xml_rw;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import org.ini4j.Wini;

public class cucm_server_socket_thread_child extends Thread {
	
    private Socket client_socket;
    private cucm_server_socket_thread_child csstc[];
    public List<String> list_dev_to_mon = new ArrayList<String>();
    private cucm_log_maker clm = new cucm_log_maker();
    private PrintStream output = null;
    private BufferedReader input = null;
    private String uinput = "";
    private int xKar;
    private cucm_server_socket css;
    private cucm_host_devices_access chda;
    private int idx, num, code_req;
    public List<String> buffer_command = new ArrayList<String>();
    private boolean keep_login = true;

    /*
     * 
     * CLASS CONSTRUCTOR
     * 
     * */
    public cucm_server_socket_thread_child(cucm_server_socket css,Socket client_socket ,cucm_server_socket_thread_child csstc[],cucm_host_devices_access chda){
        this.client_socket = client_socket;
        this.csstc= csstc;
        this.css = css;
        this.chda = chda;
    }
    
    /*
     * 
     * IF THE THREAD RUN
     * 
     * */
    public void run(){
        try{
            output = new PrintStream(client_socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            css.setCount(this.free_space());
            clm.create_log("client connected, from : " + client_socket.getRemoteSocketAddress(), "clt >> srv", "act", true);
            clm.create_log("client counter : " + this.free_space(), "srv >> clt", "act", true);
            
            /*
             * 
             * TO GIVE EACH CLIENT OBJECT HAVE AN AUTO INCREMENT ID 
             * 
             * */
            for(int i = 0; i<=4;i++){
                if(csstc[i] == this)
                idx = i;
            }		
            
            clm.create_log("client placed at room : " + idx, "clt >> srv", "act", true);
            
            String regex = "";
            while(keep_login){
                //CMD CLIENT
                while(input != null){
                    xKar = input.read();
                    if(xKar != 13)
                        uinput += (char)xKar;
                    if(xKar == 13){
                        regex = uinput.replace(String.valueOf((char)13), "");
                        buffer_command.add(regex);
                        uinput = "";
                        exec_axl_command(buffer_command.get(0));
                        buffer_command.remove(0);       
                        break;
                    }
                    if(xKar == -1)
                        break;
                }
                //ACK CLIENT
                while(input != null){
                    xKar = input.read();
                    if(xKar != 75)
                        uinput += (char)xKar;
                    if(xKar == 75){
                        uinput="";
                        break;
                    }
                    if(xKar == -1)
                        break;
                }
                if(xKar==-1)
                    break;
            }
            this.client_menu_choose_exit();
        }
        catch(Exception ex){
            ex.printStackTrace();
            try {
                this.client_menu_choose_exit();       
            } catch (IOException e) {
                clm.create_log("Client unexpected quit", "srv >> ccm", "err", true);
            }
            clm.create_log("Client unexpected quit", "srv >> ccm", "err", true);
        }
    }
	
    /*
     * 
     * COUNT FREE SPACE, SLOT OF CLIENT
     * 
     * */
    public int free_space(){
        int counter = 5;
        for (int i = 0; i < csstc.length; i++) 
            if(csstc[i] == null)
                counter--;
        return counter;
    }
	
    /* 
     * 
     * KILL CLIENT THREAD WHEN THEY ARE EXIT (MAKE NULL)
     * 
     * */
    private void client_menu_choose_exit() throws IOException{
        for(int i=0; i<=4; i++)
            if (csstc[i]==this) 
            	csstc[i]=null;
        css.setCount(this.free_space());
        clm.create_log("client logout, space left : " + this.free_space(), "srv >> clt", "act", true);
        input.close();
        output.close();
        client_socket.close();
    }
    
    /*
     * 
     * EXEC COMMAND
     * 
     */
    private void exec_axl_command(String command) throws IOException{
        clm.create_log("client "+idx+ " : "+command, "","log",false);
        //OK
        this.acknowledge_response("OK");
        if(buffer_command.size()>0){
            List<String> list;
            Wini ini;
            String arr_command [] = command.split("\\|");
            int code_idx = 0;
            try{
                code_idx = Integer.parseInt(arr_command[0].trim());
            }
            catch(Exception ex){
                code_idx = 0;
            }
            String term = "";
            String desc = "";
            String response = "";
            cucm_host_axl cha = new cucm_host_axl();
            switch (code_idx){
                //DISPLAY UPDATE
                case 1 :
                    //1|sadas|1003|TEST|TEST|TEST
                    //format : id_cmd|desc_cmd|extension|display|label|desc
                    term = ext_to_mac(arr_command[2]);
                    String ext = arr_command[2];
                    String display = arr_command[3];
                    String label = "";
                    desc = "";
                    if(arr_command.length==6){
                        if(!arr_command[4].equals(""))
                            label = arr_command[4];
                        desc = arr_command[5];
                    }
                    cha.update_display_name(term, ext, display, label, desc);
                    response = arr_command[0]+"|"+arr_command[1]+"|"+arr_command[2]+"|"+cha.get_response()+"\r";
                    this.acknowledge_response(response);
                    String updated_display = "Extension [" + ext +"] ";
                    updated_display += "Display [" + display +"] ";
                    updated_display += "Label [" + label+"[ ";
                    updated_display += "Desc ["+desc+"]";
                    clm.create_log("client "+idx+ " : display update " + updated_display, "srv >> clt", "act", true);
                    clm.create_log("server to client : " + cha.get_response(), "", "log", true);
                    break;
                //Update CSS
                case 2 :
                    //id_cmd|desc_cmd|ext|code_css
//                    cha.list_css();
//                    list = new cucm_xml_rw().list_axl_val("xmlcss", "row", "name");
                    ini = new cucm_ini_configuration().get_ini();
                    int ix = 0;
//                    for (String string : list) {
//                        if(string.equalsIgnoreCase("CSS_NONE")){
//                            ix=0;
//                            ini.put("css_level",""+ix+"",string);
//                        }
//                        else if(string.equalsIgnoreCase("CSS_INTERNAL")){
//                            ix=1;
//                            ini.put("css_level",""+ix+"",string);
//                        }
//                        else
//                            ini.put("css_level",""+ix+"",string);
//                        ix++;
//                    }
//                    try{
//                        ini.store();
//                    }
//                    catch(Exception e){
//                        e.printStackTrace();
//                    }
                    int css_type = Integer.parseInt(arr_command[3]);
                    String terminal = this.ext_to_mac(arr_command[2]);
                    String css = ini.get("css_level", ""+css_type);
                    cha.update_css(terminal,css);
                    response = arr_command[0]+"|"+arr_command[1]+"|"+arr_command[2]+"|"+cha.get_response()+"\r";
                    this.acknowledge_response(response);
                    String edited_css = "Extension [" + arr_command[3] + "] ";
                    edited_css += "CSS [" +css+"] ";
                    clm.create_log("client "+idx+ " : css edited " + edited_css, "srv >> clt", "act", true);
                    clm.create_log("server to client : " + cha.get_response(), "", "log", true);
                    break;
                //DND UPDATE
                case 3 :
                    //format : id_cmd|desc_cmd|extension|status|option
                    term = ext_to_mac(arr_command[2]);
                    System.out.println(arr_command[2]);
                    String option = "";
                    String status = DND_conv_to_status(arr_command[3]);
                    if(arr_command.length==5)    
                        option = DND_conv_to_option(arr_command[4]);
                    cha.update_dnd(term, status, option);
                    response = arr_command[0]+"|"+arr_command[1]+"|"+arr_command[2]+"|"+cha.get_response()+"\r";
                    this.acknowledge_response(response);
                    String updated_dnd = "Extension [" + arr_command[2] +"]";
                    updated_dnd += " Option ["+option+"]";
                    updated_dnd += " Statis ["+status+"]";
                    clm.create_log("client "+idx+ " : dnd update " + updated_dnd, "srv >> clt", "act", true);
                    clm.create_log("server to client : " + cha.get_response(), "", "log", true);
                    break;
                //FAC ADD
                case 4 :
                    //format : id_cmd|desc_cmd|desc|code|level
                    desc = arr_command[2];
                    String code = arr_command[3];
                    String auth_level = arr_command[4];
                    cha.add_fac(code,desc, auth_level);
                    response = arr_command[0]+"|"+arr_command[1]+"|"+arr_command[2]+"|"+cha.get_response()+"\r";
                    this.acknowledge_response(response);
                    String added_fac = "Desc ["+desc+"]";
                    added_fac += " Pin ["+code+"]";
                    added_fac += " Auth Level ["+auth_level+"]";
                    clm.create_log("client "+idx+ " : add fac " + added_fac, "srv >> clt", "act", true);
                    clm.create_log("server to client : " + cha.get_response(), "", "log", true);
                    break;
                //FAC UPDATE
                case 5 : 
                    //pin == code
                    //format : id_cmd|desc_cmd|newname|oldcode|newcode|level
                    code = arr_command[3];
                    String newname = arr_command[2];
                    String newpin = arr_command[4];
                    int level = Integer.parseInt(arr_command[5]);
                    cha.update_fac(level, newname, newpin, code);
                    response = arr_command[0]+"|"+arr_command[1]+"|"+arr_command[2]+"|"+cha.get_response()+"\r";
                    this.acknowledge_response(response);
                    String updated_fac = "Old Pin ["+code+"]";
                    updated_fac += " New Name ["+newname+"]";
                    updated_fac += " New Pin ["+newpin+"]";
                    updated_fac += " Level ["+level+"]";
                    clm.create_log("client "+idx+ " : update fac "+ updated_fac, "srv >> clt", "act", true);
                    clm.create_log("server to client : " + cha.get_response(), "", "log", true);
                    break;
                //REMOVE FAC
                case 6 :
                    //pin == code
                    //format : id_cmd|desc_cmd|desc|pin
                    desc = arr_command[2];
                    String pin = arr_command[3];
                    cha.rem_fac(desc, pin);
                    response = arr_command[0]+"|"+arr_command[1]+"|"+arr_command[2]+"|"+cha.get_response()+"\r";
                    this.acknowledge_response(response);
                    String removed_fac = "PIN [" + pin +"]";
                    removed_fac += "Desc ["+desc+"]";
                    clm.create_log("client "+idx+ " : remove fac "+ removed_fac, "srv >> clt", "act", true);
                    clm.create_log("server to client : " + cha.get_response(), "", "log", true);
                    break;
                //EXTENSION ADD
                case 7 :
                    //format : id_cmd|desc_cmd|pattern
                    String pattern = arr_command[2];
                    cha.add_ext(pattern);
                    //this.acknowledge_response("Telah diterima : " + command+"\r");
                    String added_ext = "Pattern ["+pattern+"]";
                    
                    response = arr_command[0]+"|"+arr_command[1]+"|"+arr_command[2]+"|"+cha.get_response()+"\r";
                    clm.create_log("client "+idx+ " : add extension "+added_ext, "srv >> clt", "act", true);
                    clm.create_log("server to client : " + cha.get_response(), "", "log", true);
                    this.acknowledge_response(response);
                    break;
                //LIST EXTENSION/TERMINAL
                case 10 :
                    //format : id_code|desc_cmd|Terminal/Extension
                    if(arr_command[2].equals("Terminal"))
                        list(chda.list_terminal());
                    else if(arr_command[2].equals("Extension"))
                        list(chda.list_address());
                    //this.acknowledge_response("Telah diterima : " + command+"\r");
                    this.acknowledge_response("OK\r");
                    clm.create_log("client "+idx+ " : refresh list address", "srv >> clt", "act", true);
                    break;
                //SEARCH EXT/TERM
                case 11 :
                    //format : id_code|desc_cmd|ext/term?|criteria ext/term?
                    String criteria = arr_command[3];
                    if(arr_command[2].equals("Terminal"))
                        this.list_search(chda.list_all()[1],chda.list_all()[0], criteria);
                    else if(arr_command[2].equals("Extension"))
                        this.list_search(chda.list_all()[0], chda.list_all()[1], criteria);
                    //this.acknowledge_response("Telah diterima : " + command+"\r");
                    this.acknowledge_response("OK\r");
                    String scope = "Scope ["+arr_command[2]+"]";
                    clm.create_log("client "+idx+ " : search ext/term "+scope, "srv >> clt", "act", true);
                    break;
                //REFRESH LIST CSS
                case 12:
                    cha.list_css();
                    list = new cucm_xml_rw().list_axl_val("xmlcss", "row", "name");
                    ini = new cucm_ini_configuration().get_ini();
                    ix = 0;
                    for (String string : list) {
                        if(string.equalsIgnoreCase("CSS_NONE")){
                            ix=0;
                            ini.put("css_level",""+ix+"",string);
                        }
                        else if(string.equalsIgnoreCase("CSS_INTERNAL")){
                            ix=1;
                            ini.put("css_level",""+ix+"",string);
                        }
                        else
                            ini.put("css_level",""+ix+"",string);
                        ix++;
                    }
                    try{
                        ini.store();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    this.acknowledge_response("OK\r");
                    //this.acknowledge_response("Telah diterima : " + command+"\r");
                    clm.create_log("client "+idx+ " : refresh list css", "srv >> clt", "act", true);
                    break;
                //MAKE CALL
                case 13:
                    //13|MAKE CALL|1003|1004
                    String source = arr_command[2];
                    String dest = arr_command[3];
                    try {
                        chda.make_call(source, dest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                //DROP CALL
                case 14:
                    //14
                    try {
                        chda.drop_call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                //SPECIFIED DROP CALL
                case 15:
                    //15
                    try {
                        chda.specified_drop_call(arr_command[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;    
                case 20:
                    keep_login = false;
                    break;
                default :
                    this.acknowledge_response("Please enter the right character \\o_o/\r");
                    clm.create_log("client "+idx+ " : command invalid", "srv >> clt", "act", true);
                    break;
            }
        }
    }
    
    /*
     * 
     * ACKNOWLEDGE OPTIONAL
     * 
     */
    private void acknowledge_response(String response){
        for(byte xKar : response.getBytes())
            output.write(xKar);
    }
    
    /*
     * 
     * JTAPI CONTROL
     * 
     */
    private void list(List<String>list){
        for (String string : list) {
            String word = string+"\r";
            for (byte xKar : word.getBytes())
                output.write(xKar);
        }
    }
    
    private void list_search(List<String>list_primer,List<String>list_sec, String criteria){
        for(int i = 0; i<list_primer.size();i++)
            if(list_primer.get(i).equals(criteria))
                for (byte xKar : (list_sec.get(i)+"\r").getBytes()) 
                    output.write(xKar);
    }
    
    /*
     * 
     * EXTENSION TO MAC ADDRESS
     * 
     */
    private String ext_to_mac(String extension){
        int index = 0;
        String mac = "";
        for(int i = 0; i<chda.list_all()[1].size();i++)
        {
            if(chda.list_all()[1].get(i).trim().equalsIgnoreCase(extension.trim()))
            {
                index = i;
                mac = chda.list_all()[0].get(index);
            }
        }
        if(mac.equals("".trim()))
        {
            mac="Unknown";
        }
        return mac;
    }
    
    /*
     * 
     * DND Menu Option
     * 
     */
    private String DND_conv_to_status(String status){
        String bool = "";
        try {
            bool = new cucm_ini_configuration().get_ini().get("dnd_status", status);
        } catch (Exception e) {
            //ini reader exception
        }
        return bool;
    }
    
    private String DND_conv_to_option(String option){
        String mode = "";
        try {
            mode = new cucm_ini_configuration().get_ini().get("dnd_option", option);
        } catch (Exception e) {
            //ini reader exception
        }
        return mode;
    }
    
    
    
}
