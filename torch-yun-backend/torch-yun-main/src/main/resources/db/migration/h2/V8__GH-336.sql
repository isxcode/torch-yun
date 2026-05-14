-- 添加默认codex cli模型
INSERT INTO PUBLIC.TY_MODEL_PLAZA (ID, ORG_NAME, MODEL_NAME, IS_ONLINE, LABEL, MODEL_TYPE, MODEL_PARAM, REMARK,
                                   CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME, DELETED,
                                   VERSION_NUMBER)
VALUES ('Codex-cli', 'OpenAi', 'codex-cli', 'ENABLE', 'Natural Language Processing', 'Text Generation', '235B',
        'Codex命令行',
        'zhishuyun', '2025-12-18 16:17:19.000000', 'zhishuyun', '2025-12-18 16:17:23.000000', 0, 0);

-- 添加默认codex cli 智能体
INSERT INTO PUBLIC.TY_AI (ID, NAME, REMARK, STATUS, CHECK_DATE_TIME, MODEL_ID, CLUSTER_CONFIG, AI_LOG, AI_PORT, AI_PID,
                          AUTH_CONFIG, CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME,
                          VERSION_NUMBER, DELETED, TENANT_ID, AI_TYPE)
VALUES ('Codex-cli', 'Codex-cli', '系统默认', 'ENABLE', '2026-05-13 15:39:16.000000', 'Codex-cli', '', null, null, null,
        null, 'zhishuyun', '2026-05-13 15:39:26.000000', 'zhishuyun', '2026-05-13 15:39:32.000000', 0, 0, 'zhishuyun',
        'API');

-- 添加默认codex cli默认应用
INSERT INTO PUBLIC.TY_APP (ID, NAME, LOGO_ID, AI_ID, REMARK, STATUS, CHECK_DATE_TIME, PROMPT, BASE_CONFIG, RESOURCES,
                           DEFAULT_APP, CREATE_BY, CREATE_DATE_TIME, LAST_MODIFIED_BY, LAST_MODIFIED_DATE_TIME,
                           VERSION_NUMBER, DELETED, TENANT_ID, APP_TYPE)
VALUES ('codex-cli', 'codex-cli', 'log_id', 'Codex-cli', '系统默认', 'ENABLE', '2026-05-13 15:35:31.000000', null, null,
        null, 'ENABLE',
        'zhishuyun', '2026-05-13 15:35:41.000000', 'zhishuyun', '2026-05-13 15:35:44.000000', 0, 0, 'zhishuyun',
        'TEXT_APP');

CREATE TABLE IF NOT EXISTS TY_PROJECT
(
    ID                      VARCHAR(200)  NOT NULL PRIMARY KEY,
    NAME                    VARCHAR(200)  NOT NULL,
    WORKSPACE               VARCHAR(500)  NOT NULL,
    ASSETS_DIR              VARCHAR(500)  NOT NULL,
    DESIGN_APP_ID           VARCHAR(200)  NOT NULL,
    PLAN_APP_ID             VARCHAR(200)  NOT NULL,
    DEVELOP_APP_ID          VARCHAR(200)  NOT NULL,
    REMARK                  VARCHAR(200),
    CREATE_BY               VARCHAR(200)  NOT NULL,
    CREATE_DATE_TIME        TIMESTAMP     NOT NULL,
    LAST_MODIFIED_BY        VARCHAR(200)  NOT NULL,
    LAST_MODIFIED_DATE_TIME TIMESTAMP     NOT NULL,
    DELETED                 INT DEFAULT 0 NOT NULL,
    VERSION_NUMBER          BIGINT        NOT NULL,
    TENANT_ID               VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS TY_PROJECT_DESIGN
(
    ID                      VARCHAR(200)  NOT NULL PRIMARY KEY,
    NAME                    VARCHAR(200)  NOT NULL,
    REMARK                  VARCHAR(200),
    CREATE_BY               VARCHAR(200)  NOT NULL,
    CREATE_DATE_TIME        TIMESTAMP     NOT NULL,
    LAST_MODIFIED_BY        VARCHAR(200)  NOT NULL,
    LAST_MODIFIED_DATE_TIME TIMESTAMP     NOT NULL,
    DELETED                 INT DEFAULT 0 NOT NULL,
    VERSION_NUMBER          BIGINT        NOT NULL,
    TENANT_ID               VARCHAR(200)
);

ALTER TABLE TY_PROJECT_DESIGN ADD COLUMN IF NOT EXISTS PROJECT_ID VARCHAR(200);
