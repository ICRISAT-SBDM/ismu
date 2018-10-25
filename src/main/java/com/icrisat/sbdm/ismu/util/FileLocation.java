package com.icrisat.sbdm.ismu.util;

public class FileLocation {
    private String fileNameInApplication;
    private String fileLocationOnDisk;

    public FileLocation(String fileNameInApplication, String fileLocationOnDisk) {
        this.fileNameInApplication = fileNameInApplication;
        this.fileLocationOnDisk = fileLocationOnDisk;
    }

    public String getFileNameInApplication() {
        return fileNameInApplication;
    }

    void setFileNameInApplication(String fileNameInApplication) {
        this.fileNameInApplication = fileNameInApplication;
    }

    public String getFileLocationOnDisk() {
        return fileLocationOnDisk;
    }

    void setFileLocationOnDisk(String fileLocationOnDisk) {
        this.fileLocationOnDisk = fileLocationOnDisk;
    }

    @Override
    public String toString() {
        return getFileNameInApplication();
    }
}