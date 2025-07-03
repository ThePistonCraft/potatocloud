package net.potatocloud.manager;

public class ManagerMain {

    public static void main(String[] args) {
        final long startupTime = System.currentTimeMillis();
        System.setProperty("managerStartupTime", String.valueOf(startupTime));
        new Manager();
    }
}
