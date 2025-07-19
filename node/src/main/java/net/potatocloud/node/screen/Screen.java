package net.potatocloud.node.screen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Screen {

    private final String name;
    private final List<String> cachedLogs = new ArrayList<>();

}
