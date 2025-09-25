-- 添加应用类型
ALTER TABLE TY_APP
ADD COLUMN APP_TYPE VARCHAR(100) DEFAULT 'TEXT_APP' COMMENT '应用类型';

-- 子聊天对话表
CREATE TABLE IF NOT EXISTS TY_CHAT_SUB_SESSION (
    id VARCHAR(200) NOT NULL UNIQUE PRIMARY KEY COMMENT '子对话id',
    session_index INT NOT NULL COMMENT '会话顺序',
    status VARCHAR(200) NOT NULL COMMENT '会话状态',
    session_type VARCHAR(200) NOT NULL COMMENT '会话类型',
    session_id VARCHAR(200) NOT NULL COMMENT '父级会话id',
    session_content LONGBLOB NOT NULL COMMENT '对话内容',
    session_role VARCHAR(200) NOT NULL COMMENT '对话角色',
    create_by VARCHAR(200) NOT NULL COMMENT '创建人',
    create_date_time DATETIME NOT NULL COMMENT '创建时间',
    last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
    deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    version_number INT NOT NULL COMMENT '版本号',
    tenant_id VARCHAR(200) NOT NULL COMMENT '租户id'
) COMMENT='子聊天对话表';