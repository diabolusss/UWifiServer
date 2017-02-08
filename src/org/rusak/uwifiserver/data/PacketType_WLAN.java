/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rusak.uwifiserver.data;

/**
 *
 * @author colt
 */
public class PacketType_WLAN {    
//Frame control subfield[2Bytes]
//	   2b                2b      4b       1b    1b       1b        1b       1b      1b         1b    1b
// | protocol version | type | subtype| ToDS | FromDS | MoreFrag | Retry | PwrMgt | MoreData | WEP | Order |
//						   \ + /
//                 Frame type/MAC service
//	Protocol Version:
//		zero for 802.11 standard
//
//	Type= frame type{b3 b2}:
//		 management
//		 control
//		 data
//		 reserved
//	Subtype = frame sub-type{b7 b6 b5 b4}
//
    
    /*
	#define WLAN_FRAME_FC_VERSION_MASK	0x0003 0000_0011
	#define WLAN_FRAME_FC_TYPE_MASK		0x000C 0000_1100
	#define WLAN_FRAME_FC_STYPE_MASK	0x00F0 0000_1111
	#define WLAN_FRAME_FC_STYPE_QOS		0x0080 0000_1000	
     */
    static final int WLAN_FRAME_FC_VERSION_MASK    = 0x0003;
    static final int WLAN_FRAME_FC_TYPE_MASK       = 0x000C;
    static final int WLAN_FRAME_FC_STYPE_MASK      = 0x00F0;
    static final int WLAN_FRAME_FC_STYPE_QOS       = 0x0080;    
        
    
    /* internal use only        
        #define _FC_TYPE_MGMT			0x0
        #define _FC_TYPE_CTRL			0x1
        #define _FC_TYPE_DATA			0x2
     */
    /*  
      #define WLAN_FRAME_TYPE_MGMT		_WLAN_FRAME_FC(_FC_TYPE_MGMT, 0x0)
      #define WLAN_FRAME_TYPE_CTRL		_WLAN_FRAME_FC(_FC_TYPE_CTRL, 0x0)
      #define WLAN_FRAME_TYPE_DATA		_WLAN_FRAME_FC(_FC_TYPE_DATA, 0x0)
    */
    static final int WLAN_FRAME_TYPE_MGMT = buildFrameControlFrame(0x0, 0x0);
    static final int WLAN_FRAME_TYPE_CTRL = buildFrameControlFrame(0x1, 0x0);
    static final int WLAN_FRAME_TYPE_DATA = buildFrameControlFrame(0x2, 0x0);
    
    /*
        #define DATA_NAME_INDEX(_i) (((_i) & WLAN_FRAME_FC_STYPE_MASK)>>4)
        #define MGMT_NAME_INDEX(_i) (((_i) & WLAN_FRAME_FC_STYPE_MASK)>>4)
        #define CTRL_NAME_INDEX(_i) ((((_i) & WLAN_FRAME_FC_STYPE_MASK)>>4)-7)
     */
    public enum MGMT {
        WLAN_FRAME_ASSOC_REQ(0x0, "ASOCRQ", "Association request"),     //{ 'a' },
        WLAN_FRAME_ASSOC_RESP(0x1, "ASOCRP", "Association response" ),    //{ 'A'},
        WLAN_FRAME_REASSOC_REQ(0x2, "REASRQ", "Reassociation request"),   //{ 'a' },
        WLAN_FRAME_REASSOC_RESP(0x3, "REASRP", "Reassociation response"),  //{ 'A' },
        WLAN_FRAME_PROBE_REQ(0x4, "PROBRQ", "Probe request"),     //{ 'p' },
        WLAN_FRAME_PROBE_RESP(0x5, "PROBRP", "Probe response"),    //{ 'P' },
        WLAN_FRAME_TIMING(0x6, "TIMING", "Timing Advertisement"),        //{ 'T' },
       // 0x0070(0x7),                 //{ '-', "-RESV-", "RESERVED" },
        WLAN_FRAME_BEACON(0x8, "BEACON", "Beacon"),        //{ 'B' },
        WLAN_FRAME_ATIM(0x9, "ATIM", "ATIM"),          //{ 't' },
        WLAN_FRAME_DISASSOC(0xa, "DISASC", "Disassociation"),      //{ 'D' },
        WLAN_FRAME_AUTH(0xb, "AUTH", "Authentication"),          //{ 'u' },
        WLAN_FRAME_DEAUTH(0xc, "DEAUTH", "Deauthentication"),        //{ 'U' },
        WLAN_FRAME_ACTION(0xd, "ACTION", "Action"),        //{ 'C' },
        WLAN_FRAME_ACTION_NOACK(0xe, "ACTNOA", "Action No Ack")   //{ 'c' },
        ;
	
