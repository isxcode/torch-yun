-- 模型仓库表
create table if not exists TY_MODEL
(
    id                      varchar(200)  not null unique primary key comment '模型唯一id',
    name                    varchar(200)  not null comment '模型名称',
    code                    varchar(200)  not null comment '模型编码',
    model_type              varchar(200)  not null comment '模型类型',
    model_label             varchar(200)  null comment '模型标签',
    model_file              varchar(200)  null comment '模型文件',
    status                  varchar(200)  not null comment '模型状态',
    remark                  varchar(500) comment '模型描述',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 插入默认支持的数据
INSERT INTO TY_MODEL
(ID, NAME, CODE, MODEL_TYPE, MODEL_LABEL, STATUS, REMARK, CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY,
 LAST_MODIFIED_DATE_TIME, DELETED, VERSION_NUMBER, TENANT_ID)
VALUES ('qwen-plus', '通义千问-Plus', 'Qwen-Plus', 'API', '671b', 'ENABLE', '系统默认', 'zhishuyun',
        '2025-03-07 16:02:59.000000', 'zhishuyun', '2025-03-07 16:03:06.000000', 0, 1, 'zhishuyun');

-- 智能体表
create table if not exists TY_AI
(
    id                      varchar(200)  not null unique primary key comment 'ai唯一id',
    name                    varchar(200)  not null comment 'ai名称',
    remark                  varchar(500) comment 'ai描述',
    status                  varchar(200)  not null comment 'ai状态',
    check_date_time         datetime      not null comment 'ai检测时间',
    model_id                varchar(200)  not null comment '模型id',
    cluster_config          varchar(2000) null comment '集群配置',
    ai_log                  varchar(2000) null comment 'ai部署日志',
    ai_port                 varchar(50)   null comment 'ai端口',
    ai_pid                  varchar(50)   null comment 'ai进程',
    auth_config             varchar(2000) null comment 'ai认证配置',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 智能应用表
create table if not exists TY_APP
(
    id                      varchar(200)  not null unique primary key comment '应用唯一id',
    name                    varchar(200)  not null comment '应用名称',
    logo_id                 varchar(200)  not null comment 'logo_id',
    ai_id                   varchar(200)  not null comment 'ai_id',
    remark                  varchar(500) comment '应用描述',
    status                  varchar(200)  not null comment '应用状态',
    check_date_time         datetime      not null comment '智能体检测时间',
    prompt                  varchar(2000) null comment '提示词',
    base_config             varchar(2000) null comment '基础参数配置',
    resources               varchar(2000) null comment '知识库配置',
    default_app             varchar(200)  not null comment '是否为默认app',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 聊天表
create table if not exists TY_CHAT
(
    id                      varchar(200)  not null unique primary key comment '对话唯一id',
    app_id                  varchar(500) comment '应用id',
    submitter               varchar(200)  not null comment '提交人',
    chat_type               varchar(200)  not null comment '会话类型，测试聊天还是正式聊天',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 聊天对话表
create table if not exists TY_CHAT_SESSION
(
    id                      varchar(200)  not null unique primary key comment '回话唯一id',
    chat_id                 varchar(200)  not null comment '对话唯一id',
    app_id                  varchar(200)  not null comment '应用唯一id',
    session_index           int           not null comment '会话顺序',
    status                  varchar(200)  not null comment '会话状态',
    session_type            varchar(200)  not null comment '会话类型',
    session_content         longblob      not null comment '会话内容',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    deleted                 int default 0 not null comment '逻辑删除',
    version_number          int           not null comment '版本号',
    tenant_id               varchar(200)  not null comment '租户id'
)