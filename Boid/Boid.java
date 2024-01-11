
/**
 * Simulates a bird, a bird-oid, or boid if you will, follows 3-5 rules to create higher level movement, alignment, sperationm and cohesion
 * with the possible addition of goals seeking and objectect avoidence
 * 
 * Alex Schwartz
 * j8 6/1/21
 */
import java.util.ArrayList;

public class Boid extends Thing
{

    private double angle; //radians, 0 - 2pi, no negitives
    private double vel; //.001 - .02
    final double pi = Math.PI;
    final double toRadians = 2 * pi / 360;
    //customizable values
    final double vision = .075;
    final double wallScale = 2; //multiple for how much clear boids see walls
    final double scale = .3; //just the image
    final double minSpeed = .00075;
    final double maxSpeed = .003;
    //balancing weights, the first is maximum angle change, the second is maximum velocity change for each rule
    final double cohesionWeight = 5 * toRadians, cohesionWeightV = .001;
    final double separationWeight = 7.5 * toRadians, separationWeightV = .001;
    final double alignmentWeight = 10 * toRadians, alignmentWeightV = .002;
    final double wallWeight = 45 * toRadians;
    final double goalWeight = 4 * toRadians, goalWeightV = .01;
    final double obsticalWeight = 30 * toRadians, obsticalWeightV = .06;

    /**
     * Constructor for objects of class Boid in a random location
     */
    public Boid()
    {
        super(); //assigning cords
        angle = Math.random() * 2 * pi;
        vel = Math.random() * (maxSpeed - minSpeed) + minSpeed;
    }

    /**
     * changes angles and velocity based on cohesion, seperation, alighnment and any goals or obsticals
     */
    public void update(double[][] boids, int thisNum, double[][] goals, double[][] obsticals){
        //looking
        double thatX, thatY, angleTo, distance;
        double sumX = 0, sumY = 0, sumAngle = 0, sumVel = 0, sumDistance = 0;
        int sumCount = 0;
        double seperationAngle = angle; //both so seperation happens last, and only 1 force is applied
        for (int i = 0; i < boids.length; i++){
            //determain visibility
            if (i == thisNum) continue; //ignoring iteslf
            thatX = boids[i][0];
            thatY = boids[i][1];
            distance = Math.sqrt(Math.pow(super.getX() - thatX, 2) + Math.pow(super.getY() - thatY, 2));
            if (distance > vision) continue; //ignored if too far away
            angleTo = Math.atan2((thatY - super.getY()), (thatX - super.getX()));
            if (angleTo < 0) angleTo += 2 * pi; //all angles are positive
            if (Math.abs(angle - angleTo) > 3 * pi / 4) continue; //ignored if outside angle of view (+- 135 degrees)
            //find the angles and vels of visible boids
            sumAngle += boids[i][2];
            sumVel += boids[i][3];
            //find center of visible boids
            sumX += thatX; 
            sumY += thatY;
            sumCount += 1;
            //find the distance and direction for each visible boid

            double opAngle = angleTo - pi;
            if (opAngle < 0) opAngle += 2 * pi; //all angles are positive
            seperationAngle += (opAngle - angle) * (1 / distance); //aim directly away with strength inversly proportional to distance
            sumDistance += distance;
        }

        ArrayList<Double> angleForces = new ArrayList<Double>(); //to average the forces
        ArrayList<Double> velForces = new ArrayList<Double>(); //and cap them

        //acting
        if (sumCount != 0) { //if it can't see any boids it can't act on other boids

            //alignment
            double averageAngle = sumAngle / sumCount;
            double averageVel = sumVel / sumCount;
            angleForces.add((averageAngle - angle) * alignmentWeight / (.75 * pi));
            velForces.add((averageVel - vel) * alignmentWeightV / (maxSpeed - minSpeed));

            //cohesion
            double centerOfX = sumX / sumCount;
            double centerOfY = sumY / sumCount;
            double cohesion = Math.atan2(centerOfY, centerOfX); //the angle towards the center of visible boids
            if (cohesion < 0) cohesion += 2 * pi;
            angleForces.add((cohesion - angle) * cohesionWeight / (.75 * pi));
            double centerDistance = Math.sqrt(Math.pow(super.getX() - centerOfX, 2) + Math.pow(super.getY() - centerOfY, 2));
            velForces.add(centerDistance * cohesionWeightV / vision); //changes speed to reach that center

            //seperation
            angleForces.add((seperationAngle - angle) / (.75 * pi) * separationWeight);
            double averageDistance = sumDistance / sumCount;
            //with distance - vision it will slow down when closer, enabling group turns
            velForces.add((averageDistance - vision) / (vision) * separationWeightV); 
            //aims for 1 quarter vision distance, so max influence is 1/4 vision difference

        }
        //walls if they can see one: fly away
        double wallAngle = -1;
        if (super.getX() > (1 - vision*wallScale) && (angle > 1.25 * pi || angle < .75 * pi))
            wallAngle = pi;
        else {
            if (super.getX() < vision*wallScale && (angle > .25 * pi && angle < 1.75 * pi))
                wallAngle = 0;
        }
        if (super.getY() > (1 - vision*wallScale) && (angle < 1.25 * pi || angle > 1.75 * pi))
        { //two walls is so much more complicated
            if (wallAngle == -1) wallAngle = 1.5 * pi;
            else {if (wallAngle == 0) wallAngle = 1.75 * pi;
                else wallAngle = 1.25 * pi;}
        }
        else {
            if (super.getY() < vision*wallScale && (angle > .75 * pi || angle < .25 * pi))
            {
                if (wallAngle == -1) wallAngle = .5 * pi;
                else {if (wallAngle == 0) wallAngle = .25 * pi;
                    else wallAngle = .75 * pi;} 
            }
        }

        if (wallAngle != -1){
            angleForces.add((wallAngle - angle) / (pi) * wallWeight);
            //StdDraw.filledCircle(super.getX(), super.getY(), .05);
        }

        //goal
        if (goals != null){ //common to not include goals or obsticals
            sumX = 0; sumY = 0; int count = 0; sumDistance = 0; //reset some varibles to reuse logic
            for (int i = 0; i < goals.length; i++){
                //determain visibility
                thatX = goals[i][0];
                thatY = goals[i][1];
                distance = Math.sqrt(Math.pow(super.getX() - thatX, 2) + Math.pow(super.getY() - thatY, 2));
                if (distance > vision) continue; //ignored if too far away
                angleTo = Math.atan2((thatY - super.getY()), (thatX - super.getX()));
                if (angleTo < 0) angleTo += 2 * pi; //all angles are positive
                if (Math.abs(angle - angleTo) > 3 * pi / 4) continue; //ignored if outside angle of view (+- 135 degrees)

                count += 1;
                sumX += thatX; 
                sumY += thatY;
                sumDistance += distance;
            }

            if (count != 0){ //cohesion logic
                double averageX = sumX / count;
                double averageY = sumY / count;

                double goalAngle = Math.atan2(averageX, averageY); //the angle towards the center of visible goals
                if (goalAngle < 0) goalAngle += 2 * pi;
                angleForces.add((goalAngle - angle) * goalWeight / (.75 * pi));
                
                double averageDistance = Math.sqrt(Math.pow(super.getX() - averageX, 2) + Math.pow(super.getY() - averageY, 2));
            velForces.add(averageDistance * goalWeightV / vision); //changes speed to reach that center
            }

        }

        //obstical
        if (obsticals != null){ //common to not include goals or obsticals
            sumX = 0; sumY = 0; int count = 0; double obsticalAngle = 0; sumDistance = 0;
            for (int i = 0; i < obsticals.length; i++){
                //determain visibility
                thatX = obsticals[i][0];
                thatY = obsticals[i][1];
                distance = Math.sqrt(Math.pow(super.getX() - thatX, 2) + Math.pow(super.getY() - thatY, 2));
                if (distance > vision) continue; //ignored if too far away
                angleTo = Math.atan2((thatY - super.getY()), (thatX - super.getX()));
                if (angleTo < 0) angleTo += 2 * pi; //all angles are positive
                if (Math.abs(angle - angleTo) > 3 * pi / 4) continue; //ignored if outside angle of view (+- 135 degrees)

                double opAngle = angleTo - pi;
                if (opAngle < 0) opAngle += 2 * pi; //all angles are positive
                obsticalAngle += (opAngle - angle) * (1 / distance); //aim directly away with strength inversly proportional to distance
                sumDistance += distance;
            }

            if (count != 0){ //seperation logic
                angleForces.add((obsticalAngle - angle) / (.75 * pi) * obsticalWeight);
                double averageDistance = sumDistance / sumCount;
                //with distance - vision it will slow down when closer, enabling group turns
                velForces.add((averageDistance - vision) / (vision) * obsticalWeightV); 
                //aims for 1 quarter vision distance, so max influence is 1/4 vision difference
            }

        }

        //Applying suggested vel changes, balanced as fractions of possible force, then the forces are averaged
        //devide by .75pi as that is the maximum angle change, so weight can be max radians altered
        if (angleForces.size() > 0) {
            angle += sum(angleForces) / angleForces.size();
            if (angle > 2 * pi) angle = angle % (2 * pi);
            if (angle < 0) angle += (2 * pi);
        }
        //devide by the maximum velocity offered so weightV can be the actual max difference
        if (velForces.size() > 0){
            vel += sum(velForces) / velForces.size();

            if (vel > maxSpeed) vel = maxSpeed;
            if (vel < minSpeed) vel = minSpeed; //speed caps
        }
    }

