package net.potatocloud.node.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Logger;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ServiceManagerImpl implements ServiceManager {

    private final List<Service> onlineServices = new ArrayList<>();

    private final NodeConfig config;
    private final Logger logger;

    @Override
    public Service getService(String serviceName) {
        return onlineServices.stream()
                .filter(service -> service.getName().equalsIgnoreCase(serviceName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Service> getAllOnlineServices() {
        return onlineServices;
    }

    @Override
    public void startService(ServiceGroup serviceGroup) {
        final int serviceId = getFreeServiceId(serviceGroup);
        final int port = getServicePort(serviceGroup);
        final ServiceImpl service = new ServiceImpl(serviceId, port, serviceGroup, config, logger);

        onlineServices.add(service);
        service.start();
    }

    @Override
    public void startServices(ServiceGroup serviceGroup, int count) {
        for (int i = 0; i < count; i++) {
            startService(serviceGroup);
        }
    }

    public void removeService(Service service) {
        onlineServices.remove(service);
    }

    private int getFreeServiceId(ServiceGroup serviceGroup) {
        List<Integer> usedIds = new ArrayList<>();

        for (Service service : onlineServices) {
            if (service.getServiceGroup().equals(serviceGroup)) {
                usedIds.add(service.getServiceId());
            }
        }

        int id = 1;
        while (usedIds.contains(id)) {
            id++;
        }

        return id;
    }

    private int getServicePort(ServiceGroup serviceGroup) {
        List<Integer> usedPorts = new ArrayList<>();
        for (Service service : onlineServices) {
            usedPorts.add(service.getPort());
        }

        int port = serviceGroup.getPlatform().isProxy() ? config.getProxyStartPort() : config.getServiceStartPort();

        while (usedPorts.contains(port)) {
            port++;
        }

        return port;
    }
}
