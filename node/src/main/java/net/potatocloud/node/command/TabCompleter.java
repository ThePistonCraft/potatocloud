package net.potatocloud.node.command;

import java.util.List;

public interface TabCompleter {

    List<String> complete(String[] args);

}
