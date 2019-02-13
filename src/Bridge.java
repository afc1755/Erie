//Monitor class

import java.util.ArrayList;

enum BridgeDirection{
    SOUTH,
    NORTH
}

public class Bridge {
    private ArrayList<Car> initialCarList;
    private ArrayList<Car> sWaiting;
    private ArrayList<Car> nWaiting;
    private ArrayList<Car> nCrossing;
    private ArrayList<Car> sCrossing;
    private ArrayList<Car> nRealOrder;
    private ArrayList<Car> sRealOrder;
    private BridgeDirection currentBridgeDirection;
    private BridgeDirection lastCarCrossedDirection;

    public Bridge(int numCars){
        initialCarList = new ArrayList<>();
        sWaiting = new ArrayList<>();
        nWaiting = new ArrayList<>();
        nCrossing = new ArrayList<>();
        sCrossing = new ArrayList<>();
        nRealOrder = new ArrayList<>();
        sRealOrder = new ArrayList<>();
        for(int i  = 0; i < numCars; i++){
            Car currCar = new Car(i + 1, this);
            initialCarList.add(currCar);
            System.out.println("Creating Car Driver " + (i+1) + ".");
        }
        System.out.printf("%-28s %-14s %s%n", "South", "Bridge ==>", "North");
    }

    public synchronized void setBridgeDirection(BridgeDirection bridgeDirection){
        currentBridgeDirection = bridgeDirection;
    }

    public void printState(){
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
        }else if(currentBridgeDirection == BridgeDirection.NORTH){
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

    public synchronized void reachedTheNorth(int id){
        Car currCar = initialCarList.get(id -1);
        if(!nWaiting.contains(currCar))
            nWaiting.add(currCar);
            nRealOrder.add(currCar);
        while(!((nRealOrder.get(0) == currCar  &&
                    (lastCarCrossedDirection == BridgeDirection.SOUTH || sWaiting.isEmpty()))
                && sCrossing.isEmpty() &&
                (nCrossing.isEmpty()) || (nCrossing.size() < 3 && sWaiting.isEmpty() && sCrossing.isEmpty()))) {
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nWaiting.remove(currCar);
        nRealOrder.remove(currCar);
        nCrossing.add(currCar);
        printState();
    }

    public synchronized void reachedTheSouth(int id){
        Car currCar = initialCarList.get(id - 1);
        if(!sWaiting.contains(currCar))
            sWaiting.add(currCar);
            sRealOrder.add(currCar);
        while(!((sRealOrder.get(0) == currCar  &&
                    (lastCarCrossedDirection == BridgeDirection.NORTH || nWaiting.isEmpty()))
                && nCrossing.isEmpty() &&
                (sCrossing.isEmpty()) || (sCrossing.size() < 3 && nWaiting.isEmpty() && nCrossing.isEmpty()))){
            try{
                synchronized (this) {
                    wait();
                }
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        sWaiting.remove(currCar);
        sRealOrder.remove(currCar);
        sCrossing.add(currCar);
        printState();
    }
    //main function that will run all the simulation parts, monitor the cars, etc.
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


