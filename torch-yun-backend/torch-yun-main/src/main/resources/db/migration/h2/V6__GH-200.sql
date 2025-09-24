-- 添加应用类型
alter table TY_APP
    add APP_TYPE varchar(100) default 'TEXT_APP';

comment on column TY_APP.APP_TYPE is '应用类型';

-- 添加一个实际提交的内容
alter table TY_CHAT_SESSION
    add SUB_SESSION_ID varchar(100);

comment on column TY_CHAT_SESSION.SUB_SESSION_ID is '子对话id';

-- 子聊天对话表
create table if not exists TY_CHAT_SUB_SESSION
(
    id                      varchar(200)  not null unique primary key comment '子对话id',
    session_index           int           not null comment '会话顺序',
    status                  varchar(200)  not null comment '会话状态',
    session_type            varchar(200)  not null comment '会话类型',
    session_chat            longblob      not null comment '对话内容',
    session_response        longblob      not null comment '回话内容',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    deleted                 int default 0 not null comment '逻辑删除',
    version_number          int           not null comment '版本号',
    tenant_id               varchar(200)  not null comment '租户id'
)