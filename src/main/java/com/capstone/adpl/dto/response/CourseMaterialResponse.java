package com.capstone.adpl.dto.response;

import com.capstone.adpl.model.CourseMaterial;

import java.time.LocalDateTime;

public class CourseMaterialResponse {

    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String materialType;
    private Integer orderIndex;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime createdAt;

    public static CourseMaterialResponse fromEntity(CourseMaterial material) {
        CourseMaterialResponse response = new CourseMaterialResponse();
        response.setId(material.getId());
        response.setTitle(material.getTitle());
        response.setDescription(material.getDescription());
        response.setFileUrl(material.getFileUrl());
        response.setMaterialType(material.getMaterialType() != null ? material.getMaterialType().name() : null);
        response.setOrderIndex(material.getOrderIndex());
        response.setCourseId(material.getCourse().getId());
        response.setCourseTitle(material.getCourse().getTitle());
        response.setCreatedAt(material.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
