package me.nabdev.oxconfig;

import edu.wpi.first.wpilibj.DriverStation;

class Logger {

    public enum MessageType {
        Info, Warning, Error
    }

    static void logInfo(String message) {
        log_backend(new LogMessage(message, MessageType.Info));
    }

    static void logWarning(String message) {
        log_backend(new LogMessage(message, MessageType.Warning));
    }

    static void logError(String message) {
        log_backend(new LogMessage(message, MessageType.Error));
    }

    private static void log_backend(LogMessage message) {
        if (!shouldLog(message))
            return;
        StringBuilder builder = new StringBuilder();
        builder.append("[OxConfig ");
        builder.append(message.type.name());
        builder.append("] ");
        builder.append(message.message);
        if (message.type == MessageType.Error)
            DriverStation.reportError(builder.toString(), false);
        else if (message.type == MessageType.Warning)
            DriverStation.reportWarning(builder.toString(), false);
        else
            System.out.println(builder);

    }

    private static boolean shouldLog(LogMessage message) {
        if (OxConfig.loggingMode == OxConfig.LoggingMode.None)
            return false;
        if (OxConfig.loggingMode == OxConfig.LoggingMode.Info)
            return true;
        if (OxConfig.loggingMode == OxConfig.LoggingMode.Warnings && message.type != MessageType.Info)
            return true;
        return OxConfig.loggingMode == OxConfig.LoggingMode.Errors && message.type == MessageType.Error;
    }
}

class LogMessage {
    public final String message;
    public final Logger.MessageType type;

    public LogMessage(String message, Logger.MessageType type) {
        this.message = message;
        this.type = type;
    }
}