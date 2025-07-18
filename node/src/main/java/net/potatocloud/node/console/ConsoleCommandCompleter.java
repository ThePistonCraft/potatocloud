package net.potatocloud.node.console;

import lombok.RequiredArgsConstructor;
import net.potatocloud.node.command.Command;
import net.potatocloud.node.command.CommandManager;
import net.potatocloud.node.command.TabCompleter;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

@RequiredArgsConstructor
public class ConsoleCommandCompleter implements Completer {

    private final CommandManager commandManager;

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        final List<String> words = line.words();
        final String currentWord = line.word();

        if (line.wordIndex() == 0) {
            for (String cmd : commandManager.getAllCommandNames()) {
                if (cmd.startsWith(currentWord)) {
                    candidates.add(new Candidate(cmd));
                }
            }
        } else {
            final String commandName = words.get(0);
            final Command command = commandManager.getCommand(commandName);
            if (command == null) {
                return;
            }

            if (command instanceof TabCompleter completer) {

                final String[] args = words.subList(1, words.size()).toArray(new String[0]);

                for (String suggestion : completer.complete(args)) {
                    if (suggestion.startsWith(currentWord)) {
                        candidates.add(new Candidate(suggestion));
                    }
                }
            }
        }
    }
}