        public int stype; 
        public String name;
        public String description;
      
        private MGMT(int stype, String name, String description) {
            this.stype = stype; 
            this.name = name;
            this.description = description;
        }  
        
        static MGMT getFrameSTypeByFC(int fc){
            int index = (((fc) & WLAN_FRAME_FC_STYPE_MASK)>>4);
            for(MGMT frame: MGMT.values()){
                //System.out.println("Frame.stype: "+frame.stype+", index: " + index);
                
                if(frame.stype == index){
                    return frame;
                }
            }
            return null;
        }
        
    };
    
    
    public enum CTRL{		
        WLAN_FRAME_CTRL_WRAP(0x7, "CTWRAP", "Control Wrapper"),     //,{ 'w' },
        WLAN_FRAME_BLKACK_REQ(0x8, "BACKRQ", "Block Ack Request"),     //,{ 'b' },
        WLAN_FRAME_BLKACK(0x9, "BACK",   "Block Ack"),     //,{ 'B' },
        WLAN_FRAME_PSPOLL(0xa, "PSPOLL", "PS-Poll"),     //,{ 's' },
        WLAN_FRAME_RTS(0xb, "RTS",   "RTS"),     //,{ 'R' },
        WLAN_FRAME_CTS(0xc, "CTS",   "CTS"),     //,{ 'C' },
        WLAN_FRAME_ACK(0xd, "ACK", "ACK"),     //,{ 'K' },
        WLAN_FRAME_CF_END(0xe, "CFEND", "CF-End"),     //,{ 'f' },
        WLAN_FRAME_CF_END_ACK(0xf, "CFENDK", "CF-End + CF-Ack")//{ 'f' },
        ;
	
        public int stype; 
        public String name;
        public String description;
      
        private CTRL(int stype, String name, String description) {
            this.stype = stype; 
            this.name = name;
            this.description = description;
        }  
        static CTRL getFrameSTypeByFC(int fc){
            int index = (((fc) & WLAN_FRAME_FC_STYPE_MASK)>>4);
            for(CTRL frame: CTRL.values()){
                //System.out.println("Frame.stype: "+frame.stype+", index: " + index);
                
                if(frame.stype == index){
                    return frame;
                }
            }
            return null;
        }
    };
    
    public enum DATA{	
        WLAN_FRAME_DATA(0x0, "DATA", "Data"),     //{ 'D' },
        WLAN_FRAME_DATA_CF_ACK(0x1, "DCFACK", "Data + CF-Ack"),     //{ 'F' },	
        WLAN_FRAME_DATA_CF_POLL(0x2, "DCFPLL", "Data + CF-Poll"),     //{ 'F' },
        WLAN_FRAME_DATA_CF_ACKPOLL(0x3, "DCFKPL", "Data + CF-Ack + CF-Poll"),     //{ 'F' },
        WLAN_FRAME_NULL(0x4, "NULL", "Null (no data)"),     //{ 'n' },
        WLAN_FRAME_CF_ACK(0x5, "CFACK", "CF-Ack (no data)"),     //{ 'f' },
        WLAN_FRAME_CF_POLL(0x6, "CFPOLL", "CF-Poll (no data)"),     //{ 'f' },
        WLAN_FRAME_CF_ACKPOLL(0x7, "CFCKPL", "CF-Ack + CF-Poll (no data)" ),     //{ 'f'},
        WLAN_FRAME_QDATA(0x8, "QDATA", "QoS Data"),     //{ 'Q' },
        WLAN_FRAME_QDATA_CF_ACK(0x9, "QDCFCK", "QoS Data + CF-Ack"),     //{ 'F' },
        WLAN_FRAME_QDATA_CF_POLL(0xa, "QDCFPL", "QoS Data + CF-Poll"),     //{ 'F' },
        WLAN_FRAME_QDATA_CF_ACKPOLL(0xb, "QDCFKP", "QoS Data + CF-Ack + CF-Poll"),     //{ 'F' },
        WLAN_FRAME_QOS_NULL(0xc, "QDNULL", "QoS Null (no data)"),     //{ 'N' },
        //0x00D0(0xd),     //{ '-', "-RESV-", "RESERVED" },
        WLAN_FRAME_QOS_CF_POLL(0xe, "QCFPLL", "QoS CF-Poll (no data)"),     //{ 'f' },
        WLAN_FRAME_QOS_CF_ACKPOLL(0xf, "QCFKPL", "QoS CF-Ack + CF-Poll (no data)")//{ 'f' },
        ;
	
