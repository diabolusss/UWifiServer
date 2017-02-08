/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rusak.uwifiserver.data;

import functions.Functions;
import org.rusak.uwifiserver.data.PacketType_WLAN.CTRL;
import org.rusak.uwifiserver.data.PacketType_WLAN.DATA;
import org.rusak.uwifiserver.data.PacketType_WLAN.MGMT;
import org.rusak.uwifiserver.run.UWifiServerMain;

/**
 *
 * @author colt
 */
public class Frame_t {
    int fc;
    public String tname;
    int tvalue;
    public String stname;
    int stvalue;
    String stdescription;
    
    Frame_t(){
        fc = 0;
        tname = "null";
        tvalue = 0;
        stname = "null";
        stvalue = 0;
        stdescription = "null";
    }
    
    Frame_t(MGMT sframe){        
        tname = "MGMT";
        tvalue = (PacketType_WLAN.WLAN_FRAME_TYPE_MGMT) >> 2;
        stname = sframe.name;
        stvalue = sframe.stype;
        stdescription = sframe.description;
        fc = PacketType_WLAN.buildFrameControlFrame(tvalue, stvalue);
    }
    
    Frame_t(CTRL sframe){
        tname = "CTRL";
        tvalue = (PacketType_WLAN.WLAN_FRAME_TYPE_CTRL) >> 2;
        stname = sframe.name;
        stvalue = sframe.stype;
        stdescription = sframe.description;
        fc = PacketType_WLAN.buildFrameControlFrame(tvalue, stvalue);
    }
    
    Frame_t(DATA sframe){
        tname = "DATA";
        tvalue = (PacketType_WLAN.WLAN_FRAME_TYPE_DATA) >> 2;
        stname = sframe.name;
        stvalue = sframe.stype;
        stdescription = sframe.description;
        fc = PacketType_WLAN.buildFrameControlFrame(tvalue, stvalue);
    }
    
    public Frame_t(int packet_fc_stype){           
        //Functions.dbg(UWifiServerMain.LOG_LEVEL, "[frame_t]packet_fc_stype="+Integer.toBinaryString(packet_fc_stype));
            
        Frame_t fr = new Frame_t();
        if(PacketType_WLAN.isMGMT(packet_fc_stype)){
            //System.out.println("isMGMT");
            fr = new Frame_t(MGMT.getFrameSTypeByFC(packet_fc_stype));
            
        }else if(PacketType_WLAN.isCTRL(packet_fc_stype)){
            //System.out.println("isCTRL");            
            fr = new Frame_t(CTRL.getFrameSTypeByFC(packet_fc_stype));
            
        }else if(PacketType_WLAN.isDATA(packet_fc_stype)){
            //System.out.println("isDATA");            
            fr = new Frame_t(DATA.getFrameSTypeByFC(packet_fc_stype));
            
        }else { 
            Functions.wrn(UWifiServerMain.LOG_LEVEL, "<Frame_t.init> Bad packet: "+Integer.toBinaryString(packet_fc_stype));
          
        }
        
        fc = packet_fc_stype;
        tname = fr.tname;
        tvalue = fr.tvalue;
        stname = fr.stname;
        stvalue = fr.stvalue;
        stdescription = fr.stdescription;
    }
    
    @Override
    public String toString(){
        return "fc:"+Integer.toBinaryString(fc)+", {type_name:\""+tname+"\","+"type_value:"+Integer.toBinaryString(tvalue)+","+"subtype_name:\""+stname+"\","+"subtype_value:"+Integer.toBinaryString(stvalue)+","+"subtype_description:\""+stdescription+"\"}";
    }
}
