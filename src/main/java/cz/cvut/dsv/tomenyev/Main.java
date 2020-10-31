package cz.cvut.dsv.tomenyev;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Node;
import cz.cvut.dsv.tomenyev.utils.Command;
import cz.cvut.dsv.tomenyev.utils.Constant;
import cz.cvut.dsv.tomenyev.utils.Log;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        if(args.length < 2 || args.length > 3) {
            System.out.println(Constant.ARGS_HELP);
            exit();
        }

        Address origin = null, remote = null;
        Scanner input = null;
        Log log = null;

        try {
            input = new Scanner(System.in);
            log = Log.getInstance(args[args.length == 2 ? 1 : 2]);
            origin = new Address(args[0]);
            if(args.length != 2)
                remote = new Address(args[1]);
        } catch (Exception e) {
            System.out.println(Constant.ARGS_HELP);
            exit();
        }

        try {
            System.setProperty("java.rmi.server.hostname",origin.getIp());

            Node node = new Node(origin, log);

            if(node.initNetwork().isOk()) {
                if (remote != null) {
                    node.joinNetwork(remote);
                }

                while(!Thread.interrupted() && node.isOk()) {

                    System.out.print(Constant.CONSOLE_CURSOR);

                    switch (Command.convert(input.nextLine())) {
                        case PRINT_STATUS:
                            System.out.println(node.toString());
                            break;
                        case PRINT_LOG:
                            node.printLog();
                            break;
                        case INIT_ELECTION:
                            node.initElection();
                            break;
                        case SEND_MESSAGE:
                            System.out.print(Constant.CONSOLE_CURSOR + Constant.ENTER_MESSAGE_CURSOR);
                            node.sendMessage(input.nextLine());
                            break;
                        case QUIT:
                            node.quitNetwork();
                            System.out.println(Constant.QUIT_MESSAGE);
                            break;
                        case FORCE_QUIT:
                            node.forceQuitNetwork();
                            System.out.println(Constant.QUIT_MESSAGE);
                            break;
                        case HELP:
                            System.out.println(Constant.CHAT_HELP);
                            break;
                        default:
                            System.out.println(Constant.COMMAND_NOT_FOUND);
                            break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        exit();
    }

    private static void exit() {
        Log.getInstance().close();
        System.exit(0);
    }
}
