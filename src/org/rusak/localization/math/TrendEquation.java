/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rusak.localization.math;

/**
 *
 * @author colt
 */
/**
     * TrendEquation = k*ln(x)-c, where x is distance
     */
   public class TrendEquation{
        float k;
        float c;

        public TrendEquation(float k, float c) {
            this.k = k;
            this.c = c;
        }
        
        public double RSSIFromDistance(float distance){
            return k*Math.log(distance)-c;
        }
        
        public double DistanceFromRSSI(float rssi){
            return Math.exp((rssi+c)/k);
        }
    }
