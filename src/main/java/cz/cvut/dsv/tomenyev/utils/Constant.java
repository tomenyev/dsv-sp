package cz.cvut.dsv.tomenyev.utils;

/**
 * Program constants.
 */
public class Constant {

    public final static String NAME = "tomenyev";

    public static final String ARGS_HELP =
            "\nUsage: "+NAME+" <node address> <existing network address> <log file name> \n" +
            "\t\t<node address> - (REQUIRED) current node address in format ip:port(e.g. 127.0.0.1:8080).\n" +
            "\t\t<existing network address> -(OPTIONAL) existing network address in format ip:port(e.g. 127.0.0.1:8080). Used to join existing network.\n" +
            "\t\t<log file name> - (REQUIRED) name of the log file.";

    public static final String CHAT_HELP =
            "\n\ts\tprint node status\n" +
            "\tie\tinitialize leader election\n" +
            "\tm\tbroadcast text message\n" +
            "\tjn\tjoin existing network(eg. 127.0.0.1:8080)\n" +
            "\tf\tfix network(missing node address)\n" +
            "\th\tprint commands\n" +
            "\tq\tsafety quit the network\n" +
            "\tfq\tforcibly quit the network\n" +
            "\tl\tprint complete log from the log file";


    public static final String CURSOR = "tomenyev > ";

    public static final String ENTER_MESSAGE_CURSOR = "message > ";

    public static final String JOIN_MESSAGE_CURSOR = "address > ";

    public static final String FIX_MESSAGE_CURSOR = "fix > ";

    public static final String BAD_ADDRESS_FORMAT = "Bad address format. (example: 127.0.0.1:8080)";

    public static final String QUIT_MESSAGE = "[Process completed]";

    public static final String COMMAND_NOT_FOUND = "Command not found";

    public static final boolean AUTOPILOT = true;

}
