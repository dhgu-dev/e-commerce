package com.loopers.interfaces.api;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.enums.Gender;
import com.loopers.infrastructure.member.MemberJpaRepository;
import com.loopers.interfaces.api.points.PointsV1Controller;
import com.loopers.interfaces.api.points.PointsV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PointsV1ApiE2ETest {

    private static final String ENDPOINT_POINTS_CHARGE = "/api/v1/points/charge";

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final MemberJpaRepository memberJpaRepository;

    @MockitoSpyBean
    private PointsV1Controller pointsV1Controller;

    @Autowired
    public PointsV1ApiE2ETest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp, MemberJpaRepository memberJpaRepository) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.memberJpaRepository = memberJpaRepository;
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(pointsV1Controller);
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/points/charge")
    @Nested
    class PointsCharge {
        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnsCurrentTotalPoints_whenExistingUserCharges1000Points() {
            String userId = "tester";
            Long chargeAmount = 1000L;
            memberJpaRepository.save(new MemberModel(userId, Gender.FEMALE, "2000-01-01", "test@test.com"));

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            PointsV1Dto.PointsChargeRequest request = new PointsV1Dto.PointsChargeRequest(chargeAmount);
            ParameterizedTypeReference<ApiResponse<PointsV1Dto.PointsResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointsV1Dto.PointsResponse>> response = testRestTemplate.exchange(ENDPOINT_POINTS_CHARGE, HttpMethod.POST, new HttpEntity<>(request, headers), responseType);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS);
            assertThat(response.getBody().data().points()).isEqualTo(chargeAmount);
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returnsCreatedMember_whenSuccessfulSignUp() {
            String notExistUserId = "test";
            Long chargeAmount = 1000L;

            Mockito.doThrow(new CoreException(ErrorType.NOT_FOUND)).when(pointsV1Controller).charge(eq(notExistUserId), any(PointsV1Dto.PointsChargeRequest.class));

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", notExistUserId);

            PointsV1Dto.PointsChargeRequest request = new PointsV1Dto.PointsChargeRequest(chargeAmount);
            ParameterizedTypeReference<ApiResponse<PointsV1Dto.PointsResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointsV1Dto.PointsResponse>> response = testRestTemplate.exchange(ENDPOINT_POINTS_CHARGE, HttpMethod.POST, new HttpEntity<>(request, headers), responseType);

            assertTrue(response.getStatusCode().is4xxClientError());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.FAIL);
        }
    }
}
