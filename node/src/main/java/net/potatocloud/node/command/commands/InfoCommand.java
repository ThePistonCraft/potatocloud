package net.potatocloud.node.command.commands;

import lombok.RequiredArgsConstructor;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.Command;
import net.potatocloud.node.console.Logger;
import net.potatocloud.node.utils.DurationUtil;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class InfoCommand implements Command {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final Logger logger;

    @Override
    public void execute(String[] args) {
        final SystemInfo info = new SystemInfo();
        final GlobalMemory memory = info.getHardware().getMemory();
        final CentralProcessor processor = info.getHardware().getProcessor();

        logger.info("OS&8: &a" + System.getProperty("os.name") + " &8(&a" + System.getProperty("os.version") + "&8, &a" + System.getProperty("os.arch") + "&8)");
        logger.info("User&8: &a" + System.getProperty("user.name"));
        logger.info("Java version&8: &a" + System.getProperty("java.version") + " &8(&a" + System.getProperty("java.vendor") + "&8)");
        logger.info("Uptime&8: &a" + DurationUtil.formatDuration(Node.getInstance().getUptime()));
        logger.info("Started At&8: &a" + TIME_FORMATTER.format(Instant.ofEpochMilli(Node.getInstance().getStartedTime())));

        final double totalMemory = memory.getTotal() / (1024.0 * 1024 * 1024);
        final double availableMemory = memory.getAvailable() / (1024.0 * 1024 * 1024);
        final double usedMemory = totalMemory - availableMemory;

        logger.info("System Memory&8: &a" + String.format("%.2f", usedMemory) +
                " GB &8/ &a" + String.format("%.2f", totalMemory) + " GB");

        final String cpuName = processor.getProcessorIdentifier().getName();
        final int cores = processor.getPhysicalProcessorCount();
        final int threads = processor.getLogicalProcessorCount();

        logger.info("CPU&8: &a" + cpuName +
                " &8(&a" + cores + " cores&8, &a" + threads + " threads&8)");
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Shows system and node info";
    }

    @Override
    public List<String> getAliases() {
        return List.of("me");
    }
}
