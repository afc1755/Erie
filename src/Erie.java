//Main Class for the Erie program, COPADS project 1

public class Erie {
    static final String usageMess = "Usage: java Erie number-of-cars";
    public static void main(String[] args) {
        if(args.length != 1 || !(args[0].matches("-?\\d+"))){
            throw new Error(usageMess);
        }else{
            Bridge currBridge = new Bridge(Integer.parseInt(args[0]));
            currBridge.startRun();
        }
    }
}


