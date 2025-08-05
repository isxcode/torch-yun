package com.isxcode.torch.modules.alarm.message;

public interface MessageAction {

    String getActionName();

    Object sendMessage(MessageContext messageContext);
}
