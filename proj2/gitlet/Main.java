package gitlet;

import static gitlet.MyUtils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Sean
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            exit("Please enter a command.");
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                checkValidity(args, 1);
                Repository.init();
                break;
            case "add":
                checkInit();
                checkValidity(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                checkInit();
                checkValidity(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                checkInit();
                checkValidity(args, 2);
                Repository.rm(args[1]);
                break;
        }
    }
}
