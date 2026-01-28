package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.menu.ChannelMenu;
import com.sprint.mission.discodeit.menu.MessageMenu;
import com.sprint.mission.discodeit.menu.UserMenu;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.util.InputUtil;

public class JavaApplication {
    private static UserService userService;
    private static ChannelService channelService;
    private static MessageService messageService;

    public static void main(String[] args) {

        // 1차 미션: JCF*Service (메모리 저장)
        // userService = new JCFUserService();
        // channelService = new JCFChannelService();
        // messageService = new JCFMessageService(userService, channelService);


        // 2차 미션 기본: File*Service (파일 저장)
        // userService = new FileUserService();
        // channelService = new FileChannelService();
        // messageService = new FileMessageService(userService, channelService);


        // 2차 미션 심화: Basic*Service + JCF Repository
        // UserRepository userRepository = new JCFUserRepository();
        // ChannelRepository channelRepository = new JCFChannelRepository();
        // MessageRepository messageRepository = new JCFMessageRepository();
        // userService = new BasicUserService(userRepository);
        // channelService = new BasicChannelService(channelRepository);
        // messageService = new BasicMessageService(messageRepository, userService, channelService);


        // 2차 심화: Basic*Service + File Repository
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();
        userService = new BasicUserService(userRepository);
        channelService = new BasicChannelService(channelRepository);
        messageService = new BasicMessageService(messageRepository, userService, channelService);

        // 초기 데이터 생성
        initializeData();

        // 메뉴 객체 생성
        UserMenu userMenu = new UserMenu(userService, messageService);
        ChannelMenu channelMenu = new ChannelMenu(channelService, messageService);
        MessageMenu messageMenu = new MessageMenu(messageService, userService, channelService);

        // 메인 메뉴
        while (true) {
            try {
                System.out.println("\n=== 디스코드잇 시스템 ===");
                System.out.println("1. 사용자 관리");
                System.out.println("2. 채널 관리");
                System.out.println("3. 메시지 관리");
                System.out.println("0. 종료");

                int choice = InputUtil.getIntInRange("선택: ", 0, 3);

                switch (choice) {
                    case 1:
                        userMenu.show();
                        break;
                    case 2:
                        channelMenu.show();
                        break;
                    case 3:
                        messageMenu.show();
                        break;
                    case 0:
                        System.out.println("프로그램을 종료합니다.");
                        return;
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
        }
    }

    private static void initializeData() {
        User user1 = userService.create("최재훈", "jh@email.com", "010-1234-5678");
        User user2 = userService.create("김성민", "sm@email.com", "010-2345-6789");
        User user3 = userService.create("김민석", "ms@email.com", "010-3456-7890");
        User user4 = userService.create("강민혁", "mh@email.com", "010-4567-8901");
        System.out.println("사용자 4명 생성 완료");

        Channel channel1 = channelService.create("일반", "일반 채널");
        Channel channel2 = channelService.create("공지사항", "공지사항 채널");
        Channel channel3 = channelService.create("질문", "질문 채널");
        System.out.println("채널 3개 생성 완료");

        messageService.create("제 이름은 최재훈입니다.", user1.getId(), channel1.getId());
        messageService.create("제 이름은 김성민입니다.", user2.getId(), channel1.getId());
        messageService.create("제 이름은 김민석입니다.", user3.getId(), channel1.getId());
        messageService.create("제 이름은 강민혁입니다.", user4.getId(), channel1.getId());
        messageService.create("공지사항이 있습니다.", user1.getId(), channel2.getId());
        messageService.create("공지 확인했습니다.", user2.getId(), channel2.getId());
        messageService.create("김민석 질문있습니다.", user3.getId(), channel3.getId());
        messageService.create("저 강민혁도 질문있습니다.", user4.getId(), channel3.getId());

        System.out.println("메시지 8개 생성 완료");
    }
}