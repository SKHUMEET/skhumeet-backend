package skhumeet.backend.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import skhumeet.backend.domain.study.Image;

import java.util.List;

public class ImageDTO {
    @Schema(name = "ImageDTO.Response (이미지 API 응답 DTO)")
    @Getter
    @Setter
    public static class Response {
        @Schema(description = "Image ID (이미지 ID)")
        private Long id;
        @Schema(description = "Original image name (원본 이미지 이름)")
        private String originalImageName;
        @Schema(description = "Stored image name (저장용 이미지 이름)")
        private String storedImageName;
        @Schema(description = "Image file path (이미지 파일 경로)")
        private String imagePath;
        @Schema(description = "Image file size (이미지 파일 크기)")
        private Long imageSize;

        public Response(Image image) {
            this.id = image.getId();
            this.originalImageName = image.getOriginalImageName();
            this.storedImageName = image.getStoredImageName();
            this.imagePath = image.getImagePath();
            this.imageSize = image.getImageSize();
        }
    }

    @Schema(name = "ImageDTO.Result (이미지 저장 결과 DTO)")
    @Getter
    @Setter
    public static class Result {
        @Schema(description = "Image file path (이미지 파일 경로)")
        private List<String> imagePath;

        public Result(List<String> paths) {
            this.imagePath = paths;
        }
    }

    @Schema(name = "Image.Delete (이미지 삭제 DTO)")
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Delete {
        @Schema(description = "Image file path (이미지 파일 경로)")
        private String imagePath;
    }
}
