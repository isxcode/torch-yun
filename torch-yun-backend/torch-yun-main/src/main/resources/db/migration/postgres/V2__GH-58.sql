-- 模型仓库表
CREATE TABLE PY_MODEL
(
    id                      varchar(200)  not null,
    name                    varchar(200)  not null,
    code                    varchar(200)  not null,
    model_type              varchar(200)  not null,
    model_label             varchar(200)  null,
    model_file              varchar(200)  null,
    status                  varchar(200)  not null,
    remark                  varchar(500),
    create_by               varchar(200)  not null,
    create_date_time        timestamp     not null,
    last_modified_by        varchar(200)  not null,
    last_modified_date_time timestamp     not null,
    version_number          int           not null,
    deleted                 int default 0 not null,
    tenant_id               varchar(200)  not null,
    CONSTRAINT pk_py_model PRIMARY KEY (id)
);

-- 为列添加注释
COMMENT ON COLUMN PY_MODEL.id IS '模型唯一id';
COMMENT ON COLUMN PY_MODEL.name IS '模型名称';
COMMENT ON COLUMN PY_MODEL.code IS '模型编码';
COMMENT ON COLUMN PY_MODEL.model_type IS '模型类型';
COMMENT ON COLUMN PY_MODEL.model_label IS '模型标签';
COMMENT ON COLUMN PY_MODEL.model_file IS '模型文件';
COMMENT ON COLUMN PY_MODEL.status IS '模型状态';
COMMENT ON COLUMN PY_MODEL.remark IS '模型描述';
COMMENT ON COLUMN PY_MODEL.create_by IS '创建人';
COMMENT ON COLUMN PY_MODEL.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_MODEL.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_MODEL.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_MODEL.version_number IS '版本号';
COMMENT ON COLUMN PY_MODEL.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_MODEL.tenant_id IS '租户id';

-- 智能体表
CREATE TABLE PY_AI
(
    id                      varchar(200)  not null,
    name                    varchar(200)  not null,
    remark                  varchar(500),
    status                  varchar(200)  not null,
    check_date_time         timestamp     not null,
    model_id                varchar(200)  not null,
    cluster_config          varchar(2000) null,
    ai_log                  varchar(2000) null,
    ai_port                 varchar(50)   null,
    ai_pid                  varchar(50)   null,
    auth_config             varchar(2000) null,
    create_by               varchar(200)  not null,
    create_date_time        timestamp     not null,
    last_modified_by        varchar(200)  not null,
    last_modified_date_time timestamp     not null,
    version_number          int           not null,
    deleted                 int default 0 not null,
    tenant_id               varchar(200)  not null,
    CONSTRAINT pk_py_ai PRIMARY KEY (id)
);

-- 为列添加注释
COMMENT ON COLUMN PY_AI.id IS 'ai唯一id';
COMMENT ON COLUMN PY_AI.name IS 'ai名称';
COMMENT ON COLUMN PY_AI.remark IS 'ai描述';
COMMENT ON COLUMN PY_AI.status IS 'ai状态';
COMMENT ON COLUMN PY_AI.check_date_time IS 'ai检测时间';
COMMENT ON COLUMN PY_AI.model_id IS '模型id';
COMMENT ON COLUMN PY_AI.cluster_config IS '集群配置';
COMMENT ON COLUMN PY_AI.ai_log IS 'ai部署日志';
COMMENT ON COLUMN PY_AI.ai_port IS 'ai端口';
COMMENT ON COLUMN PY_AI.ai_pid IS 'ai进程';
COMMENT ON COLUMN PY_AI.auth_config IS 'ai认证配置';
COMMENT ON COLUMN PY_AI.create_by IS '创建人';
COMMENT ON COLUMN PY_AI.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_AI.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_AI.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_AI.version_number IS '版本号';
COMMENT ON COLUMN PY_AI.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_AI.tenant_id IS '租户id';

-- 智能应用表
CREATE TABLE PY_APP
(
    id                      varchar(200)  not null,
    name                    varchar(200)  not null,
    logo_id                 varchar(200)  not null,
    ai_id                   varchar(200)  not null,
    remark                  varchar(500),
    status                  varchar(200)  not null,
    check_date_time         timestamp     not null,
    prompt                  varchar(2000) null,
    base_config             varchar(2000) null,
    resources               varchar(2000) null,
    default_app             varchar(200)  not null,
    create_by               varchar(200)  not null,
    create_date_time        timestamp     not null,
    last_modified_by        varchar(200)  not null,
    last_modified_date_time timestamp     not null,
    version_number          int           not null,
    deleted                 int default 0 not null,
    tenant_id               varchar(200)  not null,
    CONSTRAINT pk_py_app PRIMARY KEY (id)
);

