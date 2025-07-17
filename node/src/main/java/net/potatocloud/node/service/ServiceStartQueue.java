package net.potatocloud.node.service;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.ServiceState;

import java.util.ArrayList;
import java.util.Comparator;
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
                List<ServiceGroup> groups = new ArrayList<>(CloudAPI.getInstance().getServiceGroupManager().getAllServiceGroups());
                groups.sort(Comparator.comparingInt(ServiceGroup::getStartPriority).reversed());

                for (ServiceGroup group : groups) {
                    if (!CloudAPI.getInstance().getServiceGroupManager().existsServiceGroup(group.getName())) {
                        continue;
                    }

                    long activeServices = CloudAPI.getInstance().getServiceManager().getAllServices().stream()
                            .filter(s -> s.getServiceGroup().getName().equals(group.getName()))
                            .filter(s -> s.getState() == ServiceState.RUNNING || s.getState() == ServiceState.STARTING || s.getState() == ServiceState.STOPPING)
                            .count();

                    int onlinePlayersInGroup = CloudAPI.getInstance().getPlayerManager().getOnlinePlayersByGroup(group).size();
                    int totalOnlinePlayers = CloudAPI.getInstance().getPlayerManager().getOnlinePlayers().size();

                    if (totalOnlinePlayers == 0) {
                        continue;
                    }

                    double onlinePercentage = (onlinePlayersInGroup / (double) totalOnlinePlayers) * 100;

                    if (activeServices < group.getMinOnlineCount() || onlinePercentage >= group.getStartPercentage()) {
                        CloudAPI.getInstance().getServiceManager().startService(group);
                    }
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                running = false;
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        running = false;
        interrupt();
    }
}
