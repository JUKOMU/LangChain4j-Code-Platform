# 数据库初始化
-- 建库
create database if not exists ai_code_platform;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    user_account  varchar(256)                           not null comment '账号',
    user_password varchar(512)                           not null comment '密码',
    user_name     varchar(256)                           null comment '用户昵称',
    user_avatar   varchar(1024)                          null comment '用户头像',
    user_profile  varchar(512)                           null comment '用户简介',
    user_role     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    edit_time     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint      default 0                 not null comment '是否删除',
    vip_expire_time datetime     null comment '会员过期时间',
    vip_code       varchar(128) null comment '会员兑换码',
    vip_number     bigint       null comment '会员编号',
    share_code     varchar(20)  DEFAULT NULL COMMENT '分享码',
    invite_user    bigint       DEFAULT NULL COMMENT '邀请用户 id',
    UNIQUE KEY uk_userAccount (user_account),
    INDEX idx_userName (user_name)
    ) comment '用户' collate = utf8mb4_unicode_ci;