-- 为列添加注释
COMMENT ON COLUMN PY_APP.id IS '应用唯一id';
COMMENT ON COLUMN PY_APP.name IS '应用名称';
COMMENT ON COLUMN PY_APP.logo_id IS 'logo_id';
COMMENT ON COLUMN PY_APP.logo_id IS 'ai_id';
COMMENT ON COLUMN PY_APP.remark IS '应用描述';
COMMENT ON COLUMN PY_APP.status IS '应用状态';
COMMENT ON COLUMN PY_APP.check_date_time IS '智能体检测时间';
COMMENT ON COLUMN PY_APP.prompt IS '提示词';
COMMENT ON COLUMN PY_APP.base_config IS '基础参数配置';
COMMENT ON COLUMN PY_APP.resources IS '知识库配置';
COMMENT ON COLUMN PY_APP.default_app IS '是否为默认app';
COMMENT ON COLUMN PY_APP.create_by IS '创建人';
COMMENT ON COLUMN PY_APP.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_APP.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_APP.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_APP.version_number IS '版本号';
COMMENT ON COLUMN PY_APP.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_APP.tenant_id IS '租户id';

-- 聊天表
CREATE TABLE PY_CHAT
(
    id                      varchar(200)  not null,
    app_id                  varchar(500),
    submitter               varchar(200)  not null,
    chat_type               varchar(200)  not null,
    create_by               varchar(200)  not null,
    create_date_time        timestamp     not null,
    last_modified_by        varchar(200)  not null,
    last_modified_date_time timestamp     not null,
    version_number          int           not null,
    deleted                 int default 0 not null,
    tenant_id               varchar(200)  not null,
    CONSTRAINT pk_py_chat PRIMARY KEY (id)
);

-- 为列添加注释
COMMENT ON COLUMN PY_CHAT.id IS '对话唯一id';
COMMENT ON COLUMN PY_CHAT.app_id IS '应用id';
COMMENT ON COLUMN PY_CHAT.submitter IS '提交人';
COMMENT ON COLUMN PY_CHAT.chat_type IS '会话类型，测试聊天还是正式聊天';
COMMENT ON COLUMN PY_CHAT.create_by IS '创建人';
COMMENT ON COLUMN PY_CHAT.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_CHAT.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_CHAT.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_CHAT.version_number IS '版本号';
COMMENT ON COLUMN PY_CHAT.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_CHAT.tenant_id IS '租户id';

-- 聊天对话表
CREATE TABLE PY_CHAT_SESSION
(
    id                      varchar(200)  not null,
    chat_id                 varchar(200)  not null,
    app_id                  varchar(200)  not null,
    session_index           int           not null,
    status                  varchar(200)  not null,
    session_type            varchar(200)  not null,
    session_content         bytea         not null,
    create_by               varchar(200)  not null,
    create_date_time        timestamp     not null,
    last_modified_by        varchar(200)  not null,
    last_modified_date_time timestamp     not null,
    deleted                 int default 0 not null,
    version_number          int           not null,
    tenant_id               varchar(200)  not null,
    CONSTRAINT pk_py_chat_session PRIMARY KEY (id)
);

-- 为列添加注释
COMMENT ON COLUMN PY_CHAT_SESSION.id IS '回话唯一id';
COMMENT ON COLUMN PY_CHAT_SESSION.chat_id IS '对话唯一id';
COMMENT ON COLUMN PY_CHAT_SESSION.app_id IS '应用唯一id';
COMMENT ON COLUMN PY_CHAT_SESSION.session_index IS '会话顺序';
COMMENT ON COLUMN PY_CHAT_SESSION.status IS '会话状态';
COMMENT ON COLUMN PY_CHAT_SESSION.session_type IS '会话类型';
COMMENT ON COLUMN PY_CHAT_SESSION.session_content IS '会话内容';
COMMENT ON COLUMN PY_CHAT_SESSION.create_by IS '创建人';
COMMENT ON COLUMN PY_CHAT_SESSION.create_date_time IS '创建时间';
COMMENT ON COLUMN PY_CHAT_SESSION.last_modified_by IS '更新人';
COMMENT ON COLUMN PY_CHAT_SESSION.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN PY_CHAT_SESSION.version_number IS '版本号';
COMMENT ON COLUMN PY_CHAT_SESSION.deleted IS '逻辑删除';
COMMENT ON COLUMN PY_CHAT_SESSION.tenant_id IS '租户id';

-- 插入默认支持的数据
INSERT INTO PY_MODEL
(ID, NAME, CODE, MODEL_TYPE, MODEL_LABEL, STATUS, REMARK, CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY,
 LAST_MODIFIED_DATE_TIME, DELETED, VERSION_NUMBER, TENANT_ID)
VALUES ('qwen-plus', '通义千问-Plus', 'Qwen-Plus', 'API', '671b', 'ENABLE', '系统默认', 'zhishuyun',
        '2025-03-07 16:02:59.000000', 'zhishuyun', '2025-03-07 16:03:06.000000', 0, 1, 'zhishuyun');