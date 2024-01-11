
/**
 * represents an goal, just stores a location, could store weight
 *
 * Alex Schwartz
 * j8 6/1/21
 */
public class Goal extends Thing
{

    public Goal(double xCord, double yCord){
        super.setX(xCord);
        super.setY(yCord);
    }
    
    public Goal(){
        super();
    }
    
    public void draw(){
     StdDraw.setPenColor(252, 186, 3);
     StdDraw.circle(super.getX(), super.getY(), .06);
    }
}
