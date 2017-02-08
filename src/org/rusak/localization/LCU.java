/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rusak.localization;

import com.google.common.collect.ArrayListMultimap;
import customjbdc.queries.DBMakeQuery;
import functions.Functions;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.rusak.localization.math.Location;
import org.rusak.localization.math.Point2D;
import org.rusak.localization.math.TrendEquation;
import org.rusak.uwifiserver.data.MonitorDataContainer;
import org.rusak.uwifiserver.run.UWifiServerMain;

/**
 * LocalizationComputingUnitRunnable > will be run every X seconds > will check
 * for data consistency > - if there is enough data to calculate some device
 * location, then > - calculation will occur and DB will be updated.
 *
 * == LCU need ============== > 3 Anchor nodes{AN1(x,y,z), AN2(x,y,z),
 * AN3(x,y,z)} > 3 distances to unknown node from 3 AN{AN1 -> UN, AN2 -> UN, AN3
 * -> UN} - distances are calculated from RSSI values using trend equation
 *
 * == Steps to retrieve data > get N snapped packets for X client from Q, Z and
 * W monitors - check for client that have snapped packets from more than 2
 * monitors - get list of clients - for each client count monitors - fifo: if
 * found client with 3 and more monitors then calculate location
 *
 * @author colt
 */
public class LCU implements Runnable {

    public LCU() {
    }

    @Override
    public void run() {
        Functions.dbg(UWifiServerMain.LOG_LEVEL, "<LCU.run> counting...");

        ArrayListMultimap<String, MonitorDataContainer> clientMonitorList = ArrayListMultimap.create();
        {//region 
            Functions.dbg(UWifiServerMain.LOG_LEVEL, "<LCU.run> getting client monitor list");
            ArrayList<String> clients = DBMakeQuery.getSnappedClients(UWifiServerMain.jdbc, null);
            if (clients == null) {
                return;
            }

            //for each client get list of linked monitors
            for (String client : clients) {
                //Functions.dbg(UWifiServerMain.LOG_LEVEL,"<LCU.run> getting monitors for client:"+client);

                String jsonResult = DBMakeQuery.getSnappedClientMonitors(UWifiServerMain.jdbc, null, client);
                if (jsonResult == null) {
                    clientMonitorList.put(client, null);
                    continue;
                }
                //Functions.dbg(UWifiServerMain.LOG_LEVEL, "<LCU.run> " + jsonResult);
                
                //parse received monitor data from json to MonitorDataContainer
                JSONObject json = null;
                try {
                    json = (JSONObject) new JSONParser().parse(jsonResult);

                    JSONArray monitors = (JSONArray) json.get("result");

                    //for each monitor parse data
                    for (Object monitor : monitors) {
                        JSONObject data = (JSONObject) monitor;
                        MonitorDataContainer tempMonitorContainer = new MonitorDataContainer();
                        
                        for (Object dataEntryObj : data.entrySet()) {
                            Map.Entry dataEntry = (Map.Entry) dataEntryObj;
                            String dataEntryKey = (String) dataEntry.getKey();
                            Object dataEntryValue = dataEntry.getValue();

                            if (dataEntryKey.equalsIgnoreCase("mac")) {
                                tempMonitorContainer.mac = dataEntryValue.toString();
                                
                            } else if (dataEntryKey.equalsIgnoreCase("zone")) {
                                tempMonitorContainer.zone = dataEntryValue.toString();
                                
                            } else if (dataEntryKey.equalsIgnoreCase("channel")) {
                                tempMonitorContainer.channel = Integer.parseInt(dataEntryValue.toString());
                            
                            } else if (dataEntryKey.equalsIgnoreCase("pos")) {
                                JSONObject posObj = (JSONObject) dataEntryValue;
                                tempMonitorContainer.position = new Point2D((Double)posObj.get("x"),(Double) posObj.get("y"));
                            
                            } else {
                                Functions.printLog("\t\tunknown setting[" + dataEntryKey + "] value:" + dataEntryValue.toString());
                            }//endif
                        }//end of for
                        clientMonitorList.put(client, tempMonitorContainer);
                        
                    }//end for each monitor
                    //Functions.dbg(UWifiServerMain.LOG_LEVEL," <LCU.run.parsejson> :"+msg_type.get(1));        

                } catch (ParseException ex) {
                    Functions.wrn(UWifiServerMain.LOG_LEVEL, " <LCU.run.parsejson> Failed to parse received JSON message[" + jsonResult + "]. E:" + ex.getLocalizedMessage());
                    return;
                }//end of try catch

            }//end for each client

        }//end region

        //print contents of map
        for (String client : clientMonitorList.keySet()) {
            Functions.dbg(UWifiServerMain.LOG_LEVEL, "<LCU.run> Monitor count:"+clientMonitorList.get(client).size());

            for (MonitorDataContainer monitor : clientMonitorList.get(client)) {
                Functions.dbg(UWifiServerMain.LOG_LEVEL, "<LCU.run> {client:" + client + ", monitor:" + monitor.toJSON() + "}");

            }
            
        }
        
        //escape
        if (clientMonitorList != null) {
            return;
        }

        Point2D anchorNode1 = new Point2D(13f, 13f);
        Point2D anchorNode2 = new Point2D(16f, 40f);
        Point2D anchorNode3 = new Point2D(39f, 28f);

        Point2D mobileNode1 = new Point2D(0f, 0f);

        TrendEquation trend = new TrendEquation(-7.4f, 67.41f);

        float[] distances = {
            0.001f,
            0.5f,
            1.0f,
            1.5f,
            2.0f,
            2.5f,
            3.0f,
            3.5f,
            4.0f
        };
        for (float dist : distances) {
            Functions.dbg(UWifiServerMain.LOG_LEVEL, "TrendRSSI: " + trend.RSSIFromDistance(dist));
        }

        float[] rssi_readings = {
            -16.29f,
            -62.28f,
            -67.41f,
            -70.41f,
            -72.54f,
            -74.19f,
            -75.54f,
            -76.68f,
            -77.67f
        };
        for (float rssi : rssi_readings) {
            Functions.dbg(UWifiServerMain.LOG_LEVEL, "TrendDistance: " + trend.DistanceFromRSSI(rssi));
        }


        Functions.dbg(UWifiServerMain.LOG_LEVEL, "Distance from AN1 to AN2: " + anchorNode1.distance(anchorNode2));

        Functions.dbg(UWifiServerMain.LOG_LEVEL, "Unknown position: " + Location.getPosition(anchorNode1, anchorNode2, anchorNode3, 16.1f, 16.1f, 16.1f).toString());

    }
}
