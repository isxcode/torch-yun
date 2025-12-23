-- 模型广场
CREATE TABLE IF NOT EXISTS TY_MODEL_PLAZA
(
    id                      VARCHAR(200)  NOT NULL,
    org_name                VARCHAR(200)  NOT NULL,
    model_name              VARCHAR(200)  NOT NULL,
    is_online               VARCHAR(200)  NOT NULL,
    label                   VARCHAR(200)  NOT NULL,
    model_type              VARCHAR(500)  NOT NULL,
    model_param             VARCHAR(200),
    remark                  VARCHAR(200),
    create_by               VARCHAR(200)  NOT NULL,
    create_date_time        TIMESTAMP     NOT NULL,
    last_modified_by        VARCHAR(200)  NOT NULL,
    last_modified_date_time TIMESTAMP     NOT NULL,
    deleted                 INT DEFAULT 0 NOT NULL,
    version_number          INT           NOT NULL,
    CONSTRAINT ty_model_plaza_pk PRIMARY KEY (id),
    CONSTRAINT ty_model_plaza_id_unique UNIQUE (id)
);

COMMENT ON TABLE TY_MODEL_PLAZA IS '模型广场';
COMMENT ON COLUMN TY_MODEL_PLAZA.id IS '模型id';
COMMENT ON COLUMN TY_MODEL_PLAZA.org_name IS '组织名称';
COMMENT ON COLUMN TY_MODEL_PLAZA.model_name IS '模型名称';
COMMENT ON COLUMN TY_MODEL_PLAZA.is_online IS '在线模型 ENABLE,离线模型 DISABLE';
COMMENT ON COLUMN TY_MODEL_PLAZA.label IS '类别多模态等';
COMMENT ON COLUMN TY_MODEL_PLAZA.model_type IS '语音/文本';
COMMENT ON COLUMN TY_MODEL_PLAZA.model_param IS '模型参数';
COMMENT ON COLUMN TY_MODEL_PLAZA.remark IS '模型描述';
COMMENT ON COLUMN TY_MODEL_PLAZA.create_by IS '创建人';
COMMENT ON COLUMN TY_MODEL_PLAZA.create_date_time IS '创建时间';
COMMENT ON COLUMN TY_MODEL_PLAZA.last_modified_by IS '更新人';
COMMENT ON COLUMN TY_MODEL_PLAZA.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN TY_MODEL_PLAZA.deleted IS '逻辑删除';
COMMENT ON COLUMN TY_MODEL_PLAZA.version_number IS '版本号';

INSERT INTO TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                            CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                            VERSION_NUMBER)
VALUES ('Aliyun_qwen3-max', 'Aliyun', 'qwen3-max', 'ENABLE', 'Natural Language Processing', 'Text Generation', '235B',
        '通义千问3系列Max模型，相较preview版本在智能体编程与工具调用方向进行了专项升级。本次发布的正式版模型达到领域SOTA水平，适配场景更加复杂的智能体需求。',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);

INSERT INTO TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                            CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                            VERSION_NUMBER)
VALUES ('Aliyun_deepseek-v3.2', 'Aliyun', 'deepseek-v3.2', 'ENABLE', 'Natural Language Processing', 'Text Generation',
        '671B',
        'DeepSeek-V3.2是引入DeepSeek Sparse Attention（一种稀疏注意力机制）的正式版模型，也是DeepSeek推出的首个将思考融入工具使用的模型，同时支持思考模式与非思考模式的工具调用。',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);

INSERT INTO TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                            CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                            VERSION_NUMBER)
VALUES ('Deepseek_deepseek-chat', 'Deepseek', 'deepseek-chat', 'ENABLE', 'Natural Language Processing',
        'Text Generation', '671B',
        'DeepSeek-V3.2正式版，平衡推理能力与输出长度，适合日常使用，例如问答场景和通用 Agent 任务场景。',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);

