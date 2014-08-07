package com.tsang.tspeak.model;

import java.io.Serializable;

public class Emotions implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** ����ʹ�õ�������� */
	private String phrase;
	/** ����ͼƬ���� */
	private String type;
	/** ����ͼƬ��ŵ�λ�� */
	private String url;
	/** �Ƿ�Ϊ���ű��� */
	private String isHot;
	/** �Ƿ�����ͨ�� */
	private String isCommon;
	/** ������� */
	private String category;
	/** �������� */
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
