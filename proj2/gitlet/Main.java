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
        switch (firstArg) {
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
            case "log":
                checkInit();
                checkValidity(args, 1);
                Repository.log();
                break;
            case "global-log":
                checkInit();
                checkValidity(args, 1);
                Repository.globalLog();
                break;
            case "find":
                checkInit();
                checkValidity(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                checkInit();
                checkValidity(args, 1);
                Repository.status();
                break;
            case "checkout":
                checkInit();
                handleCheckout(args);
                break;
            case "branch":
                checkInit();
                checkValidity(args, 2);
                Repository.createBranch(args[1]);
                break;
            case "rm-branch":
                checkInit();
                checkValidity(args, 2);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                checkInit();
                checkValidity(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                checkInit();
                checkValidity(args, 2);
                Repository.merge(args[1]);
                break;
            default:
                exit("No command with that name exists.");
        }
    }
}
