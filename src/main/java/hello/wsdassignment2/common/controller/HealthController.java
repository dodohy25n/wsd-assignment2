package hello.wsdassignment2.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Health Check", description = "서버 상태 확인 API")
@RestController
@RequiredArgsConstructor
public class HealthController {

    // build.gradle에 buildInfo() 설정이 있어야 주입됨 (없으면 null일 수 있음)
    private final BuildProperties buildProperties;

    @Operation(summary = "서버 헬스 체크", description = "서버의 현재 상태와 버전 정보를 반환합니다.")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new LinkedHashMap<>();

        health.put("status", "OK"); // 200 OK 상태
        health.put("timestamp", LocalDateTime.now()); // 현재 시간

        if (buildProperties != null) {
            health.put("version", buildProperties.getVersion()); // 버전 (ex: 1.0.0)
            health.put("buildTime", buildProperties.getTime());  // 빌드 시간
            health.put("artifact", buildProperties.getArtifact()); // 아티팩트명
        } else {
            health.put("version", "unknown");
            health.put("buildTime", "unknown");
        }

        return ResponseEntity.ok(health);
    }
}