        public int stype; 
        public String name;
        public String description;
      
        private DATA(int stype, String name, String description) {
            this.stype = stype; 
            this.name = name;
            this.description = description;
        }  
        static DATA getFrameSTypeByFC(int fc){
            int index = (((fc) & WLAN_FRAME_FC_STYPE_MASK)>>4);
            for(DATA frame: DATA.values()){
                //System.out.println("Frame.stype: "+frame.stype+", index: " + index);
                if(frame.stype == index){
                    return frame;
                }
            }
            return null;
        }      
    };    
    
    //#define PKT_WLAN_FLAG_WEP	0x1
    //#define PKT_WLAN_FLAG_RETRY	0x2
    //network.h:#define PKT_WLAN_FLAG_WPA	0x4
    //network.h:#define PKT_WLAN_FLAG_RSN	0x8
    static final byte PKT_WLAN_FLAG_WEP =	0x1;
    static final byte PKT_WLAN_FLAG_RETRY =	0x2;
    static final byte PKT_WLAN_FLAG_WPA =	0x4;
    static final byte PKT_WLAN_FLAG_RSN =	0x8;
    static final byte PKT_WLAN_FLAG_HT40PLUS =	0x10;
    

    
    //#define _WLAN_FRAME_FC(_type, _stype)	(((_type) << 2) | ((_stype) << 4))
    //Subtype = frame sub-type{b7 b6 b5 b4}
    //Type= frame type{b3 b2}:
    static int buildFrameControlFrame(int type, int subtype){
        return (((type) << 2) | ((subtype) << 4));
    }
    
    
    //#define WLAN_FRAME_IS_MGMT(_fc)		(((_fc) & WLAN_FRAME_FC_TYPE_MASK) == WLAN_FRAME_TYPE_MGMT)
    static boolean isMGMT(int frame_control){
        return (((frame_control) & WLAN_FRAME_FC_TYPE_MASK) == WLAN_FRAME_TYPE_MGMT);
    }
    
    //#define WLAN_FRAME_IS_CTRL(_fc)		(((_fc) & WLAN_FRAME_FC_TYPE_MASK) == WLAN_FRAME_TYPE_CTRL)
    static boolean isCTRL(int frame_control){
        return (((frame_control) & WLAN_FRAME_FC_TYPE_MASK) == WLAN_FRAME_TYPE_CTRL);
    }
    
    //#define WLAN_FRAME_IS_DATA(_fc)		(((_fc) & WLAN_FRAME_FC_TYPE_MASK) == WLAN_FRAME_TYPE_DATA)
    static boolean isDATA(int frame_control){
        return (((frame_control) & WLAN_FRAME_FC_TYPE_MASK) == WLAN_FRAME_TYPE_DATA);
    }
    
    //#define WLAN_FRAME_IS_QOS(_fc)		(((_fc) & WLAN_FRAME_FC_STYPE_MASK) == WLAN_FRAME_FC_STYPE_QOS)
    static boolean isQOS(int frame_control){
        return (((frame_control) & WLAN_FRAME_FC_STYPE_MASK) == WLAN_FRAME_FC_STYPE_QOS);
    }    
    
    static boolean isWEP(int bitmask){
        return (((bitmask) & PKT_WLAN_FLAG_WEP) > 0);
    }
    static boolean isRETRY(int bitmask){
        return (((bitmask) & PKT_WLAN_FLAG_RETRY) > 0);
    }
    static boolean isWPA(int bitmask){
        return (((bitmask) & PKT_WLAN_FLAG_WPA) > 0);
    }
    static boolean isRSN(int bitmask){
        return (((bitmask) & PKT_WLAN_FLAG_RSN) > 0);
    }    
    static boolean isHT40PLUS(int bitmask){
        return (((bitmask) & PKT_WLAN_FLAG_HT40PLUS) > 0);
    }
    /*
	if (np->wlan_flags & PKT_WLAN_FLAG_WEP)
		p.wlan_wep = 1;
	if (np->wlan_flags & PKT_WLAN_FLAG_RETRY)
		p.wlan_retry = 1;
	if (np->wlan_flags & PKT_WLAN_FLAG_WPA)
		p.wlan_wpa = 1;
	if (np->wlan_flags & PKT_WLAN_FLAG_RSN)
		p.wlan_rsn = 1;
                */
}
