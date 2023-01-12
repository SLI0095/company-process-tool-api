package com.semestral_project.company_process_tool.utils;

public class BPMNSnapshotUtil {

    String content;

    public BPMNSnapshotUtil(String content) {
        this.content = content;
    }

    public void changeTo(String newContent) {
        content = newContent;
    }

    public String toString() {
        return content;
    }
}
