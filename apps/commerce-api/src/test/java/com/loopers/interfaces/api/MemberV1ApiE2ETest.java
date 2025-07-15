package com.loopers.interfaces.api;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.enums.Gender;
import com.loopers.infrastructure.member.MemberJpaRepository;
import com.loopers.interfaces.api.member.MemberV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberV1ApiE2ETest {

    private static final String ENDPOINT_SIGNUP = "/api/v1/users";
    private static final String ENDPOINT_GET_MY_INFO = "/api/v1/users/me";

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final MemberJpaRepository memberJpaRepository;

    @Autowired
    public MemberV1ApiE2ETest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp, MemberJpaRepository memberJpaRepository) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.memberJpaRepository = memberJpaRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/users")
    @Nested
    class SignUp {
        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsCreatedMember_whenSuccessfulSignUp() {
            String userId = "test";
            Gender gender = Gender.MALE;
            String birthdate = "2000-01-01";
            String email = "test@test.com";

            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<MemberV1Dto.MemberResponse>> response = testRestTemplate.exchange(ENDPOINT_SIGNUP, HttpMethod.POST, new HttpEntity<>(new MemberV1Dto.SignupRequest(
                    userId, gender, birthdate, email
            )), responseType);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS);
            assertThat(response.getBody().data().userId()).isEqualTo(userId);
            assertThat(response.getBody().data().gender()).isEqualTo(gender);
            assertThat(response.getBody().data().birthdate()).isEqualTo(birthdate);
            assertThat(response.getBody().data().email()).isEqualTo(email);
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void returnsBadRequest_whenNotExistGender() {
            String userId = "test";
            String birthdate = "2000-01-01";
            String email = "test@test.com";

            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<MemberV1Dto.MemberResponse>> response = testRestTemplate.exchange(ENDPOINT_SIGNUP, HttpMethod.POST, new HttpEntity<>(new MemberV1Dto.SignupRequest(
                    userId, null, birthdate, email
            )), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.FAIL)
            );
        }
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class GetMyInfo {
        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsMember_whenSuccessfulGetMyInfo() {
            String userId = "test";
            MemberModel memberModel = memberJpaRepository.save(new MemberModel(userId, Gender.MALE, "2000-01-01", "test@test.com"));
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<MemberV1Dto.MemberResponse>> response = testRestTemplate.exchange(ENDPOINT_GET_MY_INFO, HttpMethod.GET, new HttpEntity<Void>(headers), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(memberModel.getUserId()),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(memberModel.getGender()),
                    () -> assertThat(response.getBody().data().birthdate()).isEqualTo(memberModel.getBirthdate()),
                    () -> assertThat(response.getBody().data().email()).isEqualTo(memberModel.getEmail())
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returnsNotFound_whenNotExistUserId() {
            String notExistUserId = "notExistUser";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", notExistUserId);

            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<MemberV1Dto.MemberResponse>> response = testRestTemplate.exchange(ENDPOINT_GET_MY_INFO, HttpMethod.GET, new HttpEntity<Void>(headers), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.FAIL)
            );
        }
    }
}
