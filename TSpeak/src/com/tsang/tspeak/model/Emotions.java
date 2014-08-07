package com.tsang.tspeak.model;

import java.io.Serializable;

public class Emotions implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 表情使用的替代文字 */
	private String phrase;
	/** 表情图片类型 */
	private String type;
	/** 表情图片存放的位置 */
	private String url;
	/** 是否为热门表情 */
	private String isHot;
	/** 是否属于通用 */
	private String isCommon;
	/** 表情分类 */
	private String category;
	/** 表情名称 */
	private String imageName;

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIsHot() {
		return isHot;
	}

	public void setIsHot(String isHot) {
		this.isHot = isHot;
	}

	public String getIsCommon() {
		return isCommon;
	}

	public void setIsCommon(String isCommon) {
		this.isCommon = isCommon;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

}
