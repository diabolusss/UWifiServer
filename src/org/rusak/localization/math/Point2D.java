/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rusak.localization.math;

/**
 *
 * @author colt
 */
public class Point2D{
        float x, y;
        
        public Point2D(){
            this.x=0;
            this.y=0;
        }
        
        public Point2D(float x, float y){
            this.x = x;
            this.y = y;
        }

        public Point2D(double x, double y) {
            this.x = (float)x;
            this.y = (float)y;
        }
        
        @Override
        public String toString(){
            return toJSON();
        }
        public String toJSON(){
            return "{\"x\":"+x
                    + ",\"y\":"+y
                    + "}";
        }

        /*
         * Distance between centers of 2 points
         */
        public double distance(Point2D p2){
            return Math.sqrt((p2.x-this.x)*(p2.x-this.x) + (p2.y-this.y)*(p2.y-this.y));
        }
        
        /*
         * Distance between centers of 2 points taking in count direction
         */
        public double distance(Point2D p, Point2D ex, float i){
            return Math.sqrt((p.x-this.x-i*ex.x)*(p.x-this.x-i*ex.x) + (p.y-this.y-i*ex.y)*(p.y-this.y-i*ex.y));
        }
        
        /*
         * Unit vector in the direction from p1 to p2
         */
        public Point2D getUnitVectorEx(Point2D p2){
            return new Point2D(
                    (p2.x-this.x)/distance(p2),
                    (p2.y-this.y)/distance(p2)
                   )
                   ;
        }
        
        /*
         * Unit vector in the y direction ... 
         */
        public Point2D getUnitVectorEy(Point2D p2, Point2D p3){
           Point2D e_x = getUnitVectorEx(p2);
           float i = (float)getSignedMagnitudeX(p2, p3);
           double mod = distance(p3,e_x,i);
           Point2D e_y = new Point2D();
           e_y.x =  (float) ( (p3.x-this.x-i*e_x.x)/mod );
           e_y.y =  (float) ( (p3.y-this.y-i*e_x.y)/mod );
           return e_y;
        }
        
        /*
         * Signed magnitude of the x component from p1 to p3
         * Magnitude is vector size(length):
         *  ||a|| = sqrt(a_x^2 + a_y^2)
         */
        public double getSignedMagnitudeX(Point2D p2, Point2D p3){
            Point2D e_x= getUnitVectorEx(p2);
            return (e_x.x*(p3.x-this.x) + e_x.y*(p3.y-this.y));
        }
        
        /*
         * Signed magnitude of the y component from p1 to p3
         * Magnitude is vector size(length)
         *  ||a|| = sqrt(a_x^2 + a_y^2)
         */
        public double getSignedMagnitudeY(Point2D p2, Point2D p3){
            Point2D e_y = getUnitVectorEy(p2, p3);
            return (e_y.x*(p3.x-this.x) + e_y.y*(p3.y-this.y));
        }     
        
        
    }
