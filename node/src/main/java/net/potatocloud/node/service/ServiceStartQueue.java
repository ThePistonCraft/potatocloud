package net.potatocloud.node.service;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;

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
                final List<ServiceGroup> serviceGroups = CloudAPI.getInstance().getServiceGroupManager().getAllServiceGroups();
                if (serviceGroups != null) {
                    for (ServiceGroup serviceGroup : serviceGroups) {
                        if (!CloudAPI.getInstance().getServiceGroupManager().existsServiceGroup(serviceGroup.getName())) {
                            continue;
                        }
                        if (serviceGroup.getOnlineServiceCount() < serviceGroup.getMinOnlineCount()) {
                            CloudAPI.getInstance().getServiceManager().startService(serviceGroup);
                        }
                    }
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void close() {
        running = false;
        interrupt();
    }
}
