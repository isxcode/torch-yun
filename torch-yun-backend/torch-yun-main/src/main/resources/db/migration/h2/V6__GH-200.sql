-- 添加应用类型
alter table TY_APP
    add APP_TYPE varchar(100) default 'TEXT_APP';

comment on column TY_APP.APP_TYPE is '应用类型';

-- 添加一个实际提交的内容
alter table TY_CHAT_SESSION
    add SUBMIT_CONTENT text;

comment on column TY_CHAT_SESSION.SUBMIT_CONTENT is '提交的对话内容';

