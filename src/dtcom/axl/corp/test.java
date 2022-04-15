package dtcom.axl.corp;

public class test {

    public static void main(String[] args) {
        String cmd = "2|Blabla|1003|130";
        String [] breakcmd = cmd.split("\\|");
        for (String string : breakcmd) {
            System.out.println(string);
        }
    }
    
}
