
/**
 * template for things with coordinates, everything is overriden
 *
 * Alex Schwartz
 * j8 6/1/21
 */
public class Thing
{
    private double x;
    private double y;
    
    public Thing(){
        x = Math.random();
        y = Math.random();
    }
    
    public Thing(double xCord, double yCord){
        x = xCord;
        y = yCord;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    protected void setX(double xCord){
        x = xCord;
    }
    
    protected void setY(double yCord){
        y = yCord;
    }
    
    public void draw(){
        StdDraw.setPenColor(0, 0, 0);
        StdDraw.filledCircle(x, y, .1);
    }
}
