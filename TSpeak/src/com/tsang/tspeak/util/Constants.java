/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tsang.tspeak.util;

/**
 * 该类定义了微博授权时所需要的参数。
 * 
 * @author SINA
 * @since 2013-09-29
 */
public interface Constants {

    /** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
    public static final String APP_KEY      = "2749623658";

    /** 
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * 
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * 
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * 
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    
    /** 微博短链接正则表达式 */
    public static final String regex_http = "http(s)?://([a-zA-Z|\\d]+\\.)+[a-zA-Z|\\d]+(/[a-zA-Z|\\d|\\-|\\+|_./?%=]*)?";
    public static final String regex_at = "@[\\u4e00-\\u9fa5\\w\\-]+";
    public static final String regex_sharp = "#([^\\#|.]+)#";
    public static final String regex_emoji = "\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]";
    
    /** 程序的 APP_SECRET */
    public static final String WEIBO_DEMO_APP_SECRET = "52d0924e7d129657e7d2eb2fe28b3327";
	/** 通过 code 获取 Token 的 URL */
    public static final String OAUTH2_ACCESS_TOKEN_URL = "https://open.weibo.cn/oauth2/access_token";
	/** 根据用户ID获取用户信息的URL */
    public static final String USER_SHOW_URL = "https://api.weibo.com/2/users/show.json";   
    /** 发布一条新微博 */
	public static final String UPDATE_STATUS_URL = "https://api.weibo.com/2/statuses/update.json";
	/** 发布一条带图片的新微博 */
	public static final String UPLOAD_STATUS_URL = "https://upload.api.weibo.com/2/statuses/upload.json";
	/** 转发一条微博 */
	public static final String REPOST_STATUS_URL = "https://api.weibo.com/2/statuses/repost.json";
	/** 对一条微博进行评论 */
	public static final String COMMENT_STATUS_URL = "https://api.weibo.com/2/comments/create.json";
	/**获取用户的关注列表*/
	public static final String FRIENDS_URL = "https://api.weibo.com/2/friendships/friends.json";	
	/** 获取某个用户最新发表的微博列表的URL,新版接口只能获取授权用户的微博列表 */
	public static final String USER_TIMELINE_URL = "https://api.weibo.com/2/statuses/user_timeline.json";
	/** 根据微博ID删除指定微博 */
	public static final String STATUS_DESTROY_URL = "https://api.weibo.com/2/statuses/destroy.json";
	/** 根据微博ID获取评论列表 */
	public final static String COMMENTS_SHOW_URL = "https://api.weibo.com/2/comments/show.json";
	/** 获取当前登录用户及其所关注用户的最新微博的URL */
	public static final String HOME_TIMELINE_URL = "https://api.weibo.com/2/statuses/home_timeline.json";
	
}
