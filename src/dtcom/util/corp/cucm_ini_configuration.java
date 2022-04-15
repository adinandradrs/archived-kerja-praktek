package dtcom.util.corp;

import java.io.File;
import org.ini4j.Wini;

public class cucm_ini_configuration {

    private Wini ini = null;
    
    /*
     * 
     * CLASS CONSTRUCTOR, SET FILE INI LOCATION
     * 
     */
    public cucm_ini_configuration(){
        try {
            this.ini = new Wini(new File("conf/cucm.ini"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
     * 
     * FUNCTION RETURN INI VALUES
     * 
     */
    public Wini get_ini(){
        return this.ini;
    }
    
}
