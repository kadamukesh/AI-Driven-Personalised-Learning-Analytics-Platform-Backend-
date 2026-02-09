package com.capstone.adpl.dto.request;

public class SubmissionRequest {

    private String content;
    private String fileUrl;

    public SubmissionRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
