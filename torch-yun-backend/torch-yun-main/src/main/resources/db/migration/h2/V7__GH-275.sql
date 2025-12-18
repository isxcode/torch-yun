-- 模型广场
create table if not exists TY_MODEL_PLAZA
(
    id                      varchar(200)  not null unique primary key comment '模型id',
    org_name                varchar(200)  not null comment '组织名称',
    model_name              varchar(200)  not null comment '模型名称',
    is_online               varchar(200)  not null comment '在线模型 ENABLE,离线模型 DISABLE',
    label                   varchar(200)  not null comment '类别多模态等',
    model_type              varchar(500)  not null comment '语音/文本',
    model_param             varchar(200)  null comment '模型参数',
    remark                  varchar(200)  null comment '模型描述',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    deleted                 int default 0 not null comment '逻辑删除',
    version_number          int           not null comment '版本号'
);

INSERT INTO PUBLIC.TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                                   CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                                   VERSION_NUMBER)
VALUES ('Aliyun_qwen3-max', 'Aliyun', 'qwen3-max', 'ENABLE', 'Natural Language Processing', 'Text Generation', '235B',
        '通义千问3系列Max模型，相较preview版本在智能体编程与工具调用方向进行了专项升级。本次发布的正式版模型达到领域SOTA水平，适配场景更加复杂的智能体需求。',
        'admin', '2025-12-18 16:17:19.000000', 'admin', '2025-12-18 16:17:23.000000', 0, 0);