package net.potatocloud.node.screen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Screen {

    public static final String NODE_SCREEN = "node_screen";

    private final String name;
    private final List<String> cachedLogs = new ArrayList<>();

    public List<String> getCachedLogs() {
        return Collections.unmodifiableList(cachedLogs);
    }

    public void addLog(String log) {
        cachedLogs.add(log);
    }
}
