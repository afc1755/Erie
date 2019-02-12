//Runnable class that represents a Car on the bridge

import java.util.Random;
public class Car extends Thread{
    private int id;
    private Bridge myBridge;

    public Car(int carNum, Bridge myBridge){
        this.id = carNum;
        this.myBridge = myBridge;
    }

    public int getCarId(){
        return id;
    }

    public void startCar(){
        Random rand = new Random(31);
        try{
            Thread.sleep(rand.nextInt(500));
        }catch(InterruptedException e){
            System.out.println("oof");
        }
        if(id % 2 == 1){
            myBridge.reachedTheSouth(id);
        }else{
           myBridge.reachedTheNorth(id);
        }
        crossBridge();
    }

    public void crossBridge(){
        Random rand = new Random(31);
        if(id % 2 == 1) {
            myBridge.setBridgeDirection(BridgeDirection.SOUTH);
            try {
                this.sleep(rand.nextInt(1000) + 500);
            } catch (InterruptedException e) {
                System.out.println("oof");
            }
            myBridge.crossToSouth(id);
        }else{
            myBridge.setBridgeDirection(BridgeDirection.NORTH);
            try {
                this.sleep(rand.nextInt(1000) + 500);
            } catch (InterruptedException e) {
                System.out.println("oof");
            }
            myBridge.crossToNorth(id);
        }
    }

     @Override
     public void run(){
        startCar();
     }
}
