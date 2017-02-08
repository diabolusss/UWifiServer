/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rusak.uwifiserver.data;

import org.rusak.localization.math.Point2D;

/**
 *
 * @author colt
 */
public class MonitorDataContainer {
    public String mac="";
    public int channel=0;
    public String zone="";
    public Point2D position = new Point2D();
    public boolean fixed = true;
    
    
    @Override
    public String toString(){
        return null;        
    }
    
    public String toJSON(){
        return "{\"type\":\"wlan_monitor\",\"data\":"
                    +"{"
                        +"\"mac\":\""   +mac+"\""
                        +",\"channel\":"+channel
                        +",\"zone\":\"" +zone+"\""
                        +",\"position\":"+position.toJSON()
                        +",\"fixed\":\""+fixed+"\""
                    +"}"
                +"}";
    }
    
}
