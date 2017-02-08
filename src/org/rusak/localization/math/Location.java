/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rusak.localization.math;

/**
 *
 * @author colt
 */
public class Location {
    
    /*
     * p{1-3} - known position points
     * pr{1-3} - known distances to unknown node, i.e. radiuses of spheres
     */
    public static Point2D getPosition(Point2D p1, Point2D p2, Point2D p3, float pr1, float pr2, float pr3){
        Point2D pos = new Point2D();
        
        double d12 = p1.distance(p2);
        Point2D e_x = p1.getUnitVectorEx(p2);
        Point2D e_y = p1.getUnitVectorEy(p2, p3);
        double i = p1.getSignedMagnitudeX(p2, p3);
        double j = p1.getSignedMagnitudeY(p2, p3);
        double x = (pr1*pr1 - pr2*pr2 + d12*d12) / (2*d12);
        double y = (pr1*pr1 - pr3*pr3 + i*i + j*j)/(2*j) - i * x/j;
        
        pos.x = (float) (p1.x + x*e_x.x + y*e_y.x);
        pos.y = (float) (p1.y + x*e_x.y + y*e_y.y);
        return pos;
    }
}