INSERT INTO TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                            CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                            VERSION_NUMBER)
VALUES ('Volcengine_DeepSeek-V3.2', 'Volcengine', 'DeepSeek-V3.2', 'ENABLE', 'Natural Language Processing',
        'Text Generation', '235B',
        'DeepSeek-V3.2正式版，平衡推理能力与输出长度，适合日常使用，例如问答场景和通用 Agent 任务场景。',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);

INSERT INTO TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                            CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                            VERSION_NUMBER)
VALUES ('qwen_Qwen2.5-0.5B', 'Qwen', 'Qwen2.5-0.5B', 'DISABLE', 'Natural Language Processing', 'Text Generation',
        '0.5B',
        'Qwen2.5 is the latest series of Qwen large language models. For Qwen2.5, we release a number of base language models and instruction-tuned language models ranging from 0.5 to 72 billion parameters. ',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);

INSERT INTO TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                            CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                            VERSION_NUMBER)
VALUES ('google_gemma-3-270m', 'Google', 'gemma-3-270m', 'DISABLE', 'Natural Language Processing', 'Text Generation',
        '270M',
        'Gemma is a family of lightweight, state-of-the-art open models from Google, built from the same research and technology used to create the Gemini models. ',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);

-- 清空模型仓库
DELETE
FROM TY_MODEL
WHERE ID = 'Qwen2.5-0.5B';
DELETE
FROM TY_MODEL
WHERE ID = 'doubao';
DELETE
FROM TY_MODEL
WHERE ID = 'deepseek';
DELETE
FROM TY_MODEL
WHERE ID = 'qwen-plus';
DELETE
FROM TY_MODEL
WHERE ID = 'qwen';

-- 修改模型仓库表 - 重命名列
ALTER TABLE TY_MODEL
    RENAME COLUMN CODE TO MODEL_PLAZA_ID;

COMMENT ON COLUMN TY_MODEL.MODEL_PLAZA_ID IS '模型广场的模型id';

-- 删除类型字段
ALTER TABLE TY_MODEL
    DROP COLUMN IF EXISTS MODEL_TYPE;

-- 删除标签字段
ALTER TABLE TY_MODEL
    DROP COLUMN IF EXISTS MODEL_LABEL;

-- 添加python启动脚本
ALTER TABLE TY_MODEL
    ADD COLUMN DEPLOY_SCRIPT TEXT NOT NULL DEFAULT '';

COMMENT ON COLUMN TY_MODEL.DEPLOY_SCRIPT IS '部署脚本';

-- 添加智能体类型
ALTER TABLE TY_AI
    ADD COLUMN AI_TYPE VARCHAR(200) NOT NULL DEFAULT '';

COMMENT ON COLUMN TY_AI.AI_TYPE IS 'ai类型远程还是本地的';

-- 添加默认资源文件
INSERT INTO TY_FILE (ID, FILE_NAME, FILE_SIZE, FILE_TYPE, CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY,
                     LAST_MODIFIED_DATE_TIME, VERSION_NUMBER, DELETED, TENANT_ID, REMARK)
VALUES ('Qwen2.5-0.5B.zip', 'Qwen2.5-0.5B.zip', '751.17 MB', 'MODEL_FILE', 'zhishuyun', '2025-12-19 14:35:17.447000',
        'zhishuyun', '2025-12-19 14:35:17.447000', 0, 0, 'zhishuyun', '系统默认模型文件');

-- 添加默认模型
INSERT INTO TY_MODEL (ID, NAME, MODEL_PLAZA_ID, MODEL_FILE, STATUS, REMARK, CREATE_BY, CREATE_DATE_TIME,
                      LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, VERSION_NUMBER, DELETED, TENANT_ID,
                      DEPLOY_SCRIPT)
VALUES ('Qwen2.5-0.5B', 'Qwen2.5-0.5B', 'qwen_Qwen2.5-0.5B', 'Qwen2.5-0.5B.zip', 'ENABLE', '系统默认模型', 'zhishuyun',
        '2025-12-19 15:36:55.516000', 'zhishuyun', '2025-12-19 15:36:55.516000', 0, 0, 'zhishuyun', '');

-- 修改字段
ALTER TABLE TY_CHAT_SESSION
    ALTER COLUMN session_content TYPE TEXT;