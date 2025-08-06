package net.potatocloud.node.service;

public class ServiceProcessChecker extends Thread {

    private final ServiceImpl service;

    public ServiceProcessChecker(ServiceImpl service) {
        this.service = service;
        setDaemon(true);
        setName("ServiceProcessChecker-" + service.getName());
    }


    @Override
    public void run() {
        while (!isInterrupted() && service.isOnline() && service.getServerProcess() != null && service.getServerProcess().isAlive()) {
            // if everything is fine, check again after 2 seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        if (!isInterrupted()) {
            service.getLogger().info("Service &a" + service.getName() + " &7seems to be offline...");
            service.cleanup();
        }
    }
}
