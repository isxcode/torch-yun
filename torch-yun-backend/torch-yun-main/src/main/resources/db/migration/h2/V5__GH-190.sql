-- 部署ai的日志字段太小了
alter table TY_AI
    alter column AI_LOG text;