package org.example.flowday.domain.member;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.member.config.TestConfig;
import org.example.flowday.domain.member.controller.MemberController;
import org.example.flowday.domain.member.dto.MemberDTO;
import org.example.flowday.domain.member.exception.MemberTaskException;
import org.example.flowday.domain.member.service.MemberService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // application-test.yml을 사용하도록 설정
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestConfig.class)
public class MemberScenarioTests {

    @Autowired
    MemberController memberController;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private static String testUserToken;  // 토큰 저장용 변수
    private static final String loginId = "user1";
    private static final String password = "password123";
    private static String url;


    @BeforeAll
    public static void setUp(@Autowired MemberController memberController) {

        // 유저 가입을 위한 DTO 준비
        MemberDTO.CreateRequestDTO dto = new MemberDTO.CreateRequestDTO();
        dto.setLoginId(loginId);
        dto.setEmail("user1@example.com");
        dto.setPw(password);
        dto.setName("User One");
        dto.setPhoneNum("123-456-7890");
        dto.setDateOfBirth(LocalDate.now());

        // 회원가입 API 호출
        ResponseEntity<MemberDTO.CreateResponseDTO> response = memberController.createMember(dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MemberDTO.CreateResponseDTO responseBody = response.getBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getLoginId()).isEqualTo("user1");
        assertThat(responseBody.getEmail()).isEqualTo("user1@example.com");
        assertThat(responseBody.getName()).isEqualTo("User One");
        assertThat(responseBody.getPhoneNum()).isEqualTo("123-456-7890");
    }

//    @Test
//    @DisplayName("회원가입 후 로그인 테스트")
//    @Order(1)
//    void testRegister() throws Exception {
//
//        // given: 회원 정보 준비
//        MemberDTO.CreateRequestDTO dto = new MemberDTO.CreateRequestDTO();
//        dto.setLoginId(loginId);
//        dto.setEmail("user1@example.com");
//        dto.setPw(password);
//        dto.setName("User One");
//        dto.setPhoneNum("123-456-7890");
//        dto.setDateOfBirth(LocalDate.now());
//
//        //when: 회원가입 시도
//        ResponseEntity<MemberDTO.CreateResponseDTO> response = memberController.createMember(dto);
//
//        // then: 정상적으로 응답하는지 확인
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        MemberDTO.CreateResponseDTO responseBody = response.getBody();
//
//        assertThat(responseBody).isNotNull();
//        assertThat(responseBody.getLoginId()).isEqualTo("user1");
//        assertThat(responseBody.getEmail()).isEqualTo("user1@example.com");
//        assertThat(responseBody.getName()).isEqualTo("User One");
//        assertThat(responseBody.getPhoneNum()).isEqualTo("123-456-7890");
//
//        String loginUrl = "http://localhost:" + port + "/api/v1/members/login"; // 실제 로그인 엔드포인트를 입력하세요.
//
//        MemberDTO.LoginRequestDTO loginRequestDTO = new MemberDTO.LoginRequestDTO();
//        loginRequestDTO.setLoginId(loginId);
//        loginRequestDTO.setPw(password);
//
//        String loginDataJson = objectMapper.writeValueAsString(loginRequestDTO);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON); // Content-Type을 application/json으로 설정
//        HttpEntity<String> entity = new HttpEntity<>(loginDataJson, headers);
//
//        ResponseEntity<String> loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);
//
//        // 로그인 후 Authorization 헤더가 있는지 확인
//        String accessTokenHeader = loginResponse.getHeaders().getFirst("Authorization");
//        String refreshTokenHeader = loginResponse.getHeaders().getFirst("Refresh-Token");
//
//        // 검증
//        assertThat(accessTokenHeader).isNotNull();
//        assertThat(accessTokenHeader).startsWith("Bearer ");
//        assertThat(refreshTokenHeader).isNotNull();
//        assertThat(refreshTokenHeader).startsWith("Bearer ");
//    }

    @Test
    @DisplayName("로그인 테스트")
    @Order(1)
    void testBefore() throws Exception {
        String loginUrl = "http://localhost:" + port + "/api/v1/members/login"; // 실제 로그인 엔드 포인트
        url = "http://localhost:" + port + "/api/v1/members";

        MemberDTO.LoginRequestDTO loginRequestDTO = new MemberDTO.LoginRequestDTO();
        loginRequestDTO.setLoginId(loginId);
        loginRequestDTO.setPw(password);

        String loginDataJson = objectMapper.writeValueAsString(loginRequestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Content-Type을 application/json으로 설정
        HttpEntity<String> entity = new HttpEntity<>(loginDataJson, headers);

        ResponseEntity<String> loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);

        // 로그인 후 Authorization 헤더가 있는지 확인
        String accessTokenHeader = loginResponse.getHeaders().getFirst("Authorization");
        String refreshTokenHeader = loginResponse.getHeaders().getFirst("Refresh-Token");

        // 검증
        assertThat(accessTokenHeader).isNotNull();
        assertThat(accessTokenHeader).startsWith("Bearer ");
        assertThat(refreshTokenHeader).isNotNull();
        assertThat(refreshTokenHeader).startsWith("Bearer ");

        testUserToken = accessTokenHeader;

        System.out.println(testUserToken);
    }

