package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.DisplayHelper;
import com.sprint.mission.discodeit.util.InputUtil;
import com.sprint.mission.discodeit.util.MenuHelper;

public class ChannelMenu {
    private final ChannelService channelService;
    private final MessageService messageService;

    public ChannelMenu(ChannelService channelService, MessageService messageService) {
        this.channelService = channelService;
        this.messageService = messageService;
    }

    public void show() {
        while (true) {
            try {
                System.out.println("\n채널 관리");
                System.out.println("1) 채널 조회");
                System.out.println("2) 채널 수정");
                System.out.println("3) 채널 삭제");
                System.out.println("0) 뒤로가기");

                int choice = InputUtil.getIntInRange("선택: ", 0, 3);

                switch (choice) {
                    case 1:
                        viewChannels();
                        break;
                    case 2:
                        updateChannel();
                        break;
                    case 3:
                        deleteChannel();
                        break;
                    case 0:
                        return;
                }
            } catch (Exception e) {
                DisplayHelper.showError("오류 발생: " + e.getMessage());
            }
        }
    }

    // 채널 조회
    private void viewChannels() {
        MenuHelper.viewWithDetailOption(
                channelService.findAll(),
                channel -> channel.getChannelName() + " - " + channel.getDescription(),
                DisplayHelper::printChannelDetail,
                "채널"
        );
    }

    // 채널 수정
    private void updateChannel() {
        Channel channel = MenuHelper.selectFromList(
                channelService.findAll(),
                Channel::getChannelName,
                "수정할 채널 선택"
        );

        if (channel == null) return;

        // 필드별 수정 루프
        while (true) {
            DisplayHelper.showEditingHeader(channel.getChannelName(), "");
            System.out.println("1. 채널명 (현재: " + channel.getChannelName() + ")");
            System.out.println("2. 설명 (현재: " + channel.getDescription() + ")");
            System.out.println("0. 수정 완료");

            int fieldChoice = InputUtil.getIntInRange("선택: ", 0, 2);

            if (fieldChoice == 0) {
                DisplayHelper.showEditComplete("채널");
                break;
            }

            String input = getFieldInput(fieldChoice);
            if (input != null) {
                updateChannelField(channel, fieldChoice, input);
                channel = channelService.findById(channel.getId()); // 갱신
            }
        }
    }

    private String getFieldInput(int fieldChoice) {
        String prompt;
        switch (fieldChoice) {
            case 1:
                prompt = "새 채널명: ";
                break;
            case 2:
                prompt = "새 설명: ";
                break;
            default:
                prompt = null;
                break;
        }
        return prompt != null ? InputUtil.getStringOrCancel(prompt) : null;
    }

    private void updateChannelField(Channel channel, int fieldChoice, String newValue) {
        switch (fieldChoice) {
            case 1:
                channelService.update(channel.getId(), newValue, channel.getDescription());
                break;
            case 2:
                channelService.update(channel.getId(), channel.getChannelName(), newValue);
                break;
        }
    }

    // 채널 삭제
    private void deleteChannel() {
        Channel channel = MenuHelper.selectFromList(
                channelService.findAll(),
                Channel::getChannelName,
                "삭제할 채널 선택"
        );

        if (channel == null) return;

        int messageCount = messageService.findByChannelId(channel.getId()).size();

        if (MenuHelper.confirmDelete(channel.getChannelName(), messageCount, "메시지")) {
            int deletedMessages = messageService.deleteByChannelId(channel.getId());
            channelService.delete(channel.getId());
            DisplayHelper.showDeleteSuccess("채널", deletedMessages, "메시지");
        } else {
            DisplayHelper.showCancelled();
        }
    }
}