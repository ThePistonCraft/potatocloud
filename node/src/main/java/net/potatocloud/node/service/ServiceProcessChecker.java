package net.potatocloud.node.service;

import lombok.SneakyThrows;

public class ServiceProcessChecker extends Thread {

    private final ServiceImpl service;

    public ServiceProcessChecker(ServiceImpl service) {
        this.service = service;
        setDaemon(true);
    }

    @SneakyThrows
    @Override
    public void run() {
        while (service.isOnline() && service.getServerProcess() != null && service.getServerProcess().isAlive()) {
            // if everything is fine, check again after 2 seconds
            Thread.sleep(2000);
        }
        service.getLogger().info("The service &a" + service.getName() + " &7seems to be offline...");
        service.cleanup();
    }
}
