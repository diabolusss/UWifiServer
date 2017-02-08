package org.rusak.uwifiserver.data;

import functions.Functions;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  Also, Java's numbers are always stored in network-byte-order(big endian) no matter the platform 
 * that you run Java upon (the JVM spec requires a specific byte order).
 * @author colt
 */
public class PacketInfoHorst {
    //size: max 80211 frame (2312) + space for prism2 header (144)
    // or radiotap header (usually only 26) + some extra 
    public static final int HORST_PACKET_LEN = 2312 + 200;
    static final byte MAC_LEN = 6;
    static final byte WLAN_MAX_SSID_LEN = 34;

    /* unsigned */ char proto_version;
    /* unsigned */ char proto_type;
    public byte[] wlan_monitor_mac;
    /* unsigned */ char version;
    /* general */
    /* unsigned */ int		pkt_types;	/* bitmask of packet types */

    /* wlan phy (from radiotap) */
    public int			phy_signal;//,phy_noise;	/* signal strength (usually dBm) */
    /* unsigned */ int		//phy_snr,
            phy_rate;	/* physical rate * 10 (=in 100kbps) */
    /* unsigned */ char		phy_rate_idx;	/* MCS index */
    /* unsigned */ char		phy_rate_flags;	/* MCS flags */
    /* unsigned */ int		phy_freq;	/* frequency from driver */
    /* unsigned */ int		phy_flags;	/* A, B, G, shortpre */

    /* wlan mac */
    /* unsigned */ int		wlan_len;	/* packet length */
    public /* u_int16_t */int		wlan_type;	/* frame control field */
    ///* unsigned */ char		wlan_src[MAC_LEN]; /* transmitter (TA) */
    ///* unsigned */ char		wlan_dst[MAC_LEN]; /* receiver (RA) */
    ///* unsigned */ char		wlan_bssid[MAC_LEN];
    //char			wlan_essid[WLAN_MAX_SSID_LEN];
    public byte[] 
            wlan_src,
            wlan_dst,
            wlan_bssid
            ;
    String wlan_essid;
    /* u_int64_t */long		wlan_tsf;	/* timestamp from beacon */
    /* unsigned */ int		wlan_bintval;	/* beacon interval */
    /* unsigned */ int		wlan_mode;	/* AP, STA or IBSS */
    public /* unsigned */ char		wlan_channel;	/* channel from beacon, probe */
    /* unsigned */ char		wlan_chan_width;
    /* unsigned */ char		wlan_tx_streams;
    /* unsigned */ char		wlan_rx_streams;
    /* unsigned */ char		wlan_qos_class;	/* for QDATA frames */
    /* unsigned */ int		wlan_nav;	/* frame NAV duration */
    /* unsigned */ int		wlan_seqno,wlan_flags;	/* sequence number */

    /* flags */
    /* unsigned */ int		wlan_WEP,//:1,	/* WEP on/off */
                            wlan_RETRY,//:1,
                            wlan_WPA,//:1,
                            wlan_RSN,
                            wlan_HT40PLUS
            ;//:1;

    /* batman-adv */
    /* unsigned */ char		bat_version;
    /* unsigned */ char		bat_packet_type;
    /* unsigned */ char		bat_gw;//:1;

    /* IP */
    /* unsigned */ int		ip_src;
    /* unsigned */ int		ip_dst;
    /* unsigned */ int		tcpudp_port;
    /* unsigned */ int		olsr_type;
    /* unsigned */ int		olsr_neigh;
    /* unsigned */ int		olsr_tc;

   /* unsigned */ char bat_flags,bat_pkt_type;

    /* calculated from other values */
    /* unsigned */ int		pkt_duration;	/* packet "airtime" */
    int			pkt_chan_idx;	/* received while on channel */
    int			wlan_retries;	/* retry count for this frame */  
    
    //local public variables
    public float phy_signalf=0;
    
