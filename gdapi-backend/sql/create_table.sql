
use gdapi;
-- 接口信息表
create table if not exists gdapi.`interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '接口名',
    `description` varchar(256) null comment '描述',
    `url` varchar(512) not null comment '接口地址',
    `requestParams` text not null comment '请求参数',
    `requestHeader` text null comment '请求头',
    `responseHeader` text null comment '响应头',
    `status` int default 0 not null comment '接口状态(0关闭,1开启)',
    `method` varchar(256) not null comment '请求类型',
    `userId` bigint not null comment '创建人',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDeleted` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '接口信息表';

insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('郑明轩', '阎健柏', 'www.winter-brown.co', '邹彬', '万烨华', 0, '覃弘文', 2);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('江俊驰', '蒋胤祥', 'www.louvenia-schroeder.co', '叶烨磊', '郭博超', 0, '邵聪健', 8653092175);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('贺文', '方耀杰', 'www.lynn-steuber.info', '邵耀杰', '孙晓博', 0, '高煜祺', 89);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('侯志强', '罗俊驰', 'www.domingo-auer.name', '赵越彬', '段健柏', 0, '熊晓博', 40);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('刘胤祥', '贺荣轩', 'www.gerald-bernier.net', '洪峻熙', '戴弘文', 0, '顾晟睿', 1827);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('冯鑫鹏', '严炎彬', 'www.jody-lubowitz.com', '薛思聪', '熊子轩', 0, '冯峻熙', 4);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('谢志强', '赖钰轩', 'www.karoline-lang.org', '王天宇', '王正豪', 0, '夏果', 4);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('熊苑博', '毛雨泽', 'www.etha-hodkiewicz.biz', '龙弘文', '吕凯瑞', 0, '钱熠彤', 0);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('邹浩', '段旭尧', 'www.darwin-tillman.org', '邹晓啸', '薛鸿涛', 0, '杜嘉懿', 15742);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('杜健雄', '宋琪', 'www.granville-witting.biz', '方振家', '田鹏', 0, '姚文轩', 61);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('郭正豪', '梁绍齐', 'www.moriah-berge.name', '谢文轩', '叶天磊', 0, '孙展鹏', 173);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('林建辉', '秦致远', 'www.sallie-hintz.com', '秦旭尧', '任越彬', 0, '侯胤祥', 2607577190);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('段弘文', '苏烨霖', 'www.stacey-kuphal.com', '邓雪松', '阎峻熙', 0, '严博文', 3);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('马建辉', '任懿轩', 'www.allie-lynch.info', '陈瑞霖', '袁子默', 0, '万金鑫', 8);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('龙越彬', '郝哲瀚', 'www.racheal-langosh.net', '廖远航', '孔瑞霖', 0, '许健雄', 9235067542);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('高致远', '熊峻熙', 'www.tom-gislason.com', '石靖琪', '孔皓轩', 0, '方文轩', 867273994);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('苏立轩', '卢鹏', 'www.cecelia-olson.name', '韩钰轩', '程思淼', 0, '孟钰轩', 80484);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('顾鹏煊', '钟哲瀚', 'www.shay-heller.biz', '陆煜祺', '莫胤祥', 0, '万泽洋', 4);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('莫煜祺', '彭鹭洋', 'www.santos-crist.org', '郝昊天', '高志强', 0, '孙立轩', 399620);
insert into gdapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('秦明轩', '叶雨泽', 'www.angelena-grimes.com', '周苑博', '曾炫明', 0, '马黎昕', 950);


-- auto-generated definition
create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                       null comment '用户名',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '用户状态',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    role         int      default 0                 not null comment '用户角色 0-普通用户 1-管理员',
    vipCode      varchar(512)                       null comment 'vip编号'
)
    comment '用户';

create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                       null comment '用户名',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    `accessKey` varchar(512) not null comment 'accessKey',
    `secretKey` varchar(512) not null comment 'secretKey',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '用户状态',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    role         int      default 0                 not null comment '用户角色 0-普通用户 1-管理员',
    vipCode      varchar(512)                       null comment 'vip编号'


)
    comment '用户';

create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                       null comment '用户名',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    `accessKey` varchar(512) not null comment 'accessKey',
    `secretKey` varchar(512) not null comment 'secretKey',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '用户状态',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    role         int      default 0                 not null comment '用户角色 0-普通用户 1-管理员',
    vipCode      varchar(512)                       null comment 'vip编号'


)
    comment '用户';



-- auto-generated definition
create table user_interface_info
(
    id              bigint auto_increment comment '主键'
        primary key,
    userId          bigint                             not null comment '调用用户的ID',
    interfaceInfoId bigint                             not null comment '接口ID',
    totalNum        int      default 0                 not null comment '总调用次数',
    leftNum         int      default 0                 not null comment '剩余调用次数'
    status          int      default 0                 not null comment '状态:0-正常,1-禁用',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除:0-未删,1-已删'
)
    comment '用户调用接口信息表';



-- auto-generated definition
create table interface_info
(
    id               bigint auto_increment comment '主键'
        primary key,
    name             varchar(256)                       not null comment '接口名',
    description      varchar(256)                       null comment '描述',
    url              varchar(512)                       not null comment '接口地址',
    requestParams    text                               not null comment '请求参数',
    requestHeader    text                               null comment '请求头',
    responseHeader   text                               null comment '响应头',
    status           int      default 0                 not null comment '接口状态(0关闭,1开启)',
    method           varchar(256)                       not null comment '请求类型',
    userId           bigint                             not null comment '创建人',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDeleted        tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)',
    sdk              varchar(255)                       null comment '接口对应的SDK类路径',
    parameterExample varchar(255)                       null comment '参数示例'
)
    comment '接口信息表';


-- ----------------------------
-- Table structure for interface_charging
-- ----------------------------
DROP TABLE IF EXISTS `interface_charging`;
CREATE TABLE `interface_charging`  (
                                       `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `interfaceId` bigint(0) NOT NULL COMMENT '接口id',
                                       `charging` float(255, 2) NOT NULL COMMENT '计费规则（元/条）',
                                       `availablePieces` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '接口剩余可调用次数',
                                       `userId` bigint(0) NOT NULL COMMENT '创建人',
                                       `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                       `updateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                       `isDelete` tinyint(0) NOT NULL DEFAULT 1 COMMENT '是否删除(0-删除 1-正常)',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of interface_charging
-- ----------------------------
INSERT INTO `interface_charging` VALUES (1, 3, 0.52, '977', 1, '2023-07-24 14:33:49', '2023-08-02 19:31:39', 1);










DROP TABLE IF EXISTS `alipay_info`;
CREATE TABLE `alipay_info`  (
                                `orderNumber` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单id',
                                `subject` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易名称',
                                `totalAmount` float(10, 2) NOT NULL COMMENT '交易金额',
                                `buyerPayAmount` float(10, 2) NOT NULL COMMENT '买家付款金额',
                                `buyerId` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '买家在支付宝的唯一id',
                                `tradeNo` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '支付宝交易凭证号',
                                `tradeStatus` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易状态',
                                `gmtPayment` datetime(0) NOT NULL COMMENT '买家付款时间',
                                PRIMARY KEY (`orderNumber`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;