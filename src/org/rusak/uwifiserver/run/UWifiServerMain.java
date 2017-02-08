/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rusak.uwifiserver.run;

import customjbdc.connection.DBConnection;

import functions.Functions;
import static functions.Functions.DBG;
import static functions.Functions.DBG1;
import static functions.Functions.ERR;
import static functions.Functions.INF;
import static functions.Functions.WRN;

import java.sql.Connection;
import java.util.Properties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.rusak.localization.LCU;
import org.rusak.uwifiserver.server.UWifiPacketServer;

/**
 *
 * @author colt
 */
public class UWifiServerMain {
    public static final int LOG_LEVEL = ERR|WRN|INF|DBG|DBG1;
    
    private static String app_uid;
    private static int server_port = 8080;
    
    //# db connection settings
    private static /*final*/    String  jdbc_URL;
    private static /*final*/    String  db_user;
    private static /*final*/    String  db_pass;
    private static /*final*/    int     db_max_conn;
        
    //if external settings wasn't found
    private static final String DEFAULT_SERVER_PROPERTIES_PATH = "/resources/server.properties";    
    public static Properties serverProperties = new Properties();
    
    public static DBConnection jdbc;
    
    //two threads will exist:
    //  > UWifiPacketServer - that will handle data from clients
    //  > LCU   - that will monitor data consistency and calculate locations when it's possible
    static ExecutorService server_executor =  Executors.newSingleThreadExecutor();
    static ScheduledExecutorService lcu_executor =  Executors.newSingleThreadScheduledExecutor();

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Check connection to DB
        //#REGION get settings            
        if (args.length == 0){
            serverProperties = Functions.getProperties(UWifiServerMain.class.getResourceAsStream(DEFAULT_SERVER_PROPERTIES_PATH));
        }else{
            serverProperties = Functions.getProperties(args[0]);
        }

        if(serverProperties == null){
            Functions.err(LOG_LEVEL, "<SERVER.GETPROP>: No properties were found. Halt");
            return;
        }  

        initVariables(serverProperties);            

        Functions.inf(LOG_LEVEL,
                "<SERVER.INIT> Creating jdbc connection" +
                "[URL:" + jdbc_URL +
                "; User:" + db_user + 
                "; Max_conn:" + db_max_conn +
                "]"
                );

        jdbc = DBConnection.getInstance(jdbc_URL, db_user, db_pass, db_max_conn);
        Connection con = jdbc.getConnection();
        jdbc.freeConnection(con);  
        Functions.inf(LOG_LEVEL, "<SERVER.INIT>: Connection freed.");
        
        //Start UWifiListener Server
        Functions.inf(LOG_LEVEL,
                "<SERVER.INIT> Starting UWifiServer on port: " +
                server_port
                );
        UWifiPacketServer wifi_sniffer_server = new UWifiPacketServer(server_port);
        server_executor.execute(wifi_sniffer_server);
        server_executor.shutdown();
        
        //Schedule Location Calculating Unit starts
        LCU location_calculating_unit = new LCU();
        lcu_executor.scheduleAtFixedRate(location_calculating_unit, 0, 1, TimeUnit.SECONDS);
        //lcu_executor.shutdown();
    }
    
    /*
     * Sets properties 
     * 
     */
    private static void initVariables(Properties properties){
        //get jdbc settings
        //Properties jdbc_props = Functions.getProperties( properties.getProperty("jdbc_props_path") );         
        UWifiServerMain.server_port     = Integer.parseInt(properties.getProperty("server_port"));
        
        //# db connection settings
        UWifiServerMain.jdbc_URL        = properties.getProperty("jdbc_host");
        UWifiServerMain.db_user         = properties.getProperty("jdbc_username");
        UWifiServerMain.db_pass         = properties.getProperty("jdbc_password");
        UWifiServerMain.db_max_conn     = Integer.parseInt(properties.getProperty("jdbc_max_connections"));
        
        UWifiServerMain.app_uid       = properties.getProperty("application_uid");
        
        Functions.inf(LOG_LEVEL,"<SERVER.initVariables>: Properties parsed successfully");   
    }
}
