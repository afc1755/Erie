/**
 * @author Andrew Chabot
 * @version 1.0
 * Erie.java
 * Main Class for the Erie program, COPADS project 1
 * February 13, 2019
 */
public class Erie {
    /**
     * usageMess: string to print if arguments are incorrect, shows proper usage
     */
    static final String usageMess = "Usage: java Erie number-of-cars";

    /**
     * Main function, checks the arguments for correctness and then creates a new Bridge
     * and starts the Bridge's execution if the given arguments are correct
     * @param args input arguments from running the program
     */
    public static void main(String[] args) {
        if(args.length != 1 || !(args[0].matches("-?\\d+") || Integer.parseInt(args[0]) < 0)){
            throw new Error(usageMess);
        }else{
            Bridge currBridge = new Bridge(Integer.parseInt(args[0]));
            currBridge.startRun();
        }
    }
}


