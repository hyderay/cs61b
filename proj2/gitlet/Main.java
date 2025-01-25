package gitlet;

import static gitlet.MyUtils.exit;

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
                /** make sure it's in the folder which is initialized. **/
                // TODO
                checkValidity(args, 2);
                Repository.add(args[2]);
                break;
            // TODO: FILL THE REST IN
        }
    }

    private static void checkValidity(String[] args, int n) {
        if (args.length != n) {
            exit("Incorrect operands.");
        }
    }
}
