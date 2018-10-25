package com.icrisat.sbdm.ismu.retrofit.bms;

public class User {
	
	private String username;
	private String password;
	private String grant_type;
	private String client_id;
	public User(String username, String password, String grant_type, String client_id) {
		super();
		this.username = username;
		this.password = password;
		this.grant_type = grant_type;
		this.client_id = client_id;
	}
	
	
}