    public PacketInfoHorst(byte[] bytes) throws IOException {
        //qfinal PacketInfoHorst data = new PacketInfoHorst();
        final ByteBuffer buf = ByteBuffer.wrap(bytes);

        // restore packet from bytes        
        // Example to convert unsigned short to a positive int
        //data.tc_2_as_int = buf.getShort() & 0xffff;
        buf.order(ByteOrder.LITTLE_ENDIAN);
        
        this.proto_version = (char) (buf.get() & 0xff);
        this.proto_type = (char) (buf.get() & 0xff);
        
        this.version = (char) (buf.get() & 0xff);
        
        this.wlan_monitor_mac = new byte[MAC_LEN];
        buf.get(this.wlan_monitor_mac);
                
        this.pkt_types = buf.getInt() & 0xffff;
        //this.pkt_types = Integer.reverse(this.pkt_types);
        
        this.phy_signal = buf.getInt();
        //this.phy_signal = Integer.reverse(this.phy_signal);
        
        //this.phy_noise = buf.getInt(); 
        //this.phy_noise = Integer.reverse(this.phy_noise);
        
        //this.phy_snr = buf.getInt() & 0xffff;
        //this.phy_snr = Integer.reverse(this.phy_snr);
        
        this.phy_rate = buf.getInt() & 0xffff;
        //this.phy_rate = Integer.reverse(this.phy_rate);
                
        this.phy_rate_idx = (char) (buf.get() & 0xff);
        
        this.phy_rate_flags = (char) (buf.get() & 0xff);
        
        this.phy_freq = buf.getInt() & 0xffff;
        //this.phy_freq = Integer.reverse(this.phy_freq);
        
        this.phy_flags = buf.getInt() & 0xffff;
        //this.phy_flags = Integer.reverse(this.phy_flags);
        
        this.wlan_len = buf.getInt() & 0xffff;
        //this.wlan_len = Integer.reverse(this.wlan_len);
        
        this.wlan_type = buf.getInt() & 0xffff;
        //this.wlan_type = Integer.reverse(this.wlan_type);
                
        this.wlan_src = new byte[MAC_LEN];
        buf.get(this.wlan_src);
        //this.wlan_src = Functions_custom.macToString(tmpstr);//new String(tmpstr, Charset.forName("UTF-8"));
        
        //Arrays.fill( tmpstr, (byte) 0 );
        this.wlan_dst = new byte[MAC_LEN];
        buf.get(this.wlan_dst); 
        //this.wlan_dst = new String(tmpstr, Charset.forName("UTF-8")); 
        
        //Arrays.fill( tmpstr, (byte) 0 );
        this.wlan_bssid = new byte[MAC_LEN];
        buf.get(this.wlan_bssid);
        //this.wlan_bssid = new String(tmpstr, Charset.forName("UTF-8"));
        
        byte[] tmpstr = new byte[WLAN_MAX_SSID_LEN];
        buf.get(tmpstr);        
        this.wlan_essid = new String(tmpstr, Charset.forName("UTF-8")).replace('\0', '#');
        //this.wlan_essid = Functions_custom.removeDuplicates(this.wlan_essid);
        
        this.wlan_tsf = buf.getLong() & 0xffffffff;//long
        //this.wlan_tsf = Long.reverse(this.wlan_tsf);
        
        this.wlan_bintval = buf.getInt() & 0xffff;
        //this.wlan_bintval = Integer.reverse(this.wlan_bintval);
        
        this.wlan_mode = buf.getInt() & 0xffff;
        //this.wlan_mode = Integer.reverse(this.wlan_mode);
                
        this.wlan_channel = (char) (buf.get() & 0xff);
        this.wlan_chan_width = (char) (buf.get() & 0xff);
        this.wlan_tx_streams = (char) (buf.get() & 0xff);
        this.wlan_rx_streams = (char) (buf.get() & 0xff);
        
        this.wlan_qos_class = (char) (buf.get() & 0xff);
        
        this.wlan_nav = buf.getInt() & 0xffff;
        //this.wlan_nav = Integer.reverse(this.wlan_nav);
        
        this.wlan_seqno = buf.getInt() & 0xffff;
        //this.wlan_seqno = Integer.reverse(this.wlan_seqno)  & 0xffff;
        
        this.wlan_flags = buf.getInt() & 0xffff;
        //this.wlan_flags = Integer.reverse(this.wlan_flags);
        if(PacketType_WLAN.isWEP(this.wlan_flags)){
            this.wlan_WEP = 1;
        }
        if(PacketType_WLAN.isWPA(this.wlan_flags)){
            this.wlan_WPA = 1;
        }
        if(PacketType_WLAN.isRSN(this.wlan_flags)){
            this.wlan_RSN = 1;
        }
        if(PacketType_WLAN.isRETRY(this.wlan_flags)){
            this.wlan_RETRY = 1;
        }
        if(PacketType_WLAN.isHT40PLUS(this.wlan_flags)){
            this.wlan_HT40PLUS = 1;
        }
        
        this.ip_src = buf.getInt() & 0xffff;
        //tmpstr = new byte[4]; 
        //buf.get(tmpstr);
        //System.out.println(Functions_custom.ipToString(tmpstr));
        
        this.ip_dst = buf.getInt() & 0xffff;
        this.tcpudp_port = buf.getInt() & 0xffff;
        //this.tcpudp_port = Integer.reverse(this.tcpudp_port);
        
        this.olsr_type = buf.getInt() & 0xffff;
        //this.olsr_type = Integer.reverse(this.olsr_type);
        
        this.olsr_neigh = buf.getInt() & 0xffff;
        //this.olsr_neigh = Integer.reverse(this.olsr_neigh);
        
        this.olsr_tc = buf.getInt() & 0xffff;
        //this.olsr_tc = Integer.reverse(this.olsr_tc);        
        
        this.bat_flags = (char) (buf.get() & 0xff);
        this.bat_pkt_type = (char) (buf.get() & 0xff);
              
    }
    

    public PacketInfoHorst createFromBytes(byte[] bytes) throws IOException {
        final PacketInfoHorst data = new PacketInfoHorst(bytes);
      
        return data;        
    }
    
