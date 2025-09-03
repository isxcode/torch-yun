package com.isxcode.torch.api.agent.constants;

import com.isxcode.torch.api.main.constants.ModuleCode;

/**
 * 代理接口访问地址.
 */
public interface AgentUrl {

    String HEART_CHECK_URL = "/" + ModuleCode.torch_YUN_AGENT + "/heartCheck";

    String DEPLOY_AI_URL = "/" + ModuleCode.torch_YUN_AGENT + "/deployAi";

    String STOP_AI_URL = "/" + ModuleCode.torch_YUN_AGENT + "/stopAi";

    String GET_AI_LOG_URL = "/" + ModuleCode.torch_YUN_AGENT + "/getAiLog";

    String CHAT_AI_URL = "/" + ModuleCode.torch_YUN_AGENT + "/chatAi";

    String CHECK_AI_URL = "/" + ModuleCode.torch_YUN_AGENT + "/checkAi";

    String DELETE_AI_URL = "/" + ModuleCode.torch_YUN_AGENT + "/deleteAi";
}
