-- 部署ai的日志字段太小了
ALTER TABLE TY_AI
    ALTER
        COLUMN AI_LOG TYPE TEXT;