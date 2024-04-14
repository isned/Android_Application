package itbs.sem2.friendslocationv1;

public class Config {

    /**
     * AVD:android virtual device :10.0.2.2 kan bil emulator
     * LAN:reseau local : IPV4 : 192.168
     * Internet:www......
     */
    public static final String IP="192.168.1.18";
    public static final String URL_GETALL="http://"+IP+"/servicephp/get_all.php";
    public static final String URL_AddPosition="http://"+IP+"/servicephp/add_position.php";
}