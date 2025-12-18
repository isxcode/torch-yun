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
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);
INSERT INTO PUBLIC.TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                                   CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                                   VERSION_NUMBER)
VALUES ('Aliyun_deepseek-v3.2', 'Aliyun', 'deepseek-v3.2', 'ENABLE', 'Natural Language Processing', 'Text Generation',
        '671B',
        'DeepSeek-V3.2是引入DeepSeek Sparse Attention（一种稀疏注意力机制）的正式版模型，也是DeepSeek推出的首个将思考融入工具使用的模型，同时支持思考模式与非思考模式的工具调用。',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);
INSERT INTO PUBLIC.TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                                   CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                                   VERSION_NUMBER)
VALUES ('Deepseek_deepseek-chat', 'Deepseek', 'deepseek-chat', 'ENABLE', 'Natural Language Processing',
        'Text Generation', '671B',
        'DeepSeek-V3.2正式版，平衡推理能力与输出长度，适合日常使用，例如问答场景和通用 Agent 任务场景。',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);
INSERT INTO PUBLIC.TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                                   CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                                   VERSION_NUMBER)
VALUES ('Volcengine_DeepSeek-V3.2', 'Volcengine', 'DeepSeek-V3.2', 'ENABLE', 'Natural Language Processing',
        'Text Generation', '235B',
        'DeepSeek-V3.2正式版，平衡推理能力与输出长度，适合日常使用，例如问答场景和通用 Agent 任务场景。',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);
INSERT INTO PUBLIC.TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                                   CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                                   VERSION_NUMBER)
VALUES ('qwen_Qwen2.5-0.5B', 'Qwen', 'Qwen2.5-0.5B', 'DISABLE', 'Natural Language Processing', 'Text Generation',
        '0.5B',
        'Qwen2.5 is the latest series of Qwen large language models. For Qwen2.5, we release a number of base language models and instruction-tuned language models ranging from 0.5 to 72 billion parameters. ',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);
INSERT INTO PUBLIC.TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                                   CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                                   VERSION_NUMBER)
VALUES ('google_gemma-3-270m', 'Google', 'gemma-3-270m', 'DISABLE', 'Natural Language Processing', 'Text Generation',
        '270M',
        'Gemma is a family of lightweight, state-of-the-art open models from Google, built from the same research and technology used to create the Gemini models. ',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);
INSERT INTO PUBLIC.TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                                   CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                                   VERSION_NUMBER)
VALUES ('openai_circuit-sparsity', 'Openai', 'circuit-sparsity', 'DISABLE', 'Natural Language Processing',
        'Text Generation', '0.4B',
        'Weights for a sparse model from Gao et al. 2025, used for the qualitative results from the paper.',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);

-- 清空模型仓库
DELETE
FROM PUBLIC.TY_MODEL
WHERE ID = 'Qwen2.5-0.5B';

DELETE
FROM PUBLIC.TY_MODEL
WHERE ID = 'doubao';

DELETE
FROM PUBLIC.TY_MODEL
WHERE ID = 'deepseek';

DELETE
FROM PUBLIC.TY_MODEL
WHERE ID = 'qwen-plus';

DELETE
FROM PUBLIC.TY_MODEL
WHERE ID = 'qwen';

-- 修改模型仓库表
alter table TY_MODEL
    alter column CODE rename to MODEL_PLAZA_ID;

comment on column TY_MODEL.MODEL_PLAZA_ID is '模型广场的模型id';

-- 删除类型字段
alter table TY_MODEL
    drop column MODEL_TYPE;

-- 删除标签字段
alter table TY_MODEL
    drop column MODEL_LABEL;

-- 添加python启动脚本
alter table TY_MODEL
    add DEPLOY_SCRIPT text not null default '';

comment on column TY_MODEL.DEPLOY_SCRIPT is '部署脚本';




