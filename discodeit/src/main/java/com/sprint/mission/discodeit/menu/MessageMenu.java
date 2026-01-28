package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.DisplayHelper;
import com.sprint.mission.discodeit.util.InputUtil;
import com.sprint.mission.discodeit.util.MenuHelper;

import java.util.List;
import java.util.UUID;

public class MessageMenu {
    private final MessageService messageService;
    private final UserService userService;
    private final ChannelService channelService;

    public MessageMenu(MessageService messageService, UserService userService, ChannelService channelService) {
        this.messageService = messageService;
        this.userService = userService;
        this.channelService = channelService;
    }

    public void show() {
        while (true) {
            try {
                System.out.println("\n메시지 관리");
                System.out.println("1) 메시지 조회");
                System.out.println("2) 메시지 생성");
                System.out.println("3) 메시지 수정");
                System.out.println("4) 메시지 삭제");
                System.out.println("9) 검증 테스트");
                System.out.println("0) 뒤로가기");

                int choice = InputUtil.getIntInRange("선택: ", 0, 9);

                switch (choice) {
                    case 1:
                        viewMessages();
                        break;
                    case 2:
                        createMessage();
                        break;
                    case 3:
                        updateMessage();
                        break;
                    case 4:
                        deleteMessage();
                        break;
                    case 9:
                        testValidation();
                        break;
                    case 0:
                        return;
                }
            } catch (Exception e) {
                DisplayHelper.showError("오류 발생: " + e.getMessage());
            }
        }
    }

    // 메시지 조회
    private void viewMessages() {
        try {
            List<Message> messages = messageService.findAll();

            if (messages.isEmpty()) {
                DisplayHelper.showError("전송된 메시지가 없습니다.");
                return;
            }

            System.out.println("\n메시지 조회 옵션");
            System.out.println("1. 전체 메시지");
            System.out.println("2. 채널별 필터링");
            System.out.println("3. 작성자별 필터링");
            System.out.println("0. 취소");

            int option = InputUtil.getIntInRange("선택: ", 0, 3);

            if (option == 0) {
                DisplayHelper.showCancelled();
                return;
            }

            List<Message> filteredMessages = getFilteredMessages(option, messages);

            if (filteredMessages == null) return; // 취소됨

            displayMessageList(filteredMessages);

        } catch (Exception e) {
            DisplayHelper.showError("조회 중 오류 발생: " + e.getMessage());
        }
    }

    private List<Message> getFilteredMessages(int option, List<Message> allMessages) {
        // ✅ switch 표현식 → 전통적 switch 문
        List<Message> result;
        switch (option) {
            case 1:
                result = allMessages;
                break;
            case 2:
                result = filterByChannel();
                break;
            case 3:
                result = filterByAuthor();
                break;
            default:
                result = null;
                break;
        }
        return result;
    }

    private List<Message> filterByChannel() {
        Channel channel = MenuHelper.selectFromList(
                channelService.findAll(),
                Channel::getChannelName,
                "채널 선택"
        );

        return (channel != null) ? messageService.findByChannelId(channel.getId()) : null;
    }

    private List<Message> filterByAuthor() {
        User user = MenuHelper.selectFromList(
                userService.findAll(),
                User::getDisplayName,
                "작성자 선택"
        );

        return (user != null) ? messageService.findByAuthorId(user.getId()) : null;
    }

    private void displayMessageList(List<Message> messages) {
        System.out.println("\n메시지 목록");

        if (messages.isEmpty()) {
            System.out.println("조건에 맞는 메시지가 없습니다.");
            return;
        }

        for (int i = 0; i < messages.size(); i++) {
            System.out.println(formatMessage(messages.get(i), i + 1));
        }
    }

    private String formatMessage(Message message, int index) {
        User author = userService.findById(message.getAuthorId());
        Channel channel = channelService.findById(message.getChannelId());

        String authorName = (author != null) ? author.getDisplayName() : "[삭제된 사용자]";
        String channelName = (channel != null) ? channel.getChannelName() : "[삭제된 채널]";

        return "   " + index + ". [" + channelName + "] " + authorName + ": " + message.getContent();
    }

    // 메시지 생성
    private void createMessage() {
        try {
            System.out.println("\n=== 메시지 생성 ===");

            // 작성자 선택
            User author = MenuHelper.selectFromList(
                    userService.findAll(),
                    User::getDisplayName,
                    "작성자 선택"
            );
            if (author == null) return;

            // 채널 선택
            Channel channel = MenuHelper.selectFromList(
                    channelService.findAll(),
                    Channel::getChannelName,
                    "채널 선택"
            );
            if (channel == null) return;

            // 메시지 내용 입력
            String content = InputUtil.getStringOrCancel("메시지 내용");
            if (content == null) {
                DisplayHelper.showCancelled();
                return;
            }

            // 메시지 생성
            Message message = messageService.create(content, author.getId(), channel.getId());

            System.out.println("\n✅ 메시지가 생성되었습니다!");
            System.out.println("   [" + channel.getChannelName() + "] " +
                    author.getDisplayName() + ": " + message.getContent());

        } catch (IllegalArgumentException e) {
            DisplayHelper.showError(e.getMessage());
        } catch (Exception e) {
            DisplayHelper.showError("오류 발생: " + e.getMessage());
        }
    }

