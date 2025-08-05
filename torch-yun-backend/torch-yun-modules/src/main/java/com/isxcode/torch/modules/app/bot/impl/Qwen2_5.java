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
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import com.isxcode.torch.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.torch.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.torch.modules.cluster.repository.ClusterNodeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class Qwen2_5 extends Bot {

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
        ChatAgentAiReq chatAgentAiReq = ChatAgentAiReq.builder()
            .prompt(botChatContext.getChats().get(botChatContext.getChats().size() - 1).getContent())
            .aiPort(botChatContext.getAiPort()).build();

        // 封装请求
        BaseResponse<?> baseResponse = HttpUtils.doPost(
            httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.CHAT_AI_URL),
            chatAgentAiReq, BaseResponse.class);
        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new IsxAppException(baseResponse.getMsg());
        }

        // 修改智能体状态
        ChatAgentAiRes chatAgentAiRes =
            JSON.parseObject(JSON.toJSONString(baseResponse.getData()), ChatAgentAiRes.class);

        // 提交当前会话
        ChatSessionEntity nowChatSession = chatSessionRepository
            .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();
        nowChatSession.setStatus(ChatSessionStatus.OVER);
        ChatContent build = ChatContent.builder().content(chatAgentAiRes.getResponse()).build();
        nowChatSession.setSessionContent(JSON.toJSONString(build));
        chatSessionRepository.save(nowChatSession);
    }

    @Override
    public String name() {
        return ModelCode.QWEN2_5;
    }
}
