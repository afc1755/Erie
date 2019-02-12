//Monitor class

import java.util.ArrayList;

enum BridgeDirection{
    SOUTH,
    NORTH
}

public class Bridge {
    private ArrayList<Car> carList;
    private ArrayList<Car> southWaiting;
    private ArrayList<Car> northWaiting;
    private ArrayList<Car> nCrossing;
    private ArrayList<Car> sCrossing;
    private ArrayList<Car> realOrder;
    private BridgeDirection bd;

    public Bridge(int numCars){
        carList = new ArrayList<>();
        southWaiting = new ArrayList<>();
        northWaiting = new ArrayList<>();
        nCrossing = new ArrayList<>();
        sCrossing = new ArrayList<>();
        realOrder = new ArrayList<>();
        for(int i  = 0; i < numCars; i++){
            Car currCar = new Car(i + 1, this);
            carList.add(currCar);
            System.out.println("Creating Car Driver " + (i+1) + ".");
        }
        System.out.printf("%-29s %-15s %s%n", "South", "Bridge ==>", "North");
    }

    public void setBridgeDirection(BridgeDirection bridgeDirection){
        bd = bridgeDirection;
    }

    private void printState(){
        String southString = "[";
        String northString = "[";
        String crossString;
        for(Car c: northWaiting)
            northString += c.getCarId() + ",";
        for(Car c: southWaiting)
            southString += c.getCarId() + ",";
        if(northString.length() > 1)
            northString = northString.substring(0, northString.length() - 1);
        if(southString.length() > 1)
            southString = southString.substring(0, southString.length() - 1);
        northString += "]";
        southString += "]";
        if(bd == BridgeDirection.NORTH){
            crossString = "<== ";
            for(Car c: nCrossing){
                crossString += c.getCarId() + " ";
            }
        }else{
            crossString = "==> ";
            for(Car c: sCrossing){
                crossString += c.getCarId() + " ";
            }
        }
        System.out.printf("S:%-29s %-15s %s :N%n", southString, crossString, northString);
    }

    public synchronized void crossToNorth(int id){
        printState();
        nCrossing.remove(carList.get(id -1));
        if (!realOrder.isEmpty())
            realOrder.get(0).notify();
    }


    public synchronized void crossToSouth(int id){
        printState();
        sCrossing.remove(carList.get(id -1));
        if (!realOrder.isEmpty())
            realOrder.get(0).notify();
    }

    public synchronized void reachedTheNorth(int id){
        printState();
        Car currCar = carList.get(id -1);
        if(!northWaiting.contains(currCar))
            northWaiting.add(currCar);
            realOrder.add(currCar);
        if((sCrossing.isEmpty() && (nCrossing.isEmpty()) || (nCrossing.size() < 3 && southWaiting.isEmpty() && sCrossing.isEmpty()))){
            northWaiting.remove(currCar);
            realOrder.remove(currCar);
            nCrossing.add(currCar);
        }else{
            try{
                synchronized (currCar){
                    currCar.wait();
                }
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public synchronized void reachedTheSouth(int id){
        printState();
        Car currCar = carList.get(id - 1);
        if(!southWaiting.contains(currCar))
            southWaiting.add(currCar);
            realOrder.add(currCar);
        if ((nCrossing.isEmpty() && (sCrossing.isEmpty()) || (sCrossing.size() < 3 && northWaiting.isEmpty() && nCrossing.isEmpty()))){
            southWaiting.remove(currCar);
            realOrder.remove(currCar);
            sCrossing.add(currCar);
        }else{
            try{
                synchronized (currCar) {
                    currCar.wait();
                }
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    //main function that will run all the simulation parts, monitor the cars, etc.
    public void startRun(){
        for(Car c: carList){
            c.start();
        }
        for(Car c: carList){
            try{
                c.join();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        System.out.println("simulation finished.");
    }
}


