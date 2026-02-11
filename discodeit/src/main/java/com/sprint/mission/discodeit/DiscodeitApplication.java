//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ConfigurableApplicationContext;
//
//@SpringBootApplication
//public class DiscodeitApplication {
//
//	static User setupUser(UserService userService) {
//		User user = userService.create("woody", "woody@codeit.com", "010-1234-1234");
//		return user;
//	}
//
//	static Channel setupChannel(ChannelService channelService) {
//		Channel channel = channelService.create("공지", "공지 채널입니다");
//		return channel;
//	}
//
//	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//		com.sprint.mission.discodeit.entity.Message message = messageService.create("오늘은 수요일입니다.", author.getId(), channel.getId());
//		System.out.println("메시지 생성: " + message.getId());
//	}
//
//	public static void main(String[] args) {
//		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
//
//		UserService userService = context.getBean(UserService.class);
//		ChannelService channelService = context.getBean(ChannelService.class);
//		MessageService messageService = context.getBean(MessageService.class);
//
//		User user = setupUser(userService);
//		Channel channel = setupChannel(channelService);
//
//		messageCreateTest(messageService, channel, user);
//		System.out.println("=== 테스트 ===");
//	}
//
//}
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// Bean 조회
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		AuthService authService = context.getBean(AuthService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

		System.out.println("\n=== 테스트  ===\n");

		//User 생성 테스트
		testUserService(userService);

		//Auth 로그인 테스트
		testAuthService(authService);

		//Channel 생성 테스트 (PUBLIC)
		testPublicChannel(channelService);

		//Channel 생성 테스트 (PRIVATE)
		testPrivateChannel(channelService, userService);

		//Message 생성 테스트
		testMessageService(messageService, userService, channelService);

		//ReadStatus 테스트
		testReadStatusService(readStatusService, userService, channelService);

		//UserStatus 테스트
		testUserStatusService(userStatusService, userService);

		//BinaryContent 테스트
		testBinaryContentService(binaryContentService);

		System.out.println("\n=== 테스트 완료! ===\n");
	}

	//User 테스트
	static void testUserService(UserService userService) {
		System.out.println("UserService 테스트");

		// User 생성
		UserCreateRequest createRequest = new UserCreateRequest(
				"woody",
				"woody@codeit.com",
				"password123"
		);
		UserResponse user = userService.create(createRequest);
		System.out.println("사용자 생성: " + user.getDisplayName());

		// User 조회
		UserResponse found = userService.findById(user.getId());
		System.out.println("사용자 조회: " + found.getDisplayName() + " (로그인 상태: " + found.getLoginStatus() + ")");

		System.out.println();
	}

	//Auth 테스트
	static void testAuthService(AuthService authService) {
		System.out.println("AuthService 테스트");

		// 로그인 성공
		LoginRequest loginRequest = new LoginRequest("woody", "password123");
		UserResponse loginUser = authService.login(loginRequest);
		System.out.println("로그인 성공: " + loginUser.getDisplayName());

		// 로그인 실패 테스트
		try {
			LoginRequest wrongRequest = new LoginRequest("woody", "wrongpassword");
			authService.login(wrongRequest);
		} catch (IllegalArgumentException e) {
			System.out.println("로그인 실패 처리: " + e.getMessage());
		}

		System.out.println();
	}

	//PUBLIC Channel 테스트
	static void testPublicChannel(ChannelService channelService) {
		System.out.println("ChannelService 테스트 (PUBLIC)");

		// PUBLIC 채널 생성
		PublicChannelCreateRequest publicRequest = new PublicChannelCreateRequest(
				"공지사항",
				"공지사항 채널입니다."
		);
		ChannelResponse publicChannel = channelService.createPublicChannel(publicRequest);
		System.out.println("PUBLIC 채널 생성: " + publicChannel.getChannelName() + " (타입: " + publicChannel.getType() + ")");

		System.out.println();
	}

	//PRIVATE Channel 테스트
	static void testPrivateChannel(ChannelService channelService, UserService userService) {
		System.out.println("ChannelService 테스트 (PRIVATE)");

		// 사용자 2명 추가 생성
		UserCreateRequest user1Request = new UserCreateRequest("alice", "alice@codeit.com", "pass1");
		UserCreateRequest user2Request = new UserCreateRequest("bob", "bob@codeit.com", "pass2");

		UserResponse user1 = userService.create(user1Request);
		UserResponse user2 = userService.create(user2Request);

		// PRIVATE 채널 생성
		PrivateChannelCreateRequest privateRequest = new PrivateChannelCreateRequest(
				Arrays.asList(user1.getId(), user2.getId())
		);
		ChannelResponse privateChannel = channelService.createPrivateChannel(privateRequest);
		System.out.println("PRIVATE 채널 생성: 타입=" + privateChannel.getType() +
				", 참여자 수=" + privateChannel.getParticipantUserIds().size());

		System.out.println();
	}

	//Message 테스트
	static void testMessageService(MessageService messageService, UserService userService, ChannelService channelService) {
		System.out.println("MessageService 테스트");

		// User와 Channel 가져오기
		List<UserResponse> users = userService.findAll();
		List<ChannelResponse> channels = channelService.findAllByUserId(users.get(0).getId());

		if (!users.isEmpty() && !channels.isEmpty()) {
			UserResponse user = users.get(0);
			ChannelResponse channel = channels.stream()
					.filter(c -> c.getType() == ChannelType.PUBLIC)
					.findFirst()
					.orElse(channels.get(0));

			// 메시지 생성
			MessageCreateRequest messageRequest = new MessageCreateRequest(
					"안녕하세요! 첫 메시지입니다.",
					user.getId(),
					channel.getId()
			);
			MessageResponse message = messageService.create(messageRequest);
			System.out.println("메시지 생성: " + message.getContent());

			// 채널별 메시지 조회
			List<MessageResponse> messages = messageService.findAllByChannelId(channel.getId());
			System.out.println("채널 메시지 조회: " + messages.size() + "개");
		}

		System.out.println();
	}

	//ReadStatus 테스트
	static void testReadStatusService(ReadStatusService readStatusService, UserService userService, ChannelService channelService) {
		System.out.println("ReadStatusService 테스트");

		List<UserResponse> users = userService.findAll();
		List<ChannelResponse> channels = channelService.findAllByUserId(users.get(0).getId());

		if (!users.isEmpty() && !channels.isEmpty()) {
			UserResponse user = users.get(0);
			ChannelResponse channel = channels.stream()
					.filter(c -> c.getType() == ChannelType.PUBLIC)
					.findFirst()
					.orElse(null);

			if (channel != null) {
				// ReadStatus 생성
				ReadStatusCreateRequest readRequest = new ReadStatusCreateRequest(
						user.getId(),
						channel.getId()
				);
				ReadStatusResponse readStatus = readStatusService.create(readRequest);
				System.out.println("ReadStatus 생성: userId=" + readStatus.getUserId());

				// 사용자별 ReadStatus 조회
				List<ReadStatusResponse> readStatuses = readStatusService.findAllByUserId(user.getId());
				System.out.println("사용자 ReadStatus 조회: " + readStatuses.size() + "개");
			}
		}

		System.out.println();
	}

	//UserStatus 테스트
	static void testUserStatusService(UserStatusService userStatusService, UserService userService) {
		System.out.println("UserStatusService 테스트");

		//UserStatus는 User 생성 시 자동으로 생성
		List<UserStatusResponse> userStatuses = userStatusService.findAll();
		System.out.println("전체 UserStatus 조회: " + userStatuses.size() + "개");

		if (!userStatuses.isEmpty()) {
			UserStatusResponse userStatus = userStatuses.get(0);
			System.out.println("UserStatus 상태: " + userStatus.getLoginStatus());

			// updateByUserId 테스트
			UserStatusResponse updated = userStatusService.updateByUserId(
					userStatus.getUserId(),
					Instant.now()
			);
			System.out.println("UserStatus 업데이트: " + updated.getLoginStatus());
		}

		System.out.println();
	}

	//BinaryContent 테스트
	static void testBinaryContentService(BinaryContentService binaryContentService) {
		System.out.println("BinaryContentService 테스트");

		// BinaryContent 생성
		byte[] testData = "test file content".getBytes();
		BinaryContentRequest binaryRequest = new BinaryContentRequest(
				"test.txt",
				"text/plain",
				(long) testData.length,
				testData
		);
		BinaryContentResponse binaryContent = binaryContentService.create(binaryRequest);
		System.out.println("BinaryContent 생성: " + binaryContent.getFilename() +
				" (" + binaryContent.getFileSize() + " bytes)");

		// 조회
		BinaryContentResponse found = binaryContentService.findById(binaryContent.getId());
		System.out.println("BinaryContent 조회: " + found.getFilename());

		System.out.println();
	}
}