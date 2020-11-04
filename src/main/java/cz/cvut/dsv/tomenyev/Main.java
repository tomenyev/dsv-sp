package cz.cvut.dsv.tomenyev;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Node;
import cz.cvut.dsv.tomenyev.utils.*;

import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        if(args.length < 2 || args.length > 3) {
            Log.getInstance().print(Log.To.CONSOLE, Constant.ARGS_HELP);
            exit();
        }

        Address origin = null, remote = null;
        Scanner input = null;

        try {
            input = new Scanner(System.in);
            Log.setInstance(args[args.length == 2 ? 1 : 2]);
            origin = new Address(args[0]);
            if(args.length != 2)
                remote = new Address(args[1]);
        } catch (Exception e) {
            Log.getInstance().print(Log.To.CONSOLE, Constant.ARGS_HELP);
            exit();
        }

        try {
            System.setProperty("java.rmi.server.hostname", Objects.requireNonNull(origin).getIp());

            Node node = new Node(origin);

            if(node.initNetwork().isOk()) {
                if (remote != null)
                    node.joinNetwork(remote);

                while(!Thread.interrupted() && node.isOk()) {
                    switch (Command.convert(next(input))) {
                        case PRINT_STATUS:
                            Log.getInstance().print(Log.To.CONSOLE, node.toString());
                            break;
                        case PRINT_LOG:
                            Log.getInstance().printLog();
                            break;
                        case INIT_ELECTION:
                            node.initElection();
                            break;
                        case JOIN_NETWORK:
                            System.out.print(Constant.JOIN_MESSAGE_CURSOR);
                            try {
                                remote = new Address(input.nextLine());
                            } catch (Exception ignored) {
                                Log.getInstance().print(Log.To.CONSOLE, Constant.BAD_ADDRESS_FORMAT);
                                break;
                            }
                            node.joinNetwork(remote);
                            break;
                        case SEND_MESSAGE:
                            System.out.print(Constant.ENTER_MESSAGE_CURSOR);
                            node.sendMessage(input.nextLine());
                            break;
                        case QUIT:
                            node.quitNetwork();
                            break;
                        case FORCE_QUIT:
                            node.forceQuitNetwork();
                            Log.getInstance().print(Log.To.CONSOLE, Constant.QUIT_MESSAGE);
                            break;
                        case HELP:
                            Log.getInstance().print(Log.To.CONSOLE, Constant.CHAT_HELP);
                            break;
                        case FIX:
                            System.out.print(Constant.FIX_MESSAGE_CURSOR);
                            Address fix;
                            try {
                                fix = new Address(input.nextLine());
                            } catch (Exception ignored) {
                                break;
                            }
                            node.fixNetwork(fix);
                            break;
                        default:
                            Log.getInstance().print(Log.To.CONSOLE,Constant.COMMAND_NOT_FOUND);
                            break;
                    }
                }
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }

        exit();
    }

    private static void exit() {
        Log.getInstance().close();
        System.exit(0);
    }

    private static String next(Scanner input) {
        if (input.hasNext())
            return input.nextLine();
        exit();
        return "";
    }


}
