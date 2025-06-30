package net.potatocloud.core.command;

import java.util.List;

public interface Command {

    void execute(String[] args);

    String getName();

    String getDescription();

    List<String> getAliases();

    List<String> complete(String[] args);

}