    @Test
    @DisplayName("토큰 확인")
    @Order(2)
    void testAfter() {
        System.out.println(testUserToken);
    }

//    추후에 예외처리 완료 후 재작성
//    @Test
//    @DisplayName("잘못된 ID 입력시 예외 발생 테스트")
//    @Order(3)
//    void testIncorrectID() throws JsonProcessingException {
//        String loginUrl = "http://localhost:" + port + "/api/v1/members/login"; // 실제 로그인 엔드 포인트
//        String loginId = "user2"; // 존재하지 않는 ID
//
//        MemberDTO.LoginRequestDTO loginRequestDTO = new MemberDTO.LoginRequestDTO();
//        loginRequestDTO.setLoginId(loginId);
//        loginRequestDTO.setPw(password);
//
//        String loginDataJson = objectMapper.writeValueAsString(loginRequestDTO);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON); // Content-Type을 application/json으로 설정
//        HttpEntity<String> entity = new HttpEntity<>(loginDataJson, headers);
//
//        Throwable thrown = catchThrowable(() -> restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class));
//        assertThat(thrown).isInstanceOf(BadCredentialsException.class)
//                .hasMessageContaining("Authentication error: 잘못된 ID 혹은 PW 입니다");
//    }

    @Test
    @DisplayName("중복 회원 가입 시 예외 발생 테스트")
    @Order(3)
    void testDuplicateMemberRegistration() {

        MemberDTO.CreateRequestDTO createRequestDto = new MemberDTO.CreateRequestDTO();
        createRequestDto.setLoginId("user2"); // 동일한 loginId
        createRequestDto.setEmail("user2@example.com");
        createRequestDto.setPw("password456");
        createRequestDto.setName("User Two");
        createRequestDto.setPhoneNum("987-654-3210");

        memberService.createMember(createRequestDto.toEntity());

        // 같은 로그인 아이디로 또 가입 시 예외가 발생하는지 확인
        MemberDTO.CreateRequestDTO newCreateRequestDto = new MemberDTO.CreateRequestDTO();
        newCreateRequestDto.setLoginId("user2"); // 동일한 loginId
        newCreateRequestDto.setEmail("user2@example.com");
        newCreateRequestDto.setPw("password456");
        newCreateRequestDto.setName("User Two");
        newCreateRequestDto.setPhoneNum("987-654-3210");

        Throwable thrown = catchThrowable(() -> memberService.createMember(newCreateRequestDto.toEntity()));
        assertThat(thrown).isInstanceOf(MemberTaskException.class)
                .hasMessageContaining("이미 존재하는 로그인 아이디 입니다");
    }

    @Test
    @DisplayName("파트너 ID 수정 테스트")
    @Order(4)
    void testUpdatePartnerId() throws Exception {
        // 파트너 ID 수정 요청을 위한 DTO 생성
        MemberDTO.UpdatePartnerIdRequestDTO updateDto = new MemberDTO.UpdatePartnerIdRequestDTO(4L);

        // PUT 요청을 보낼 URL 설정
        url = "http://localhost:" + port + "/api/v1/members/partnerUpdate"; // 실제 엔드포인트 사용

        // HTTP 요청 헤더 설정 (Authorization 헤더 포함)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", testUserToken); // 로그인 후 받은 토큰을 Authorization 헤더에 추가
        headers.setContentType(MediaType.APPLICATION_JSON); // Content-Type을 application/json으로 설정

        // PUT 요청 보내기
        String requestBody = objectMapper.writeValueAsString(updateDto);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        // 응답 상태 코드 검증
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("partner updated");
    }

    @Test
    @DisplayName("마이페이지 로딩 테스트")
    @Order(5)
    void testGetMyPage() {

        // HttpHeaders 설정 (Authorization 헤더 추가)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", testUserToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // GET 요청 보내기
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        MemberDTO.MyPageResponseDTO response = memberService.getMyPage(3L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("User One");
        assertThat(response.getPartnerName()).isEqualTo("User Two");
    }

    @Test
    @DisplayName("내 정보 수정 테스트")
    @Order(6)
    void testUpdateMyInfo() throws JsonProcessingException {
        // 정보 수정 요청을 위한 DTO 생성
        MemberDTO.UpdateRequestDTO updateDto = new MemberDTO.UpdateRequestDTO(
                "user3",
                "updatedEmail@example.com",
                "1234",
                "updatedName",
                "010-9876-5432"
        );


        // PUT 요청을 보낼 URL 설정
        url = "http://localhost:" + port + "/api/v1/members/"; // 실제 엔드포인트 사용

        // HTTP 요청 헤더 설정 (Authorization 헤더 포함)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", testUserToken); // 로그인 후 받은 토큰을 Authorization 헤더에 추가
        headers.setContentType(MediaType.APPLICATION_JSON); // Content-Type을 application/json으로 설정

        // PUT 요청 보내기
        HttpEntity<MemberDTO.UpdateRequestDTO> entity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<MemberDTO.UpdateResponseDTO> response = restTemplate.exchange(url, HttpMethod.PUT, entity, MemberDTO.UpdateResponseDTO.class);

        // 응답 상태 코드 검증
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MemberDTO.UpdateResponseDTO responseDTO = response.getBody();

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getLoginId()).isEqualTo("user3");
        assertThat(responseDTO.getEmail()).isEqualTo("updatedEmail@example.com");
        assertThat(responseDTO.getName()).isEqualTo("updatedName");
        assertThat(responseDTO.getPhoneNum()).isEqualTo("010-9876-5432");

    }

    @Test
    @DisplayName("회원탈퇴 테스트")
    @Order(7)
    void testDeleteMember() {

        // HttpHeaders 설정 (Authorization 헤더 추가)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", testUserToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 보내기
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Deleted Successfully");

    }
}
