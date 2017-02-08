/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rusak.uwifiserver.data;

/**
 *
 * @author colt
 */
public  class PacketType_HORST {
    //public enum HorstPacketType{
    //    PROTO_PKT_INFO(0);
    //    private int value; 
        
    //    private HorstPacketType(int value) {
    //        this.value = value; 
    //    }
    //};
    
    public static final byte PROTO_PKT_INFO    = 0;
    public static final byte PROTO_CHAN_LIST   = 1;
    public static final byte PROTO_CONF_CHAN   = 2;
    public static final byte PROTO_CONF_FILTER	= 3;
    
    public static String num2Type(int number){
        switch(number){
            case PROTO_PKT_INFO: 
                return "PROTO_PKT_INFO";
                //break;
            case PROTO_CHAN_LIST: 
                return "PROTO_CHAN_LIST";
                //break;
            case PROTO_CONF_CHAN: 
                return "PROTO_CONF_CHAN";
                //break;
            case PROTO_CONF_FILTER: 
                return "PROTO_CONF_FILTER";
                //break;
        }
        
        return null;        
    }

}
