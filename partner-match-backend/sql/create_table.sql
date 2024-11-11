create schema partner_match collate utf8mb4_general_ci;
ALTER DATABASE partner_match CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use partner_match;


-- 用户表
create table user
(
    id            bigint auto_increment comment 'id'
        primary key,
    username      varchar(256)                       null comment '用户昵称',
    user_account  varchar(256)                       null comment '账号',
    avatar_url    varchar(1024)                      null comment '用户头像',
    gender        tinyint                            null comment '性别',
    user_password varchar(512)                       not null comment '密码',
    contact_info  varchar(512)                       null comment '联系方式',
    email         varchar(512)                       null comment '邮箱',
    user_profile  varchar(512)                       null comment '个人简介',
    tag_names     varchar(1024)                      null comment '标签名称 json 列表',
    friend_ids    varchar(1024)                      null comment '好友 id 列表',
    user_status   int      default 0                 not null comment '状态 0 - 正常',
    user_role     tinyint  default 0                 not null comment '角色 0 - 普通用户 1 - 管理员',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                 not null comment '是否删除'
)
    comment '用户' charset = utf8mb4;


-- 标签表
create table tag
(
    id          bigint auto_increment comment 'id'
        primary key,
    tag_name    varchar(256)                       null comment '标签名称',
    user_id     bigint                             null comment '用户 id',
    parent_id   bigint                             null comment '父标签 id',
    is_parent   tinyint                            null comment '0 - 不是，1 - 父标签',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    constraint unique_index_tag_name
        unique (tag_name) comment '基于 tag_name 创建的唯一索引'
)
    comment '标签' charset = utf8mb4;

create index index_user_id
    on tag (user_id)
    comment '基于 user_id 创建的索引';


-- 队伍表
create table team
(
    id               bigint auto_increment comment 'id'
        primary key,
    team_name        varchar(256)                       not null comment '队伍名称',
    team_description varchar(1024)                      null comment '队伍描述',
    avatar_url       varchar(1024)                      null comment '队伍头像',
    max_num          int      default 1                 not null comment '最大人数',
    expire_time      datetime                           null comment '过期时间',
    user_id          bigint                             null comment '用户id（队长id）',
    team_status      int      default 0                 not null comment '队伍状态  0 - 公开，1 - 私有，2 - 加密',
    team_password    varchar(512)                       null comment '队伍密码',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete        tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍' charset = utf8mb4;


-- 用户队伍关系表
create table user_team
(
    id bigint auto_increment comment 'id'
        primary key,
    user_id bigint comment '用户id',
    team_id bigint comment '队伍id',
    join_time datetime null comment '加入时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete tinyint default 0 not null comment '是否删除'
)

    comment '用户队伍关系' charset = utf8mb4;


-- 聊天消息表
create table chat_message
(
    id              bigint auto_increment comment 'id'
        primary key,
    from_id         bigint                             not null comment '发送消息 id',
    to_id           bigint                             null comment '接收消息 id',
    message_content varchar(1024)                      not null comment '消息内容',
    message_type    tinyint                            not null comment '消息类型 0 - 世界聊天 1 - 队伍聊天 2 - 私聊',
    team_id         bigint                             null comment '队伍 id',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint  default 0                 not null comment '是否删除'
)
    comment '聊天消息' charset = utf8mb4;


-- 好友申请表
create table friendship
(
    id              bigint auto_increment comment '好友申请 id'
        primary key,
    from_id         bigint                             not null comment '发送申请的用户 id',
    to_id           bigint                             not null comment '接收申请的用户 id ',
    friendship_status  tinyint  default 0                 not null comment '申请状态 默认 0 （0-待处理 1-已同意 2-已拒绝）',
    friendship_message varchar(256)                       null comment '好友申请验证信息',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint  default 0                 not null comment '是否删除'
)
    comment '好友申请表' charset = utf8mb4;

