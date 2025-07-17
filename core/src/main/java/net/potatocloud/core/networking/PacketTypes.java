package net.potatocloud.core.networking;

public class PacketTypes {

    public static final String MESSAGE = "message";

    public static final String SERVICE_ADD = "service_add";
    public static final String SERVICE_REMOVE = "service_remove";
    public static final String UPDATE_SERVICE = "update_service";
    public static final String SERVICE_STARTED = "service_started";
    public static final String REQUEST_SERVICES = "request_services";
    public static final String START_SERVICE = "start_service";
    public static final String SHUTDOWN_SERVICE = "shutdown_service";
    public static final String SERVICE_EXECUTE_COMMAND = "service_execute_command";

    public static final String REQUEST_GROUPS = "request_groups";
    public static final String GROUP_ADD = "group_add";
    public static final String UPDATE_GROUP = "update_group";

    public static final String EVENT = "event";

    public static final String PLAYER_ADD = "player_add";
    public static final String PLAYER_REMOVE = "player_remove";
    public static final String UPDATE_PLAYER = "update_player";
    public static final String CONNECT_PLAYER = "connect_player";
}
