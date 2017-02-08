package org.rusak.uwifiserver.server;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import customjbdc.queries.DBMakeQuery;
import functions.Functions;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import org.rusak.uwifiserver.data.PacketInfoHorst;
import org.rusak.uwifiserver.run.UWifiServerMain;

/**

 */
public class UWifiPacketClientSocket implements Runnable{
    protected Socket clientSocket = null;
    protected String serverText   = null;
    
    class AverageKeyPair{
        int count;
        float value;
        PacketInfoHorst packet;

        private AverageKeyPair(int count, int value, PacketInfoHorst packet) {
            this.count = count;
            this.value = value;
            this.packet = packet;
        }

        private AverageKeyPair(int value, PacketInfoHorst packet) {            
            this.count = 1;
            this.value = value;
            this.packet = packet;
        }
        
        void avg(){
            this.value /= this.count;
            this.count = 0;
        }
        
        void sum(int value){
            this.value += value;
            this.count++;
        }
    }

    public UWifiPacketClientSocket(Socket clientSocket, String serverText) {  
        Functions.inf(UWifiServerMain.LOG_LEVEL, "<UWifiPacketClientSocket."+serverText+"> New client accepted:");
        System.out.print("{getChannel():" + clientSocket.getChannel());      
        System.out.print(", getInetAddress():" + clientSocket.getInetAddress());      
        System.out.print(", getLocalAddress():" + clientSocket.getLocalAddress());      
        System.out.print(", getLocalPort():" + clientSocket.getLocalPort());      
        System.out.print(", getLocalSocketAddress():" + clientSocket.getLocalSocketAddress());      
        System.out.print(", getPort():" + clientSocket.getPort());      
        System.out.println(", getRemoteSocketAddress():" + clientSocket.getRemoteSocketAddress()+"}");
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    @Override
    public void run() {
        byte[] bytes = new byte[PacketInfoHorst.HORST_PACKET_LEN];
        //InputStream input  = null;
        BufferedInputStream inputbuf = null;
        OutputStream output = null;

        try {
            //input  = clientSocket.getInputStream();
            inputbuf = new BufferedInputStream(clientSocket.getInputStream());
            output = clientSocket.getOutputStream();

        } catch (IOException e) {
            Functions.err(UWifiServerMain.LOG_LEVEL, "<UWifiPacketClientSocket.run."+serverText+"> I/O error.ERR#"+e.getMessage());
            return;
        }
        
        while(true){
            try {
                long time = System.currentTimeMillis();

                int bytesRead = inputbuf.read(bytes);
                PacketInfoHorst packet = new PacketInfoHorst(bytes);
                //packet.println();
                packetHandlerLogic(packet);
                
                //output smth to check connection
                //to actually determine whether or not a socket has been closed 
                //data must be written to the output stream and an exception must be caught. 
                //http://stackoverflow.com/questions/10240694/java-socket-api-how-to-tell-if-a-connection-has-been-closed
                output.write(("ACK").getBytes());                
                
                time = System.currentTimeMillis()-time;                
                  
                //Functions.inf(UWifiServerMain.LOG_LEVEL, "<UWifiPacketClientSocket> Request processed: " + time + "ms");
                
            } catch (IOException e) {
                try {
                    output.close();
                    inputbuf.close();
                    clientSocket.close();
                    
                } catch (IOException ex) {                      
                    Functions.err(UWifiServerMain.LOG_LEVEL, "<UWifiPacketClientSocket.run."+serverText+"> " + ex.getLocalizedMessage());                
                }
                
                break;            
            }
        }
    }
    
    long pastSeconds = 0;
    //HashMap<byte[], HashMap<byte[], int>> monitor_data = new HashMap<byte[], HashMap<byte[], int>>();
    Table<String, String, AverageKeyPair> monitor_data = HashBasedTable.create();
    ArrayList<String> monitor_list = new ArrayList<>();        
    void packetHandlerLogic(PacketInfoHorst packet){
        //skip zero signal value as faulty one
        if(packet.phy_signal >= 0) return;
        long currentSeconds = System.currentTimeMillis()/1000;
        //init var
        if(pastSeconds == 0) pastSeconds = currentSeconds;
        
        //Functions.dbg(UWifiServerMain.LOG_LEVEL, "<UWifiPacketClientSocket.packetHandlerLogic>: past="+pastSeconds+"; curr="+currentSeconds);
        //Functions.dbg(UWifiServerMain.LOG_LEVEL, "<UWifiPacketClientSocket.packetHandlerLogic>: past="+TimeUnit.MILLISECONDS.toSeconds(pastSeconds)+"; curr="+TimeUnit.MILLISECONDS.toSeconds(currentSeconds));
         //TimeUnit.MILLISECONDS.toSeconds(timeMillis);
        //for monitor
        // for src
        //  do
        //   if now.second == data.second
        //     rssi += data.rssi
        //     n++
        //   else 
        //     rssi \= n
        //     save_value_to_db
        //if new second arrived
        String monitor_mac = Functions.macToString(packet.wlan_monitor_mac);
        String wlan_src_mac = Functions.macToString(packet.wlan_src);
        //Functions.dbg(UWifiServerMain.LOG_LEVEL, " packet:{monitor: " + monitor_mac+", device_mac: " + wlan_src_mac+", rssi: "+packet.phy_signal+"}");
                
        //skip faulty packet
        if(wlan_src_mac.equalsIgnoreCase("00:00:00:00:00:00") || monitor_mac.equalsIgnoreCase("00:00:00:00:00:00")){
            return;
        }
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // Monitor Handler logic
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        //if this monitor wasn't noticed before
        if(!monitor_list.contains(monitor_mac)){
            //save to DB and add to HashMap
            DBMakeQuery.addMonitor(UWifiServerMain.jdbc, packet);
            monitor_list.add(monitor_mac);
        }
        
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // Packet Handler logic
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        
        //if second passed
        if(currentSeconds != pastSeconds){
            //Functions.dbg(UWifiServerMain.LOG_LEVEL, "<UWifiPacketClientSocket.packetHandlerLogic>: new second");
            // for each device in monitor_data 
            for (String monmac : monitor_data.rowKeySet()) {
                
                //make average of summed values
                for (Entry<String, AverageKeyPair> row : monitor_data.row(monmac).entrySet()) {
                    //Functions.dbg(UWifiServerMain.LOG_LEVEL,"-before {src:" + row.getKey() + ", value.count:" + row.getValue().count + ", value.value:" + row.getValue().value+"}");
                    AverageKeyPair avg_kpair = row.getValue();
                    avg_kpair.avg();
                    PacketInfoHorst pk = avg_kpair.packet;
                    pk.phy_signalf = avg_kpair.value;
                    //store it to DB
                    //Functions.dbg(UWifiServerMain.LOG_LEVEL,"-after {src:" + row.getKey() + ", value.count:" + avg_kpair.count + ", value.value:" + avg_kpair.value+"}");
                    DBMakeQuery.addSnappedPacket(UWifiServerMain.jdbc, pk);                   
                }                               
            }
            
            //after this step all values are updated, so we can delete them all at once
            monitor_data.clear();
            
            pastSeconds = currentSeconds;
            
        //if second havent passed
        }else {
            //if new monitor -> add and skip
            if(!monitor_data.containsRow(monitor_mac)){
                monitor_data.put(monitor_mac, wlan_src_mac, new AverageKeyPair(packet.phy_signal,packet));
                return;
            }
            //or such monitor alredy was noticed
            Map<String, AverageKeyPair> wlan_src = monitor_data.row(monitor_mac);
            
            //if new device -> add and skip
            if(!wlan_src.containsKey(wlan_src_mac)){                    
                monitor_data.put(monitor_mac, wlan_src_mac, new AverageKeyPair(packet.phy_signal,packet));
                return;
            }
            
            //otherwise sum up received rssi values                                
            AverageKeyPair avg_kpair = wlan_src.get(wlan_src_mac);
            //Functions.dbg(UWifiServerMain.LOG_LEVEL,"before {src:" + wlan_src_mac + ", value.count:" + avg_kpair.count + ", value.value:" + avg_kpair.value+"}");

            avg_kpair.sum(packet.phy_signal);
            //and update record
            monitor_data.put(monitor_mac, wlan_src_mac, avg_kpair); 
            //Functions.dbg(UWifiServerMain.LOG_LEVEL,"after {src:" + wlan_src_mac + ", value.count:" + monitor_data.row(monitor_mac).get(wlan_src_mac).count + ", value.value:" + monitor_data.row(monitor_mac).get(wlan_src_mac).value+"}");

        }
            
    }
}