package com.capstone.adpl.service;

import com.capstone.adpl.dto.request.CourseMaterialRequest;
import com.capstone.adpl.dto.response.CourseMaterialResponse;
import com.capstone.adpl.exception.ResourceNotFoundException;
import com.capstone.adpl.model.Course;
import com.capstone.adpl.model.CourseMaterial;
import com.capstone.adpl.model.MaterialType;
import com.capstone.adpl.repository.CourseMaterialRepository;
import com.capstone.adpl.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseMaterialService {

    private final CourseMaterialRepository materialRepository;
    private final CourseRepository courseRepository;

    public CourseMaterialService(CourseMaterialRepository materialRepository, CourseRepository courseRepository) {
        this.materialRepository = materialRepository;
        this.courseRepository = courseRepository;
    }

    public List<CourseMaterialResponse> getMaterialsByCourse(Long courseId) {
        return materialRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(CourseMaterialResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CourseMaterialResponse getMaterialById(Long id) {
        CourseMaterial material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));
        return CourseMaterialResponse.fromEntity(material);
    }

    @Transactional
    public CourseMaterialResponse createMaterial(CourseMaterialRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        CourseMaterial material = new CourseMaterial();
        material.setTitle(request.getTitle());
        material.setDescription(request.getDescription());
        material.setFileUrl(request.getFileUrl());
        material.setCourse(course);
        material.setOrderIndex(request.getOrderIndex());

        if (request.getMaterialType() != null) {
            try {
                material.setMaterialType(MaterialType.valueOf(request.getMaterialType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                material.setMaterialType(MaterialType.PDF);
            }
        }

        CourseMaterial saved = materialRepository.save(material);
        return CourseMaterialResponse.fromEntity(saved);
    }

    @Transactional
    public CourseMaterialResponse updateMaterial(Long id, CourseMaterialRequest request) {
        CourseMaterial material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));

        if (request.getTitle() != null) {
            material.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            material.setDescription(request.getDescription());
        }
        if (request.getFileUrl() != null) {
            material.setFileUrl(request.getFileUrl());
        }
        if (request.getOrderIndex() != null) {
            material.setOrderIndex(request.getOrderIndex());
        }
        if (request.getMaterialType() != null) {
            try {
                material.setMaterialType(MaterialType.valueOf(request.getMaterialType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Keep existing type
            }
        }

        CourseMaterial saved = materialRepository.save(material);
        return CourseMaterialResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Material", "id", id);
        }
        materialRepository.deleteById(id);
    }
}
