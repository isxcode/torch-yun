-- 添加应用类型
alter table TY_APP
    add APP_TYPE varchar(100) default 'TEXT_APP';

comment on column TY_APP.APP_TYPE is '应用类型';

