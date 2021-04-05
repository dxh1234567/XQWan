package com.jj.logger;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jj.base.exts.CommonExtKt;

import static com.jj.logger.Utils.checkNotNull;

/**
 * Abstract class that takes care of background threading the file log operation on Android.
 * implementing classes are free to directly perform I/O operations there.
 * <p>
 * Writes all logs to the disk with CSV format.
 */
public class DiskLogStrategy implements LogStrategy {

    @NonNull
    private final Handler handler;

    public DiskLogStrategy(@NonNull Handler handler) {
        this.handler = checkNotNull(handler);
    }

    @Override
    public void log(int level, @Nullable String tag, @NonNull String message) {
        checkNotNull(message);

        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(level, message));
    }

    public File getLogFile() {
        if (handler instanceof WriteHandler) {
            return ((WriteHandler) handler).getLogFile();
        }
        return null;
    }

    static class WriteHandler extends Handler {

        @NonNull
        private final String folder;
        private final int maxFileSize;
        private Context context;

        WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize, Context context) {
            super(checkNotNull(looper));
            this.folder = checkNotNull(folder);
            this.maxFileSize = maxFileSize;
            this.context = context;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override
        public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;

            FileWriter fileWriter = null;
            File logFile = getLogFile(folder, genFileName(today()));
            try {
                fileWriter = new FileWriter(logFile, true);

                writeLog(fileWriter, content);

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }

        /**
         * 获取最新日志文件
         *
         * @return
         */
        public File getLogFile() {
            return getLogFile(folder, genFileName(context.getSharedPreferences("logger_pref", Context.MODE_PRIVATE).getString("last_log_date", "")));
        }

        private String genFileName(String date) {
            return new StringBuilder(CommonExtKt.getProductId()).append("_").append(date).toString();
        }

        private String today() {
            String date = new SimpleDateFormat("M-dd").format(new Date());
            if (null != context) {
                context.getSharedPreferences("logger_pref", Context.MODE_PRIVATE).edit().putString("last_log_date", date).apply();
            }
            return date;
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
            checkNotNull(fileWriter);
            checkNotNull(content);

            fileWriter.append(content);
        }

        private File getLogFile(@NonNull String folderName, @NonNull String fileName) {
            checkNotNull(folderName);
            checkNotNull(fileName);

            File folder = new File(folderName);
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs();
            }

            int newFileCount = 0;
            File newFile;
            File existingFile = null;

            newFile = new File(folder, String.format("%s_%s.log", fileName, newFileCount));
            while (newFile.exists()) {
                existingFile = newFile;
                newFileCount++;
                newFile = new File(folder, String.format("%s_%s.log", fileName, newFileCount));
            }

            if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    return newFile;
                }
                return existingFile;
            }

            return newFile;
        }
    }
}
