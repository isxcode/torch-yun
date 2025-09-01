package com.isxcode.torch.modules.app.bot.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.agent.constants.AgentUrl;
import com.isxcode.torch.api.agent.req.ChatAgentAiReq;
import com.isxcode.torch.api.agent.res.ChatAgentAiRes;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.torch.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.torch.api.model.constant.ModelCode;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.backend.api.base.pojos.BaseResponse;
import com.isxcode.torch.common.utils.aes.AesUtils;
import com.isxcode.torch.common.utils.http.HttpUrlUtils;
import com.isxcode.torch.common.utils.http.HttpUtils;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.app.bot.StreamBot;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import com.isxcode.torch.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.torch.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.torch.modules.cluster.repository.ClusterNodeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Random;

@Service
public class Qwen2_5 extends StreamBot {

    private final ChatSessionRepository chatSessionRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final HttpUrlUtils httpUrlUtils;

    public Qwen2_5(ChatSessionRepository chatSessionRepository, ClusterNodeRepository clusterNodeRepository,
        ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, HttpUrlUtils httpUrlUtils) {
        this.chatSessionRepository = chatSessionRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.httpUrlUtils = httpUrlUtils;
    }

    @Override
    public void chat(BotChatContext botChatContext) {

        // 随机一个集群id
        List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
            .findAllByClusterIdAndStatus(botChatContext.getClusterConfig().getClusterId(), ClusterNodeStatus.RUNNING);
        if (allEngineNodes.isEmpty()) {
            throw new IsxAppException("申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
        }
        ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

        // 翻译节点信息
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 重新封装对应的请求
        ChatAgentAiReq chatAgentAiReq = ChatAgentAiReq.builder().topK(botChatContext.getBaseConfig().getTopK())
            .topP(botChatContext.getBaseConfig().getTopP()).maxTokens(botChatContext.getBaseConfig().getMaxTokens())
            .temperature(botChatContext.getBaseConfig().getTemperature())
            .repetitionPenalty(botChatContext.getBaseConfig().getRepetitionPenalty()).prompt(botChatContext.getPrompt())
            .messages(botChatContext.getChats()).aiPort(botChatContext.getAiPort()).build();

        // 封装请求
        BaseResponse<?> baseResponse = HttpUtils.doPost(
            httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.CHAT_AI_URL),
            chatAgentAiReq, BaseResponse.class);
        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new IsxAppException(baseResponse.getMsg());
        }

        String content;
        if (baseResponse.getMsg().contains("Connection refused")) {
            content = "智能体已停用";
        } else {
            content = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), ChatAgentAiRes.class).getResponse();
        }

        // 提交当前会话
        ChatSessionEntity nowChatSession = chatSessionRepository
            .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();
        nowChatSession.setStatus(ChatSessionStatus.OVER);
        ChatContent build = ChatContent.builder().content(content).build();
        nowChatSession.setSessionContent(JSON.toJSONString(build));
        chatSessionRepository.save(nowChatSession);
    }

    @Override
    public void chatStream(BotChatContext botChatContext, SseEmitter sseEmitter) {

        // try {
        // // 随机一个集群id
        // List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
        // .findAllByClusterIdAndStatus(botChatContext.getClusterConfig().getClusterId(),
        // ClusterNodeStatus.RUNNING);
        // if (allEngineNodes.isEmpty()) {
        // throw new IsxAppException("申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
        // }
        // ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
        //
        // // 翻译节点信息
        // ScpFileEngineNodeDto scpFileEngineNodeDto =
        // clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        // scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));
        //
        // // 封装请求
        // ChatAgentAiReq chatAgentAiReq = new ChatAgentAiReq();
        // chatAgentAiReq.setMessages(botChatContext.getChats());
        // chatAgentAiReq.setAiPort(botChatContext.getAiPort());
        // chatAgentAiReq.setPrompt(botChatContext.getPrompt());
        // if (botChatContext.getBaseConfig() != null) {
        // chatAgentAiReq.setMaxTokens(botChatContext.getBaseConfig().getMaxTokens());
        // chatAgentAiReq.setTopK(botChatContext.getBaseConfig().getTopK());
        // chatAgentAiReq.setTopP(botChatContext.getBaseConfig().getTopP());
        // chatAgentAiReq.setTemperature(botChatContext.getBaseConfig().getTemperature());
        // chatAgentAiReq.setRepetitionPenalty(botChatContext.getBaseConfig().getRepetitionPenalty());
        // }
        //
        // // 获取当前会话实体
        // ChatSessionEntity nowChatSession = chatSessionRepository
        // .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();
        //
        // // 调用AI接口 - 这里需要实现流式调用
        // // 由于当前的 HttpUtils.doPost 不支持流式，我们先模拟流式响应
        // ChatAgentAiRes response = HttpUtils.doPost(
        // httpUrlUtils.genHttpUrl(scpFileEngineNodeDto.getHost(), botChatContext.getAiPort(),
        // AgentUrl.CHAT_AI_URL),
        // chatAgentAiReq, ChatAgentAiRes.class);
        //
        // if (!(HttpStatus.OK.value() == Integer.parseInt(response.getCode()))) {
        // throw new IsxAppException(response.getMsg());
        // }
        //
        // String content = response.getData().getResponse();
        //
        // // 模拟流式输出 - 将内容分块发送
        // StringBuilder currentContent = new StringBuilder();
        // String[] words = content.split(" ");
        //
        // for (int i = 0; i < words.length; i++) {
        // currentContent.append(words[i]);
        // if (i < words.length - 1) {
        // currentContent.append(" ");
        // }
        //
        // // 更新会话内容
        // ChatContent chatContent = ChatContent.builder().content(currentContent.toString()).build();
        // nowChatSession.setSessionContent(JSON.toJSONString(chatContent));
        // chatSessionRepository.save(nowChatSession);
        //
        // // 发送增量内容
        // try {
        // sseEmitter.send(SseEmitter.event()
        // .name("delta")
        // .data(words[i] + (i < words.length - 1 ? " " : "")));
        //
        // // 模拟延迟
        // Thread.sleep(100);
        // } catch (Exception e) {
        // throw new RuntimeException("发送SSE事件失败", e);
        // }
        // }
        //
        // // 标记会话结束
        // nowChatSession.setStatus(ChatSessionStatus.OVER);
        // chatSessionRepository.save(nowChatSession);
        //
        // // 发送完成事件
        // try {
        // sseEmitter.send(SseEmitter.event()
        // .name("complete")
        // .data(""));
        // sseEmitter.complete();
        // } catch (Exception e) {
        // throw new RuntimeException("发送完成事件失败", e);
        // }
        //
        // } catch (Exception e) {
        // try {
        // sseEmitter.send(SseEmitter.event()
        // .name("error")
        // .data(e.getMessage()));
        // sseEmitter.completeWithError(e);
        // } catch (Exception ignored) {
        // }
        // throw new IsxAppException("流式聊天失败: " + e.getMessage());
        // }
    }

    @Override
    public String name() {
        return ModelCode.QWEN2_5;
    }
}
