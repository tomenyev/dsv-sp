package cz.cvut.dsv.tomenyev;

import cz.cvut.dsv.tomenyev.network.Address;
import cz.cvut.dsv.tomenyev.network.Node;
import cz.cvut.dsv.tomenyev.utils.Log;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        if(args.length < 2 || args.length > 3) {
            //TODO
            return;
        }

        Log logger;
        Address address, remote;
        Node node;
        address = new Address(args[0]);

        try {
            logger = new Log(args[args.length == 2 ? 1 : 2]);

            System.setProperty("java.rmi.server.hostname",address.getIp());

            node = new Node(address, logger);

            if(node.createNetwork().isRunning()) {
                if(args.length != 2) {
                    remote = new Address(args[1]);
                }

                Scanner input = new Scanner(System.in);

                while(!Thread.interrupted() && node.isRunning()) {
                    String cmd = input.nextLine();
                    System.out.println(cmd);
                }

            }

        } catch (Exception ignored) {
            //TODO
        }

    }
}
