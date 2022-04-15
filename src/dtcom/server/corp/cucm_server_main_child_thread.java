package dtcom.server.corp;

import java.util.List;
import dtcom.util.corp.cucm_lib_glossary;
import dtcom.util.corp.cucm_log_maker;

class cucm_server_main_thread_child extends Thread{
	
    private List<String>[] arr_list_buffer;
    private final cucm_log_maker clm = new cucm_log_maker();
    private cucm_lib_glossary clg = new cucm_lib_glossary();
    private cucm_server_socket css;
	
    /*
     * 
     * CLASS CONSTRUCTOR
     * 
     * */
    public cucm_server_main_thread_child(List<String>[] arr_list_buffer, cucm_server_socket css){
            this.arr_list_buffer = arr_list_buffer;
            this.css = css;
    }
	
    /*
     * 
     * DEFINE AND GET THE EACH BUFFER LIST SIZE 
     * 
     * */
    public int [] list_size(){
        int [] ls = {
            arr_list_buffer[0].size(),
            arr_list_buffer[1].size(),
            arr_list_buffer[2].size(),
            arr_list_buffer[3].size(),
            arr_list_buffer[4].size(),
            arr_list_buffer[5].size()
        };
        return ls;
    }
	
    /*
     * 
     * THREAD FUNCTION FOR LOG PRINT, SEND REPLY, ETC..... (THREAD CORE)
     * 
     * */
    @Override
    public void run(){
        clm.create_log("thread child run : cucm_server_main_thread_child", "ccm >> srv", "act", true);
        while(true){
            //System.out.println("Client connect : "+css.getCount());
            int cek[] = this.list_size();
            try{
                if(cek[0]>0){
                    clm.create_log("======================Data cek[0]======================", "", "act", false);
                    for(int i = 0; i<cek[0];i++){
                        String content = arr_list_buffer[0].get(0);
                        clm.create_log(this.content(content,3), "ccm >> srv", "act", true);
                        arr_list_buffer[0].remove(0);
                    }
                    clm.create_log("=======================================================", "", "act", false);
                }
                if(cek[1]>0){
                    clm.create_log("======================Data cek[1]======================", "", "act", false);
                    for(int i = 0; i<cek[1];i++){
                        String content = arr_list_buffer[1].get(0);
                        String entry = this.content(content,3);
                        clm.create_log(entry, "ccm >> srv", "act", true);
                        //css.send_reply(entry+"\r");
                        arr_list_buffer[1].remove(0);
                    }
                    clm.create_log("=======================================================", "", "act", false);					
                }
                if(cek[2]>0){
                    clm.create_log("======================Data cek[2]======================", "", "act", false);
                    for(int i = 0; i<cek[2];i++){
                        String content = arr_list_buffer[2].get(0);
                        clm.create_log(this.content(content,2), "ccm >> srv", "act", true);
                        arr_list_buffer[2].remove(0);
                    }
                    clm.create_log("=======================================================", "", "act", false);
                }
                if(cek[3]>0){
                    clm.create_log("======================Data cek[3]======================", "", "act", false);
                    for(int i = 0; i<cek[3];i++){
                        String content = arr_list_buffer[3].get(0);
                        String entry = this.content(content,3);
                        clm.create_log(this.content(content,3), "ccm >> srv", "act", true);
                        //css.send_reply(entry+"\r");
                        arr_list_buffer[3].remove(0);
                    }
                    clm.create_log("=======================================================", "", "act", false);
                }
                if(cek[4]>0){
                    clm.create_log("======================Data cek[4]======================", "", "act", false);
                    for(int i = 0; i<cek[4];i++){
                        String content = arr_list_buffer[4].get(0);
                        clm.create_log(this.content(content,0), "ccm >> srv", "act", true);
                        arr_list_buffer[4].remove(0);
                    }
                    clm.create_log("=======================================================", "", "act", false);
                }
                if(cek[5]>0){
                    clm.create_log("======================Data cek[5]======================", "", "act", false);
                    for(int i = 0; i<cek[5];i++){
                        String content = arr_list_buffer[5].get(0);
                        clm.create_log(this.content(content,1), "ccm >> srv", "act", true);
                        arr_list_buffer[5].remove(0);
                    }
                    clm.create_log("=======================================================", "", "act", false);
                }
                sleep(1500);
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }
	
    /*
     * 
     * SUBSTRING IN EVERY STRING THAT PLACED ON LAST POSITION (18,LAST POST) TO TRANSLATE
     * 
     * */
    public String content(String content, int glossary_type){
        int last_pos = content.length();
        String id = content.substring(18,last_pos);
        int ID = Integer.parseInt(id);
        if(glossary_type == 3)
            return content.substring(0,17) + clg.lib_event(ID);
        else if(glossary_type == 2)
            return content.substring(0,17) + clg.lib_csc_addr_state(ID);
        else if(glossary_type == 1)
            return content.substring(0,17) + clg.lib_csc_term_dev_state(ID);
        else if(glossary_type == 0)
            return content.substring(0,17) + clg.lib_csc_term_state(ID);
        else return "";
    }
	
}