    /**
     * sums the arraylist
     */
    private double sum(ArrayList<Double> nums){
        double sum = 0;
        for (Double num : nums) sum += num;
        return sum;
    }

    /**
     * moves the boid according to its current vel and angle
     */
    public void move(){
        super.setX((super.getX() + vel * Math.cos(angle)) % 1);
        super.setY((super.getY() + vel * Math.sin(angle)) % 1);
        if (super.getX() < 0) super.setX(super.getX() + 1);
        if (super.getY() < 0) super.setY(super.getY() + 1); //wrapping 
    }

    /**
     * draws the boid
     */
    public void draw(){
        double[] xCords = {super.getX() + .05 * scale * Math.cos(angle), super.getX() + .02 * scale * Math.cos(angle + .75 * pi), super.getX() + .02 * scale * Math.cos(angle - .75 * pi)};
        double[] yCords = {super.getY() + .05 * scale * Math.sin(angle), super.getY() + .02 * scale * Math.sin(angle + .75 * pi), super.getY() + .02 * scale * Math.sin(angle - .75 * pi)};
        StdDraw.setPenColor(74, 116, 224); //light blue
        StdDraw.filledPolygon(xCords, yCords);
        StdDraw.setPenColor(11, 58, 179); //dark blue
        StdDraw.polygon(xCords, yCords);
    }

    /**
     * draws the vision circle of the boid
     */
    public void drawVision(){
        double[] yCords = {super.getY() + .05 * scale * Math.sin(angle), super.getY() + .02 * scale * Math.sin(angle + .75 * pi), super.getY() + .02 * scale * Math.sin(angle - .75 * pi)};
        StdDraw.setPenColor(225, 0, 0); //red
        StdDraw.filledCircle(super.getX(), super.getY(), vision);
    }

    public double getAngle(){
        return angle;
    }

    public double getVel(){
        return vel;
    }
}
