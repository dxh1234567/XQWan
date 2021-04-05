package com.jj.logger;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import static com.jj.logger.Utils.checkNotNull;

/**
 * This is used to saves log messages to the disk.
 * By default it uses {@link CsvFormatStrategy} to translates text message into CSV format.
 */
public class DiskLogAdapter implements LogAdapter {

    @NonNull
    private final FormatStrategy formatStrategy;

    public DiskLogAdapter() {
        formatStrategy = CsvFormatStrategy.newBuilder().build();
    }

    public DiskLogAdapter(@Nullable String filePath, int maxBytes, Context context) {
        formatStrategy = CsvFormatStrategy
                .newBuilder()
                .filePath(filePath)
                .maxBytes(maxBytes)
                .context(context)
                .build();
    }

    public DiskLogAdapter(@NonNull FormatStrategy formatStrategy) {
        this.formatStrategy = checkNotNull(formatStrategy);
    }

    @Override
    public boolean isLoggable(int priority, @Nullable String tag) {
        return true;
    }

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        formatStrategy.log(priority, tag, message);
    }

    public File getLogFile() {
        if (formatStrategy instanceof CsvFormatStrategy) {
            return ((CsvFormatStrategy) formatStrategy).getLogFile();
        }
        return null;
    }
}
