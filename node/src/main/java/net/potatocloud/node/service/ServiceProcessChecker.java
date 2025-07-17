package net.potatocloud.node.service;

import lombok.SneakyThrows;

public class ServiceProcessChecker extends Thread {

    private final ServiceImpl service;

    public ServiceProcessChecker(ServiceImpl service) {
        this.service = service;
        setDaemon(true);
    }


    @Override
    public void run() {
        while (!isInterrupted() && service.isOnline() && service.getServerProcess() != null && service.getServerProcess().isAlive()) {
            // if everything is fine, check again after 2 seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
                return;
            }
        }
        if (!isInterrupted()) {
            service.getLogger().info("The service &a" + service.getName() + " &7seems to be offline...");
            service.cleanup();
        }
    }
}