    public void println(){
        System.out.println("<<<<<<<<<< PACKET BEGIN >>>>>>>>>>>>>>>");    
        System.out.println("proto_version :"+String.format("%04x", (int) proto_version));    
        System.out.println("proto_type :"+
                //String.format("%04x", (int) proto_type)
                PacketType_HORST.num2Type(proto_type)                
                ); 
        System.out.println("version :"+String.format("%04x", (int) version));
        System.out.println("wlan monitor mac :"+wlan_monitor_mac+"; "+Functions.macToString(wlan_monitor_mac)); 
        System.out.println("pkt_types :"+pkt_types+" ["
                +Integer.toBinaryString(pkt_types)+"]" 
                ); 	/* bitmask of packet types */

    /* wlan phy (from radiotap) */
        System.out.println("phy_signal :"+phy_signal);
        //System.out.println("phy_noise :"+phy_noise); 	/* signal strength (usually dBm) */
        //System.out.println("phy_snr :"+phy_snr);
        System.out.println("phy_rate :"+phy_rate); 	/* physical rate * 10 (=in 100kbps) */        
        System.out.println("phy_rate_idx :"+(int)phy_rate_idx); 	/* MCS index */
        System.out.println("phy_rate_flags :"+(int)phy_rate_flags); 	/* MCS flags */
        System.out.println("phy_freq :"+phy_freq); 	/* frequency from driver */
        System.out.println("phy_flags :"+phy_flags); 	/* A, B, G, shortpre */

    /* wlan mac */
        System.out.println("wlan_len :"+wlan_len); 	/* packet length */
        System.out.println("wlan_type :"+wlan_type+" "
                +new Frame_t(wlan_type).toString()
                ); 	/* frame control field */
    ///* unsigned */ char		wlan_src[MAC_LEN]);  /* transmitter (TA) */
    ///* unsigned */ char		wlan_dst[MAC_LEN]);  /* receiver (RA) */
    ///* unsigned */ char		wlan_bssid[MAC_LEN]); 
    //char			wlan_essid[WLAN_MAX_SSID_LEN]); 
        System.out.println("wlan_src :"+wlan_src+"; "+Functions.macToString(wlan_src));
        System.out.println("wlan_dst :"+wlan_dst+"; "+Functions.macToString(wlan_dst));
        System.out.println("wlan_bssid :"+wlan_bssid+"; "+Functions.macToString(wlan_bssid));
        System.out.println("wlan_essid :"+wlan_essid);
        
        System.out.println("wlan_tsf :"+wlan_tsf); 	/* timestamp from beacon */
        System.out.println("wlan_bintval :"+wlan_bintval); 	/* beacon interval */
        System.out.println("wlan_mode :"+wlan_mode); 	/* AP, STA or IBSS */
        System.out.println("wlan_channel :"+
                //String.format("%04x", (int) wlan_channel)
                (int) wlan_channel
                ); 	/* channel from beacon, probe */
        System.out.println("wlan_qos_class :"+
                //String.format("%04x", (int) wlan_qos_class)
                (int) wlan_qos_class
                ); 	/* for QDATA frames */
        System.out.println("wlan_nav :"+wlan_nav); 	/* frame NAV duration */
        System.out.println("wlan_seqno :"+wlan_seqno);
        System.out.println("wlan_flags :"+wlan_flags); 	/* sequence number */

    /* flags */
        System.out.println("wlan_wep :"+wlan_WEP);	/* WEP on/off */
        System.out.println("wlan_retry :"+wlan_RETRY);
        System.out.println("wlan_wpa :"+wlan_WPA);
        System.out.println("wlan_rsn :"+wlan_RSN);
        System.out.println("wlan_ht40plus :"+wlan_HT40PLUS);

    /* batman-adv 
        System.out.println("bat_version :"+bat_version); 
        System.out.println("bat_packet_type :"+bat_packet_type); 
        System.out.println("bat_gw :"+bat_gw); //:1); 
*/
    /* IP */
        System.out.println("ip_src :"+ip_src+"; "+Functions.ipToString(ip_src)); 
        System.out.println("ip_dst :"+ip_dst+"; "+Functions.ipToString(ip_dst));  
        System.out.println("tcpudp_port :"+tcpudp_port); 
        System.out.println("olsr_type :"+olsr_type); 
        System.out.println("olsr_neigh :"+olsr_neigh); 
        System.out.println("olsr_tc :"+olsr_tc); 

        System.out.println("bat_flags :"+String.format("%04x", (int) bat_flags));
        System.out.println("bat_pkt_type :"+String.format("%04x", (int) bat_pkt_type)); 

    /* calculated from other values 
        System.out.println("pkt_duration :"+String.format("%04x", (int) pkt_duration)); 	
        System.out.println("pkt_chan_idx :"+String.format("%04x", (int) pkt_chan_idx)); 	
        System.out.println("wlan_retries :"+wlan_retries); 	
        */
        
        System.out.println("<<<<<<<<<< PACKET END >>>>>>>>>>>>>>>");  
    }

}
