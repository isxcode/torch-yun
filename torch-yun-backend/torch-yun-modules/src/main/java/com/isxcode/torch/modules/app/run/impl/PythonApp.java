package com.isxcode.torch.modules.app.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.app.constants.AppType;
import com.isxcode.torch.api.app.dto.ChatResponse;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.app.bot.BotFactory;
import com.isxcode.torch.modules.app.run.App;
import com.isxcode.torch.modules.app.run.RunCode;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class PythonApp extends App {

    private final BotFactory botFactory;

    private final ChatSessionRepository chatSessionRepository;

    public PythonApp(BotFactory botFactory, ChatSessionRepository chatSessionRepository,
        ChatSessionRepository chatSessionRepository1) {

        super(chatSessionRepository);
        this.botFactory = botFactory;
        this.chatSessionRepository = chatSessionRepository1;
    }

    @Override
    public String appType() {
        return AppType.PYTHON_APP;
    }

    @Override
    public void start(BotChatContext botChatContext, SseEmitter sseEmitter) {

        Integer runCode = 1;
        List<Integer> runningCode = Arrays.asList(1, 2, 3, 4, 5);

        Bot bot = botFactory.getBot(botChatContext.getModelCode());

        // 封装首次请求
        ChatSessionEntity userAskSession = chatSessionRepository.findById(botChatContext.getUserAskSessionId()).get();
        ChatContent firstChatContent = JSON.parseObject(userAskSession.getSessionContent(), ChatContent.class);
        ChatResponse[] chatResponseHolder = new ChatResponse[1];
        chatResponseHolder[0] = ChatResponse.builder().content(firstChatContent.getContent()).build();

        // 直到成功为止
        while (runningCode.contains(runCode)) {

            // 让ai写一个简单的思路
            if (runCode == 1) {
                runCode = chatWay1(botChatContext, bot, sseEmitter, chatResponseHolder);
                continue;
            }

            // 让ai写出python代码
            if (runCode == 2) {
                runCode = chatWay2(botChatContext, bot, sseEmitter, chatResponseHolder);
                continue;
            }

            // 让计算引擎执行python代码并让ai分析结果
            if (runCode == 3) {
                runCode = chatWay3(botChatContext, bot, sseEmitter, chatResponseHolder);
                continue;
            }

            // 让ai判断执行结果
            if (runCode == 4) {
                runCode = chatWay4(botChatContext, bot, sseEmitter, chatResponseHolder);
                continue;
            }

            // 让ai优化一下结果内容，以好的形式展示
            if (runCode == 5) {
                runCode = chatWay5(botChatContext, bot, sseEmitter, chatResponseHolder);
            }
        }

        // 找到对应的ai体

        // 获取当前会话实体
        ChatSessionEntity nowChatSession = chatSessionRepository
            .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();

        // 保存聊天对话状态和内容
        ChatContent chatContent = ChatContent.builder().content(chatResponseHolder[0].getContent()).build();
        nowChatSession.setSessionContent(JSON.toJSONString(chatContent));
        nowChatSession.setStatus(ChatSessionStatus.OVER);
        chatSessionRepository.saveAndFlush(nowChatSession);
    }

    public static String executePythonCommand(String pythonScript) {
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "-u", "-c", pythonScript);

        try {
            // 启动进程
            Process process = processBuilder.start();

            // 获取输入流（python代码的输出）
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // 读取正常输出
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 读取错误输出
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            // 等待进程执行完成
            int exitCode = process.waitFor();

            // 如果有错误输出，将其附加到结果中
            if (errorOutput.length() > 0) {
                output.append("Error: ").append(errorOutput);
            }

            // 添加退出码信息
            output.append("Exit Code: ").append(exitCode);

            return output.toString();

        } catch (IOException | InterruptedException e) {
            return "Exception occurred: " + e.getMessage();
        }
    }

    public Integer chatWay1(BotChatContext botChatContext, Bot bot, SseEmitter sseEmitter,
        ChatResponse[] chatResponseHolder) {

        if (chatResponseHolder[0].getContent().contains("runCode")) {
            chatResponseHolder[0].setContent("");
        }

        // 对话模版
        String chatTemplate = "请分析以下任务需求，制定一个清晰的解决方案思路。\n" + "任务需求：%s\n\n" + "请按照以下要求提供思路：\n" + "1. 理解任务的核心目标\n"
            + "2. 分析可能的技术实现路径\n" + "3. 只考虑一个解决方案\n" + "4. 给出简洁、可执行的步骤规划\n" + "\n请提供简洁但完整的思路，确保后续可以基于此生成有效的Python代码。";

        // 封装用户对话
        String chatContent = String.format(chatTemplate, chatResponseHolder[0].getContent());
        botChatContext.getChats().add(ChatContent.builder().content(chatContent).role("user").build());

        // 提交给ai分析，并保存到chats
        chatResponseHolder[0] = bot.sendChat(botChatContext, sseEmitter);
        botChatContext.getChats()
            .add(ChatContent.builder().content(chatResponseHolder[0].getContent()).role("assistant").build());
        return 2;
    }

    public Integer chatWay2(BotChatContext botChatContext, Bot bot, SseEmitter sseEmitter,
        ChatResponse[] chatResponseHolder) {

        // 对话模版
        String chatTemplate =
            "基于上一个解决方案思路，请生成一个完整、可执行且安全的Python代码：\n\n" + "代码要求：\n" + "1. 必须是完整可执行的Python代码\n" + "2. 确保代码的安全性，避免危险操作\n"
                + "3. 添加适当的注释说明关键步骤\n" + "4. 考虑跨平台兼容性（如可能）\n\n" + "请直接返回代码内容，不要包含任何markdown格式或额外的解释文字。";

        // 封装用户对话
        String chatContent = String.format(chatTemplate, chatResponseHolder[0].getContent());
        botChatContext.getChats().add(ChatContent.builder().content(chatContent).role("user").build());

        // 提交给ai分析，并保存到chats
        chatResponseHolder[0] = bot.sendChat(botChatContext, sseEmitter);
        botChatContext.getChats()
            .add(ChatContent.builder().content(chatResponseHolder[0].getContent()).role("assistant").build());
        return 3;
    }

    public Integer chatWay3(BotChatContext botChatContext, Bot bot, SseEmitter sseEmitter,
        ChatResponse[] chatResponseHolder) {

        // 对话模版
        String chatTemplate = "Python代码执行结果如下：\n" + "```\n%s\n```\n\n" + "请分析这个执行结果：\n" + "1. 代码是否成功执行？\n"
            + "2. 输出结果是否符合预期？\n" + "3. 是否存在错误或警告信息？\n\n" + "请提供简洁的分析报告，如问题诊断。";

        // 提交给计算引擎计算
        String scriptBack = executePythonCommand(chatResponseHolder[0].getContent());
        String chatContent = String.format(chatTemplate, scriptBack);

        // 添加新的提交
        botChatContext.getChats().add(ChatContent.builder().content(chatContent).role("user").build());

        // 提交给ai分析，并保存到chats
        chatResponseHolder[0] = bot.sendChat(botChatContext, sseEmitter);
        botChatContext.getChats()
            .add(ChatContent.builder().content(chatResponseHolder[0].getContent()).role("assistant").build());
        return 4;
    }

    public Integer chatWay4(BotChatContext botChatContext, Bot bot, SseEmitter sseEmitter,
        ChatResponse[] chatResponseHolder) {

        // 对话模版
        String chatTemplate = "基于前一个的完整对话历史（如执行结果分析内容），请评估当前状态并决定下一步行动。\n\n" + "请严格按照以下JSON格式返回决策结果，不要包含其他内容：\n" + "{\n"
            + "  \"runCode\": 数字,\n" + "  \"reason\": \"决策理由的简要说明\",\n" + "  \"suggestion\": \"具体的改进建议或下一步行动说明\"\n"
            + "}\n\n" + "runCode取值说明：\n" + "1 - 需求理解不清晰，需要重新分析需求\n" + "2 - 代码需要优化或重写\n"
            + "5 - 任务已完成，结果符合预期，或者需要人为干预\n\n" + "请基于实际情况做出最合理的决策。";

        // 添加新的提交
        botChatContext.getChats().add(ChatContent.builder().content(chatTemplate).role("user").build());

        // 提交给ai分析
        chatResponseHolder[0] = bot.sendChat(botChatContext, sseEmitter);

        // 返回ai的code
        RunCode runCode;
        try {
            runCode = JSON.parseObject(chatResponseHolder[0].getContent(), RunCode.class);
        } catch (Exception e) {
            botChatContext.getChats()
                .add(ChatContent.builder().content(chatResponseHolder[0].getContent()).role("assistant").build());
            return 4;
        }

        botChatContext.getChats()
            .add(ChatContent.builder().content(chatResponseHolder[0].getContent()).role("assistant").build());
        return runCode.getRunCode();
    }

    public Integer chatWay5(BotChatContext botChatContext, Bot bot, SseEmitter sseEmitter,
        ChatResponse[] chatResponseHolder) {

        // 对话模版
        String chatTemplate = "请将整个任务执行过程（从需求分析到最终结果）整理成一份完整的报告。\n\n" + "报告要求：\n" + "1. 使用清晰的Markdown格式\n"
            + "2. 附上Python代码输出的结果\n" + "3. 确保报告易于理解和阅读\n\n" + "请提供完整的总结报告。";

        // 添加新的提交
        botChatContext.getChats().add(ChatContent.builder().content(chatTemplate).role("user").build());

        // 提交给ai分析，并保存到chats
        chatResponseHolder[0] = bot.sendChat(botChatContext, sseEmitter);
        botChatContext.getChats()
            .add(ChatContent.builder().content(chatResponseHolder[0].getContent()).role("assistant").build());
        return 0;
    }
}
