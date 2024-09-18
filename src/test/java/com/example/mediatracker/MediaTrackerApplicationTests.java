package com.example.mediatracker;

import com.example.mediatracker.dto.AuthenticationDTO;
import com.example.mediatracker.dto.MediaItemDTO;
import com.example.mediatracker.dto.MediaTypeDTO;
import com.example.mediatracker.dto.RegisterDTO;
import com.example.mediatracker.enums.MediaStatus;
import com.example.mediatracker.enums.UserRole;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MediaTrackerApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	private static String adminToken;
	private static String userToken;

	private HttpEntity<Void> createAdminHttpEntityGET() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);

		return new HttpEntity<>(headers);
	}

	private HttpEntity<Void> createUserHttpEntityGET() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + userToken);

		return new HttpEntity<>(headers);
	}

	@BeforeAll
    static void loginUsers(@Autowired TestRestTemplate restTemplate) {

		AuthenticationDTO adminAuthenticationDto = new AuthenticationDTO(
				"admin",
				"admin123");
		ResponseEntity<String> adminLoginResponse = restTemplate
				.postForEntity("/auth/login", adminAuthenticationDto, String.class);

		DocumentContext adminDocumentContext = JsonPath.parse(adminLoginResponse.getBody());
		adminToken = adminDocumentContext.read("$.token");


		AuthenticationDTO userAuthenticationDto = new AuthenticationDTO(
				"user",
				"user123");
		ResponseEntity<String> userLoginResponse = restTemplate
				.postForEntity("/auth/login", userAuthenticationDto, String.class);

		DocumentContext userDocumentContext = JsonPath.parse(userLoginResponse.getBody());
		userToken = userDocumentContext.read("$.token");
	}

	// ---------- Authentication Tests ----------

	@Test
	void shouldLoginAUser() {
		AuthenticationDTO authenticationDto = new AuthenticationDTO(
				"user",
				"user123");
		ResponseEntity<String> loginResponse = restTemplate
				.postForEntity("/auth/login", authenticationDto, String.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(loginResponse.getBody());

		String token = documentContext.read("$.token");
		assertThat(token).isNotNull();
	}

	@Test
	void shouldNotLoginAnInvalidUser() {
		AuthenticationDTO authenticationDto = new AuthenticationDTO(
				"user",
				"user123456");
		ResponseEntity<Void> loginResponse = restTemplate
				.postForEntity("/auth/login", authenticationDto, Void.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	@DirtiesContext
	void shouldRegisterANewUser() {
		RegisterDTO newRegisterDto = new RegisterDTO(
				"test",
				"test123",
				UserRole.USER);
		ResponseEntity<Void> registerResponse = restTemplate
				.postForEntity("/auth/register", newRegisterDto, Void.class);
		assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldNotRegisterAnExistingUser() {
		RegisterDTO newRegisterDto = new RegisterDTO(
				"user",
				"user123",
				UserRole.USER);
		ResponseEntity<Void> registerResponse = restTemplate
				.postForEntity("/auth/register", newRegisterDto, Void.class);
		assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void userCannotCreateUpdateOrDeleteAMediaItem() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + userToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MediaItemDTO newMediaItemDto = new MediaItemDTO(
				"Avengers",
				null,
				null,
				null,
				MediaStatus.WISHLIST,
				88L,
				null);

		HttpEntity<MediaItemDTO> requestEntityPost = new HttpEntity<>(newMediaItemDto, headers);

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/media-item", requestEntityPost, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);


		MediaItemDTO mediaItemDTOUpdate = new MediaItemDTO(
				"Baldurs Gate 3",
				10,
				LocalDate.of(2023, 11, 20),
				null,
				MediaStatus.ON_HOLD,
				99L,
				null);

		HttpEntity<MediaItemDTO> requestEntityPut = new HttpEntity<>(mediaItemDTOUpdate, headers);

		ResponseEntity<Void> updateResponse = restTemplate
				.exchange("/media-item/11", HttpMethod.PUT, requestEntityPut, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);


		HttpEntity<Void> requestEntityDelete = new HttpEntity<>(headers);

		ResponseEntity<Void> deleteResponse = restTemplate
				.exchange("/media-item/11", HttpMethod.DELETE, requestEntityDelete, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void userCannotCreateUpdateOrDeleteAMediaType() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + userToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MediaTypeDTO newMediaTypeDto = new MediaTypeDTO("Book");

		HttpEntity<MediaTypeDTO> requestEntityPost = new HttpEntity<>(newMediaTypeDto, headers);

		ResponseEntity<Void> createResponse = restTemplate
				.exchange("/media-type", HttpMethod.POST, requestEntityPost, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);


		MediaTypeDTO mediaTypeDTOUpdate = new MediaTypeDTO("Movies and Series");

		HttpEntity<MediaTypeDTO> requestEntityPut = new HttpEntity<>(mediaTypeDTOUpdate, headers);

		ResponseEntity<Void> updateResponse = restTemplate
				.exchange("/media-type/88", HttpMethod.PUT, requestEntityPut, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);


		HttpEntity<Void> requestEntityDelete = new HttpEntity<>(headers);

		ResponseEntity<Void> deleteResponse = restTemplate
				.exchange("/media-type/77", HttpMethod.DELETE, requestEntityDelete, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void userShouldNotGetUser() {
		ResponseEntity<String> getOneResponse = restTemplate
				.exchange("/user/199", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(getOneResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

		ResponseEntity<String> getAllResponse = restTemplate
				.exchange("/user", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}


	// ---------- User Tests ----------

	@Test
	void shouldReturnOneUser() {
		ResponseEntity<String> response = restTemplate
				.exchange("/user/188", HttpMethod.GET, createAdminHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(188);

		String userName = documentContext.read("$.userName");
		assertThat(userName).isEqualTo("user");

		String role = documentContext.read("$.role");
		assertThat(role).isEqualTo("USER");
	}

	@Test
	void shouldReturnAllUser() {
		ResponseEntity<String> response = restTemplate
				.exchange("/user", HttpMethod.GET, createAdminHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int mediaItemCount = documentContext.read("$.length()");
		assertThat(mediaItemCount).isEqualTo(2);
	}


	// ---------- Media Item Tests ----------

	@Test
	void shouldReturnOneMediaItem() {
		ResponseEntity<String> response = restTemplate
				.exchange("/media-item/25", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(25);

		String title = documentContext.read("$.title");
		assertThat(title).isEqualTo("Portal 2");

		Number rating = documentContext.read("$.rating");
		assertThat(rating).isEqualTo(10);

		String startDate = documentContext.read("$.startDate");
		assertThat(startDate).isEqualTo("2024-02-21");

		String finishDate = documentContext.read("$.finishDate");
		assertThat(finishDate).isEqualTo("2024-02-25");

		String status = documentContext.read("$.status");
		assertThat(status).isEqualTo("COMPLETED");

		Number mediaTypeId = documentContext.read("$.mediaType.id");
		assertThat(mediaTypeId).isEqualTo(99);

		String mediaTypeName = documentContext.read("$.mediaType.name");
		assertThat(mediaTypeName).isEqualTo("Game");

		String notes = documentContext.read("$.notes");
		assertThat(notes).isNull();
	}

	@Test
	void shouldReturnAllMediaItem() {
		ResponseEntity<String> response = restTemplate
				.exchange("/media-item", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int mediaItemCount = documentContext.read("$.length()");
		assertThat(mediaItemCount).isEqualTo(4);
	}

	@Test
	void shouldReturnAllMediaItemContainingTitle() {
		ResponseEntity<String> response = restTemplate
				.exchange("/media-item?title=portal", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int mediaItemCount = documentContext.read("$.length()");
		assertThat(mediaItemCount).isEqualTo(2);
	}

	@Test
	void shouldReturnAllMediaItemWithGivenRating() {
		ResponseEntity<String> response = restTemplate
				.exchange("/media-item?rating=10", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int mediaItemCount = documentContext.read("$.length()");
		assertThat(mediaItemCount).isEqualTo(3);
	}

	@Test
	void shouldReturnAllMediaItemWithGivenStatus() {
		ResponseEntity<String> response = restTemplate
				.exchange("/media-item?status=IN_PROGRESS", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int mediaItemCount = documentContext.read("$.length()");
		assertThat(mediaItemCount).isEqualTo(1);
	}

	@Test
	void shouldReturnAllMediaItemWithGivenMediaTypeId() {
		ResponseEntity<String> response = restTemplate
				.exchange("/media-item?mediaTypeId=99", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int mediaItemCount = documentContext.read("$.length()");
		assertThat(mediaItemCount).isEqualTo(3);
	}

	@Test
	void shouldReturnAllMediaItemWithGivenTitleRatingStatusMediaTypeId() {
		ResponseEntity<String> response = restTemplate
				.exchange("/media-item?title=portal&rating=10&status=COMPLETED&mediaTypeId=99", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int mediaItemCount = documentContext.read("$.length()");
		assertThat(mediaItemCount).isEqualTo(2);
	}

	@Test
	@DirtiesContext
	void shouldSaveANewMediaItem() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MediaItemDTO newMediaItemDto = new MediaItemDTO(
				"Deadpool",
				null,
				null,
				null,
				MediaStatus.WISHLIST,
				88L,
				null);

		HttpEntity<MediaItemDTO> requestEntity = new HttpEntity<>(newMediaItemDto, headers);

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/media-item", requestEntity, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNewMediaItem = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate
				.exchange(locationOfNewMediaItem, HttpMethod.GET, createAdminHttpEntityGET(), String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		Number id = documentContext.read("$.id");
		assertThat(id).isNotNull();

		String title = documentContext.read("$.title");
		assertThat(title).isEqualTo("Deadpool");

		Number rating = documentContext.read("$.rating");
		assertThat(rating).isNull();

		String startDate = documentContext.read("$.startDate");
		assertThat(startDate).isNull();

		String finishDate = documentContext.read("$.finishDate");
		assertThat(finishDate).isNull();

		String status = documentContext.read("$.status");
		assertThat(status).isEqualTo("WISHLIST");

		Number mediaTypeId = documentContext.read("$.mediaType.id");
		assertThat(mediaTypeId).isEqualTo(88);

		String mediaTypeName = documentContext.read("$.mediaType.name");
		assertThat(mediaTypeName).isEqualTo("Movie");

		String notes = documentContext.read("$.notes");
		assertThat(notes).isNull();
	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingMediaItem() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MediaItemDTO mediaItemUpdateDto = new MediaItemDTO(
				"Baldurs Gate 3",
				10,
				LocalDate.of(2024, 4, 29),
				LocalDate.of(2024, 5, 1),
				MediaStatus.COMPLETED,
				99L,
				null);

		HttpEntity<MediaItemDTO> requestEntity = new HttpEntity<>(mediaItemUpdateDto, headers);

		ResponseEntity<Void> updateResponse = restTemplate
				.exchange("/media-item/11", HttpMethod.PUT, requestEntity, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
				.exchange("/media-item/11", HttpMethod.GET, createAdminHttpEntityGET(), String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		Number id = documentContext.read("$.id");
		assertThat(id).isNotNull();

		String title = documentContext.read("$.title");
		assertThat(title).isEqualTo("Baldurs Gate 3");

		Number rating = documentContext.read("$.rating");
		assertThat(rating).isEqualTo(10);

		String startDate = documentContext.read("$.startDate");
		assertThat(startDate).isEqualTo("2024-04-29");

		String finishDate = documentContext.read("$.finishDate");
		assertThat(finishDate).isEqualTo("2024-05-01");

		String status = documentContext.read("$.status");
		assertThat(status).isEqualTo("COMPLETED");

		Number mediaTypeId = documentContext.read("$.mediaType.id");
		assertThat(mediaTypeId).isEqualTo(99);

		String mediaTypeName = documentContext.read("$.mediaType.name");
		assertThat(mediaTypeName).isEqualTo("Game");

		String notes = documentContext.read("$.notes");
		assertThat(notes).isNull();
	}

	@Test
	void shouldNotUpdateAMediaItemThatDoesNotExist() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MediaItemDTO unknownMediaItem = new MediaItemDTO(
				"Unknown Game",
				1,
				null,
				null,
				MediaStatus.COMPLETED,
				1L,
				null);

		HttpEntity<MediaItemDTO> requestEntity = new HttpEntity<>(unknownMediaItem, headers);

		ResponseEntity<Void> updateResponse = restTemplate
				.exchange("/media-item/99999", HttpMethod.PUT, requestEntity, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingMediaItem() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<Void> deleteResponse = restTemplate
				.exchange("/media-item/11", HttpMethod.DELETE, requestEntity, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
				.exchange("/media-item/11", HttpMethod.GET, createAdminHttpEntityGET(), String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteAMediaItemThatDoesNotExist() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<Void> deleteResponse = restTemplate
				.exchange("/media-item/99999", HttpMethod.DELETE, requestEntity, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}


	// ---------- Media Type Tests ----------


	@Test
	void shouldReturnOneMediaType() {
		ResponseEntity<String> response = restTemplate
				.exchange("/media-type/99", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(99);

		String name = documentContext.read("$.name");
		assertThat(name).isEqualTo("Game");
	}

	@Test
	void shouldReturnAllMediaType() {
		ResponseEntity<String> response = restTemplate
				.exchange("/media-type", HttpMethod.GET, createUserHttpEntityGET(), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int mediaTypeCount = documentContext.read("$.length()");
		assertThat(mediaTypeCount).isEqualTo(3);
	}

	@Test
	@DirtiesContext
	void shouldSaveANewMediaType() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MediaTypeDTO newMediaTypeDto = new MediaTypeDTO("Book");

		HttpEntity<MediaTypeDTO> requestEntity = new HttpEntity<>(newMediaTypeDto, headers);

		ResponseEntity<Void> createResponse = restTemplate
				.exchange("/media-type", HttpMethod.POST, requestEntity, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNewMediaType = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate
				.exchange(locationOfNewMediaType, HttpMethod.GET, createAdminHttpEntityGET(), String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		Number id = documentContext.read("$.id");
		assertThat(id).isNotNull();

		String name = documentContext.read("$.name");
		assertThat(name).isEqualTo("Book");
	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingMediaType() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MediaTypeDTO mediaTypeUpdateDto = new MediaTypeDTO("Movies and Series");

		HttpEntity<MediaTypeDTO> requestEntity = new HttpEntity<>(mediaTypeUpdateDto, headers);

		ResponseEntity<Void> updateResponse = restTemplate
				.exchange("/media-type/88", HttpMethod.PUT, requestEntity, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
				.exchange("/media-type/88", HttpMethod.GET, createAdminHttpEntityGET(), String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		Number id = documentContext.read("$.id");
		assertThat(id).isNotNull();

		String name = documentContext.read("$.name");
		assertThat(name).isEqualTo("Movies and Series");
	}

	@Test
	void shouldNotUpdateAMediaTypeThatDoesNotExist() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MediaTypeDTO unknownMediaType = new MediaTypeDTO("Unknown");

		HttpEntity<MediaTypeDTO> requestEntity = new HttpEntity<>(unknownMediaType, headers);

		ResponseEntity<Void> updateResponse = restTemplate
				.exchange("/media-type/99999", HttpMethod.PUT, requestEntity, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingMediaTypeWithNoMediaItem() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<Void> deleteResponse = restTemplate
				.exchange("/media-type/77", HttpMethod.DELETE, requestEntity, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
				.exchange("/media-type/77", HttpMethod.GET, createAdminHttpEntityGET(), String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteAnExistingMediaTypeWithAnyMediaItem() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<Void> deleteResponse = restTemplate
				.exchange("/media-type/88", HttpMethod.DELETE, requestEntity, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteAMediaTypeThatDoesNotExist() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + adminToken);
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<Void> deleteResponse = restTemplate
				.exchange("/media-type/99999", HttpMethod.DELETE, requestEntity, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
