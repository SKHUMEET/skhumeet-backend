package skhumeet.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import skhumeet.backend.domain.dto.ImageDTO;
import skhumeet.backend.service.ImageService;

import java.io.IOException;
import java.util.List;

@Tag(name = "Image API (이미지 관련 API)", description = "API for Image CRUD (이미지 CRUD를 위한 API)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {
    private final ImageService imageService;

    // Create (이미지 저장)
    @Operation(
            summary = "Create Image API (이미지 저장 API)",
            description = """
                    Create images with Firebase Storage. Authorize needed.<br/>
                    파이어베이스 스토리지를 통해 이미지들을 저장. 로그인(토큰) 필요.
                    
                    Path and image files needed to saving images in Firebase Storage.<br/>
                    API 요청 시, 이미지를 저장할 파이어베이스 스토리지 경로 이름과 이미지 파일 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDTO.Result> save(@RequestParam("path") String path,
                                                @RequestPart("images") List<MultipartFile> images) throws IOException {
        return ResponseEntity.ok(imageService.uploadImage(path + "/", images));
    }

    // Delete (이미지 삭제)
    @Operation(
            summary = "Delete Image API (이미지 삭제 API)",
            description = """
                    Delete image with internal path and Firebase Storage path. Authorize needed.<br/?>
                    내부 경로와 이미지를 저장한 파이어베이스 스토리지 경로를 통해 이미지 삭제. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam("path") String path,
                                         @RequestBody ImageDTO.Delete request) {
        return imageService.deleteImage(path + "/", request.getImagePath());
    }
}