    // 메시지 수정
    private void updateMessage() {
        try {
            List<Message> messages = messageService.findAll();

            if (messages.isEmpty()) {
                DisplayHelper.showError("수정할 메시지가 없습니다.");
                return;
            }

            System.out.println("\n수정할 메시지 선택");
            for (int i = 0; i < messages.size(); i++) {
                System.out.println(formatMessage(messages.get(i), i + 1));
            }
            System.out.println("   0. 취소");

            int choice = InputUtil.getIntInRange("선택: ", 0, messages.size());

            if (choice == 0) {
                DisplayHelper.showCancelled();
                return;
            }

            Message message = messages.get(choice - 1);

            System.out.println("\n현재 내용: " + message.getContent());
            String newContent = InputUtil.getStringOrCancel("새 내용: ");

            if (newContent == null) {
                DisplayHelper.showCancelled();
                return;
            }

            Message updated = messageService.update(message.getId(), newContent);

            if (updated != null) {
                DisplayHelper.showSuccess("메시지가 수정되었습니다.");
            } else {
                DisplayHelper.showError("메시지 수정에 실패했습니다.");
            }

        } catch (Exception e) {
            DisplayHelper.showError("수정 중 오류 발생: " + e.getMessage());
        }
    }

    // 메시지 삭제
    private void deleteMessage() {
        try {
            List<Message> messages = messageService.findAll();

            if (messages.isEmpty()) {
                DisplayHelper.showError("삭제할 메시지가 없습니다.");
                return;
            }

            System.out.println("\n삭제할 메시지 선택");
            for (int i = 0; i < messages.size(); i++) {
                System.out.println(formatMessage(messages.get(i), i + 1));
            }
            System.out.println("   0. 취소");

            int choice = InputUtil.getIntInRange("선택: ", 0, messages.size());

            if (choice == 0) {
                DisplayHelper.showCancelled();
                return;
            }

            Message message = messages.get(choice - 1);
            User author = userService.findById(message.getAuthorId());
            String authorName = (author != null) ? author.getDisplayName() : "[삭제된 사용자]";

            if (InputUtil.confirm("'" + authorName + "'의 메시지를 정말 삭제하시겠습니까?")) {
                messageService.delete(message.getId());
                DisplayHelper.showSuccess("메시지가 삭제되었습니다.");
            } else {
                DisplayHelper.showCancelled();
            }

        } catch (Exception e) {
            DisplayHelper.showError("삭제 중 오류 발생: " + e.getMessage());
        }
    }

    // 검증 테스트
    private void testValidation() {
        try {
            System.out.println("\n=== 검증 테스트 ===");

            // 작성자 선택
            UUID selectedUserId = selectUserForTest();
            if (selectedUserId == null) return;

            // 채널 선택
            UUID selectedChannelId = selectChannelForTest();
            if (selectedChannelId == null) return;

            // 메시지 내용 입력
            String content = InputUtil.getString("\n메시지 내용: ");

            // 메시지 생성
            Message message = messageService.create(content, selectedUserId, selectedChannelId);

            System.out.println("\n✅ 메시지가 생성되었습니다!");
            System.out.println("ID: " + message.getId());
            System.out.println("내용: " + message.getContent());

        } catch (IllegalArgumentException e) {
            DisplayHelper.showError("검증 실패: " + e.getMessage());
        } catch (Exception e) {
            DisplayHelper.showError("오류 발생: " + e.getMessage());
        }
    }

    private UUID selectUserForTest() {
        List<User> users = userService.findAll();

        if (users.isEmpty()) {
            DisplayHelper.showError("사용자가 없습니다.");
            return null;
        }

        System.out.println("\n작성자 선택:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + users.get(i).getDisplayName());
        }
        System.out.println("   0. 존재하지 않는 사용자 (검증 테스트)");

        int userChoice = InputUtil.getIntInRange("선택: ", 0, users.size());

        if (userChoice == 0) {
            UUID randomId = UUID.randomUUID();
            System.out.println("존재하지 않는 사용자 ID로 테스트: " + randomId);
            return randomId;
        } else {
            User selected = users.get(userChoice - 1);
            System.out.println("선택: " + selected.getDisplayName());
            return selected.getId();
        }
    }

    private UUID selectChannelForTest() {
        List<Channel> channels = channelService.findAll();

        if (channels.isEmpty()) {
            DisplayHelper.showError("채널이 없습니다.");
            return null;
        }

        System.out.println("\n채널 선택:");
        for (int i = 0; i < channels.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + channels.get(i).getChannelName());
        }
        System.out.println("   0. 존재하지 않는 채널 (검증 테스트)");

        int channelChoice = InputUtil.getIntInRange("선택: ", 0, channels.size());

        if (channelChoice == 0) {
            UUID randomId = UUID.randomUUID();
            System.out.println("존재하지 않는 채널 ID로 테스트: " + randomId);
            return randomId;
        } else {
            Channel selected = channels.get(channelChoice - 1);
            System.out.println("선택: " + selected.getChannelName());
            return selected.getId();
        }
    }
}