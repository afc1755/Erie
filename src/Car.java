/**
 * @author Andrew Chabot
 * @version 1.0
 * Car.java
 * Runnable class that represents a Car on the bridge for COPADS Project 1
 * February 13, 2019
 */

import java.util.Random;

/**
 * Car class
 */
public class Car extends Thread{
    /**
     * id: unique integer representation for a given car
     * myBridge: the bridge monitor object that holds the given car
     */
    private int id;
    private Bridge myBridge;

    /**
     * Creates a new car with the given id and bridge
     * @param carNum unique id for the car
     * @param myBridge brideg that will monitor this car
     */
    public Car(int carNum, Bridge myBridge){
        this.id = carNum;
        this.myBridge = myBridge;
    }

    /**
     * returns the car's integer representation
     * @return id: integer representation of the current car
     */
    public int getCarId(){
        return id;
    }

    /**
     * Function called when the thread/car has been run, simulates cars driving up to a
     * certain side of the bridge.
     * After reaching the side, the car will attempt to cross by calling the Bridge's
     * reachedThe____ method, followed by calling Car's own crossBridge method
     */
    private void startCar(Random rand){
        try{
            sleep(rand.nextInt(500));
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        if(id % 2 == 1){
            myBridge.reachedTheSouth(id);
        }else{
            myBridge.reachedTheNorth(id);
        }
        crossBridge(rand);
    }

    /**
     * Function to simulate a car crossing the bridge, called on a car when it has been
     * determined to be able to cross the bridge.
     * Uses sleep to simulate time taken to cross the bridge.
     * After sleeping, calls Bridge's crossTo_____ method to let other Cars know that the
     * bridge might be free for crossing again.
     */
    private void crossBridge(Random rand){
        if(id % 2 == 1) {
            try {
                sleep(rand.nextInt(1000) + 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myBridge.crossToNorth(id);
        }else{
            try {
                sleep(rand.nextInt(1000) + 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myBridge.crossToSouth(id);
        }
    }

    /**
     * Function called when a car/thread is started, simply calls the startCar method
     * and passes in the random initialized in the Bridge class
     */
    @Override
    public void run(){
        startCar(myBridge.getRand());
    }
}

