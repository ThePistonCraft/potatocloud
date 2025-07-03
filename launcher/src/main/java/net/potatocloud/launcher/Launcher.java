package net.potatocloud.launcher;

public class Launcher {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Use --manager or --node");
            return;
        }

        if (args[0].equalsIgnoreCase("--manager")) {
            net.potatocloud.manager.ManagerMain.main(args);
        } else if (args[0].equalsIgnoreCase("--node")) {
            net.potatocloud.node.NodeMain.main(args);
        } else {
            System.err.println("Use --manager or --node");
        }
    }
}
