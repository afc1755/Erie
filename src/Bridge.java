/**
 * @author Andrew Chabot
 * @version 1.0
 * Bridge.java
 * Monitor class for COPADS project 1, also includes the BridgeDirection enum
 * February 13, 2019
 */

import java.util.ArrayList;
import java.util.Random;
/**
 * enum for directions of bridge, helps keep track of the last car crossed direction and
 * the direction the bridge is currently facing.
 * Can either be NORTH or SOUTH direction
 */
enum BridgeDirection{
    SOUTH,
    NORTH
}

/**
 * Bridge class, monitor on the Car threads and simulates traffic rules between cars
 */
public class Bridge {
    /**
     * initialCarList: contains all cars, populated when cars are created
     * nWaiting: list of cars waiting on the north side
     * sWaiting: list of cars waiting on the south side
     * nCrossing: list of cars currently crossing from the north side to south side
     * sCrossing: list of cars currently crossing from the south side to north side
     * lastCarCrossedDirection: direction that the last car crossed from: south or north
     * rand: Random that will be passed to each class and seeded with 31
     */
    private ArrayList<Car> initialCarList;
    private ArrayList<Car> nWaiting;
    private ArrayList<Car> sWaiting;
    private ArrayList<Car> nCrossing;
    private ArrayList<Car> sCrossing;
    private BridgeDirection lastCarCrossedDirection;
    private Random rand;

    /**
     * Creates a new bridge object with a given number of cars
     * @param numCars number of cars/threads that the bridge will be managing
     */
    public Bridge(int numCars){
        initialCarList = new ArrayList<>();
        sWaiting = new ArrayList<>();
        nWaiting = new ArrayList<>();
        nCrossing = new ArrayList<>();
        sCrossing = new ArrayList<>();
        rand = new Random(31);
        for(int i  = 0; i < numCars; i++){
            Car currCar = new Car(i + 1, this);
            initialCarList.add(currCar);
            System.out.println("Creating Car Driver " + (i+1) + ".");
        }
        System.out.printf("%-28s %-14s %s%n", "South", "Bridge ==>", "North");
    }

    /**
     * gets the random, useful for individual cars to have randomization
     * @return the rand field of the Bridge object
     */
    public synchronized Random getRand(){
        return rand;
    }

    /**
     * Function to print the current state of the bridge, following the program output
     * specifications
     */
    private void printState(){
        String southString = "[";
        String northString = "[";
        String crossString;
        for(Car c: nWaiting)
            northString += c.getCarId() + ", ";
        for(int i = sWaiting.size(); i > 0; i--)
            southString += sWaiting.get(i - 1).getCarId() + ", ";
        if(northString.length() > 1)
            northString = northString.substring(0, northString.length() - 2);
        if(southString.length() > 1)
            southString = southString.substring(0, southString.length() - 2);
        northString += "]";
        southString += "]";
        if(!nCrossing.isEmpty()){
            crossString = "<== ";
            for(Car c: nCrossing){
                crossString += c.getCarId() + " ";
            }
        }else if(!sCrossing.isEmpty()){
            crossString = "==> ";
            for(int i = sCrossing.size(); i > 0; i--){
                crossString += sCrossing.get(i - 1).getCarId() + " ";
            }
        }else if(lastCarCrossedDirection == BridgeDirection.NORTH){
            crossString = "<== ";
        }else{
            crossString = "==> ";
        }
        if(southString.length() > 23){
            southString = southString.substring(southString.length() - 23);
        }
        if(northString.length() > 28){
            northString = northString.substring(0,28);
        }
        System.out.printf("S: %-26s%-15s%s :N%n", southString, crossString, northString);
    }

    /**
     * Function called by a Car once it has crossed from south to north
     * Handles line jumping using a wait and notify system
     * Calls the printing function after the given car has successfully left the bridge
     * @param id id of the car that has crossed
     */
    public synchronized void crossToNorth(int id){
        while(sCrossing.get(0) != initialCarList.get(id -1)){
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sCrossing.remove(initialCarList.get(id -1));
        notifyAll();
        lastCarCrossedDirection = BridgeDirection.SOUTH;
        printState();
    }

    /**
     * Function called by a Car once it has crossed from north to south
     * Handles line jumping using a wait and notify system
     * Calls the printing function after the given car has successfully left the bridge
     * @param id id of the car that has crossed
     */
    public synchronized void crossToSouth(int id){
        while(nCrossing.get(0) != initialCarList.get(id -1)){
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nCrossing.remove(initialCarList.get(id -1));
        notifyAll();
        lastCarCrossedDirection = BridgeDirection.NORTH;
        printState();
    }

    /**
     * Function called by a car once it has reached the north side of the bridge
     * Goal of the function is to ultimately have the calling car get onto the bridge
     * Utilizes wait and notify and a while loop lock(not a busy lock) to enforce rules
     * Handles line jumping and alternating directions for crossing, as well as the 3 car
     * rule, all in the while loop's condition
     * Prints the state of the bridge after the car has successfully gone on the bridge
     * @param id id of the calling car that has reached the north side of the bridge
     */
    public synchronized void reachedTheNorth(int id){
        Car currCar = initialCarList.get(id -1);
        nWaiting.add(currCar);
        while(!((nWaiting.get(0).equals(currCar)   &&
                (lastCarCrossedDirection == BridgeDirection.SOUTH || sWaiting.isEmpty()))
                && sCrossing.isEmpty() &&
                (nCrossing.isEmpty() || (nCrossing.size() < 3 && sWaiting.isEmpty())))) {
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nWaiting.remove(currCar);
        nCrossing.add(currCar);
        printState();
        notifyAll();
    }

    /**
     * Function called by a car once it has reached the south side of the bridge
     * Goal of the function is to ultimately have the calling car get onto the bridge
     * Utilizes wait and notify and a while loop lock(not a busy lock) to enforce rules
     * Handles line jumping and alternating directions for crossing, as well as the 3 car
     * rule, all in the while loop's condition
     * Prints the state of the bridge after the car has successfully gone on the bridge
     * @param id id of the calling car that has reached the north side of the bridge
     */
    public synchronized void reachedTheSouth(int id){
        Car currCar = initialCarList.get(id - 1);
        sWaiting.add(currCar);
        while(!((sWaiting.get(0).equals(currCar)  &&
                (lastCarCrossedDirection == BridgeDirection.NORTH || nWaiting.isEmpty()))
                && nCrossing.isEmpty() &&
                (sCrossing.isEmpty() || (sCrossing.size() < 3 && nWaiting.isEmpty())))){
            try{
                synchronized (this) {
                    wait();
                }
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        sWaiting.remove(currCar);
        sCrossing.add(currCar);
        printState();
        notifyAll();
    }

    /**
     * Main function that will run all the simulation parts, monitor the cars, etc.
     * Also handles joining the cars after their execution has been completed
     */
    public void startRun(){
        for(Car c: initialCarList){
            c.start();
        }
        for(Car c: initialCarList){
            try{
                c.join();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        System.out.println("simulation finished.");
    }
}




