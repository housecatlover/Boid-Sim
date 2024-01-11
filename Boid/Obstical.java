
/**
 * represents an obstical, just stores a location, could store weight
 *
 * Alex Schwartz
 * j8 6/1/21
 */
public class Obstical extends Thing
{

    private double radius;

    public Obstical(double xCord, double yCord, double r){
        super.setX(xCord);
        super.setY(yCord);
        radius = r;
    }
    
    public Obstical(){
        super();
        radius = .025;
    }
    
    public void draw(){
    StdDraw.setPenColor(0, 0, 0);
    StdDraw.filledSquare(super.getX(), super.getY(), radius);
    }
}
