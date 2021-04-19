package com.pollinators.buzzshare;

import java.io.File;


public class ReceiverFileProgress {
    private String fileName;
    private File file;
    private long fileSize;
    private long bytesSent;
    private long timeTaken;

    public ReceiverFileProgress(String fileName) {
        this.fileName = fileName;
        this.bytesSent = 0;
        this.timeTaken = 0;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }
}
