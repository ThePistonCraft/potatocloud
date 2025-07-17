package net.potatocloud.api.group;

import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.service.Service;

import java.util.List;

public interface ServiceGroup {

    String getName();

    String getPlatformName();

    Platform getPlatform();

    List<String> getServiceTemplates();

    int getMinOnlineCount();

    void setMinOnlineCount(int minOnlineCount);

    int getMaxOnlineCount();

    void setMaxOnlineCount(int maxOnlineCount);

    int getMaxPlayers();

    void setMaxPlayers(int maxPlayers);

    int getMaxMemory();

    void setMaxMemory(int maxMemory);

    boolean isFallback();

    void setFallback(boolean fallback);

    boolean isStatic();

    int getStartPriority();

    String getJavaCommand();

    void addServiceTemplate(String template);

    void removeServiceTemplate(String template);

    List<Service> getOnlineServices();

    int getOnlineServiceCount();

    void update();

}
