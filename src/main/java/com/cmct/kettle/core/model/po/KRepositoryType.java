package com.cmct.kettle.core.model.po;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "k_repository_type")
public class KRepositoryType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer repositoryTypeId ;
	private String repositoryTypeCode ;
	private String repositoryTypeDes ;
	
	public KRepositoryType() {
	}
	
	public Integer getRepositoryTypeId(){
		return  repositoryTypeId;
	}
	public void setRepositoryTypeId(Integer repositoryTypeId ){
		this.repositoryTypeId = repositoryTypeId;
	}
	
	public String getRepositoryTypeCode(){
		return  repositoryTypeCode;
	}
	public void setRepositoryTypeCode(String repositoryTypeCode ){
		this.repositoryTypeCode = repositoryTypeCode;
	}
	
	public String getRepositoryTypeDes(){
		return  repositoryTypeDes;
	}
	public void setRepositoryTypeDes(String repositoryTypeDes ){
		this.repositoryTypeDes = repositoryTypeDes;
	}
	
}