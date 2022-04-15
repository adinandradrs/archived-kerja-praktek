package dtcom.util.corp;

import java.io.*;
import java.util.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class cucm_log_maker {
	
    private Date date = null;
    private Format formatter;
    
    /*
     * 
     * METHOD CREATE LOG TO FILE
     * 
     */
    public void create_log(String content, String opt, String type, boolean include_time) 
    {
    	try
        {
            formatter = new SimpleDateFormat("yyyyMM");
            date = new Date();
            String title = formatter.format(date)+Calendar.getInstance().getTime().getDate()+"."+type;
            String log_content = "";
            String time = this.get_time_milis();
            if(include_time == true)
            	log_content = time + " | ";
            if(opt.equals(""))
            	log_content += content;
            else
            	log_content += opt  +" | "+ content;
            log_content += "\n";
            File log = new File(title);
            FileWriter logWriter = new FileWriter(log,true);
            logWriter.write(log_content);
            logWriter.flush();
            logWriter.close();
    	}
    	catch(Exception e){
    		
    	}
    }
    
    /*
     * 
     * RETURN TIME IN MILISECOND
     * 
     */
    public String get_time_milis(){
        date = new Date();
        formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        return formatter.format(date);
    }

}