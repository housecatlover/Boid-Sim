
/**
 * Drives the Boids
 */
public class BoidDriver
{
    public static void main(int boidNum, int goalNum, int obsticalNum){
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(.005);

        Boid[] boids = new Boid[boidNum];
        for (int i = 0; i < boidNum; i++)
            boids[i] = new Boid();
        Goal[] goals = new Goal[goalNum];
        for (int i = 0; i < goalNum; i++)
            goals[i] = new Goal();
        Obstical[] obsticals = new Obstical[obsticalNum];
        for (int i = 0; i < obsticalNum; i++)
            obsticals[i] = new Obstical();

        while (true){
            move(boids);
            //drawVision(boids);
            draw(boids);
            draw(goals);
            draw(obsticals);
            StdDraw.show();
            StdDraw.clear();
            update(boids, goals, obsticals);
            StdDraw.pause(5);
        }
    }
    
    public static void test(){
        //test doesn't require varible input
        int boidNum = 20;
        int goalNum = 0;
        int obsticalNum = 0;
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(.005);

        Boid[] boids = new Boid[boidNum];
        for (int i = 0; i < boidNum; i++)
            boids[i] = new Boid();
        Goal[] goals = new Goal[goalNum];
        for (int i = 0; i < goalNum; i++)
            goals[i] = new Goal();
        Obstical[] obsticals = new Obstical[goalNum];
        for (int i = 0; i < obsticalNum; i++)
            obsticals[i] = new Obstical();

        while (true){
            move(boids);
            drawVision(boids);
            draw(boids);
            draw(goals);
            draw(obsticals);
            update(boids, goals, obsticals);
            StdDraw.show();
            StdDraw.clear();
            StdDraw.pause(20);
        }
    }

    /**
     * runs the draw command for every boid
     */
    private static void draw(Thing[] things){
        for (Thing thing : things)
            thing.draw();
    }
    
    /**
     * runs the drawVision command for every boid
     */
    private static void drawVision(Boid[] boids){
        for (Boid boid : boids)
            boid.drawVision();
    }

    /**
     * runs the move command for every boid
     */
    private static void move(Boid[] boids){
        for (Boid bird : boids)
            bird.move();
    }

    /**
     * runs a tick of the simulation
     */
    private static void update(Boid[] boids, Goal[] goals, Obstical[] obsticals){
        double[][] boidsStats = new double[boids.length][4]; //so they are updated simultaniously & for multi-threading
        for (int i = 0; i < boids.length; i++){
            boidsStats[i][0] = boids[i].getX();
            boidsStats[i][1] = boids[i].getY();
            boidsStats[i][2] = boids[i].getAngle();
            boidsStats[i][3] = boids[i].getVel();
        }
        double[][] goalsStats, obsticalsStats;
        if (goals.length > 0){
            goalsStats = new double[goals.length][2]; //could possibly compress these 2 loops as they're both things
            for (int i = 0; i < goals.length; i++){
                goalsStats[i][0] = goals[i].getX();
                goalsStats[i][1] = goals[i].getY();
            }
        }
        else {
            goalsStats = null;
        }
        if (obsticals.length > 0){
            obsticalsStats = new double[obsticals.length][2];
            for (int i = 0; i < obsticals.length; i++){
                obsticalsStats[i][0] = obsticals[i].getX();
                obsticalsStats[i][1] = obsticals[i].getY();
            }
        }
        else {
            obsticalsStats = null;
        }

        for (int i = 0; i < boids.length; i++) //only the boids need updating
            boids[i].update(boidsStats, i, goalsStats, obsticalsStats);
    }
}
//Output
/**
 * The output for this program should be the live simulation of a flock
 * of triangles, showing alignment, cohesion, and seperation with nearby boids
 * 
 */
