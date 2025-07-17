package net.potatocloud.node.service;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.ServiceState;

import java.util.List;

public class ServiceStartQueue extends Thread {

    private boolean running = true;

    public ServiceStartQueue() {
        setName("ServiceStartQueue");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                List<ServiceGroup> groups = CloudAPI.getInstance().getServiceGroupManager().getAllServiceGroups();

                for (ServiceGroup group : groups) {
                    if (!CloudAPI.getInstance().getServiceGroupManager().existsServiceGroup(group.getName())) {
                        continue;
                    }

                    long services = CloudAPI.getInstance().getServiceManager().getAllServices().stream()
                            .filter(service -> service.getServiceGroup().getName().equals(group.getName()))
                            .filter(service -> service.getState() == ServiceState.RUNNING || service.getState() == ServiceState.STARTING || service.getState() == ServiceState.STOPPING)
                            .count();

                    if (services < group.getMinOnlineCount()) {
                        CloudAPI.getInstance().getServiceManager().startService(group);
                    }
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void close() {
        running = false;
        interrupt();
    }
}

