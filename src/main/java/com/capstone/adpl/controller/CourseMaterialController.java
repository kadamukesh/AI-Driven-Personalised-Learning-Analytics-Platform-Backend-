package com.capstone.adpl.controller;

import com.capstone.adpl.dto.request.CourseMaterialRequest;
import com.capstone.adpl.dto.response.ApiResponse;
import com.capstone.adpl.dto.response.CourseMaterialResponse;
import com.capstone.adpl.service.CourseMaterialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseMaterialController {

    private final CourseMaterialService materialService;

    public CourseMaterialController(CourseMaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping("/courses/{courseId}/materials")
    public ResponseEntity<List<CourseMaterialResponse>> getMaterialsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(materialService.getMaterialsByCourse(courseId));
    }

    @GetMapping("/materials/{id}")
    public ResponseEntity<CourseMaterialResponse> getMaterial(@PathVariable Long id) {
        return ResponseEntity.ok(materialService.getMaterialById(id));
    }

    @PostMapping("/materials")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @userSecurity.isCourseOwner(#request.courseId))")
    public ResponseEntity<CourseMaterialResponse> createMaterial(@Valid @RequestBody CourseMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(materialService.createMaterial(request));
    }

    @PutMapping("/materials/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @userSecurity.isMaterialOwner(#id))")
    public ResponseEntity<CourseMaterialResponse> updateMaterial(@PathVariable Long id, @Valid @RequestBody CourseMaterialRequest request) {
        return ResponseEntity.ok(materialService.updateMaterial(id, request));
    }

    @DeleteMapping("/materials/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @userSecurity.isMaterialOwner(#id))")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.ok(ApiResponse.success("Material deleted successfully"));
    }
}
