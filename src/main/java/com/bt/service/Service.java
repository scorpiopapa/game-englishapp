package com.bt.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class Service {

	static final String DRAW_THE_SPEED = "speed";
	static final String HOLLY_CHAINS = "chains";
	
	static final String OTHER = "other";
	
	static List<String> categories = Arrays.asList(new String[]{"suggest", "payment", "account", OTHER});
	
	static ApplicationContext ctx = new ClassPathXmlApplicationContext("spring/mail-context.xml");
	
	Map<String, String> messages = new HashMap<>();
	
	public Service(){}
	
	public Service(String game, Locale locale){
		try{
			ResourceBundle props = ResourceBundle.getBundle(game, locale);
			
			Iterator<String> keys = props.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				String value = new String(props.getString(key).getBytes("ISO-8859-1"), "UTF-8");
				
				messages.put(key, value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getMessage(String key){
		return messages.get(key);
	}
	
	public void processFeedback(String category, String uid, String email, String feedback, String game){
		int sid = saveData(category, uid, email, feedback, game);
		
		// send notice mail
		String title = sid + ":" + uid + " " + getMessage("title.game") + " - " + getMessage("select." + category);
//		feedback += feedback;
//		+ "\\n" + "SELECT * FROM USER_FEEDBACK WHERE SID = '" + sid + "'";
		sendMail(title, feedback);
	}
	
	int saveData(String category, String uid, String email, String feedback, String game){
		int sid = 0;
		
		String cat = category;
		if(!categories.contains(category)){
			cat = OTHER;
		}
		
		Connection conn = null;
		
		try {
			Credential cred = getCredential(game);
			if(cred == null) {
				System.out.println("no data was inserted to DB for game " + game);
				return 0;
			}
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(cred.url, cred.uid, cred.pwd);
			
			Date date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).getTime();
			String sql = "INSERT INTO USER_FEEDBACK(USER_ID,CATEGORY,EMAIL,TEXT,CREATE_DATE) "
					+ "VALUES(?,?,?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, uid);
			stmt.setString(2, cat);
			stmt.setString(3, email);
			stmt.setString(4, feedback);
			stmt.setDate(5, new java.sql.Date(date.getTime()));
			
			stmt.executeUpdate();
			
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()){
				sid = rs.getInt(1);
			}
			
		} catch (Exception e) {
			String msg = "failed to save user feedback, game = {0}, uid = {1}, email = {2}, feedback = {3}";
			System.out.println(MessageFormat.format(msg, new Object[]{game, uid, email, feedback}));
			e.printStackTrace();
		}finally{
			try {
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return sid;
	}
	
	void sendMail(String title, String msg){
		try{
			JavaMailSender sender = ctx.getBean(JavaMailSender.class);
			SimpleMailMessage mail = ctx.getBean(SimpleMailMessage.class);
			
			mail.setSubject(title);
			mail.setText(msg);
			sender.send(mail);
		}catch(Throwable t){
			System.out.println("failed to send mail, body -> " + msg);
			t.printStackTrace();
		}
	}
	
	Credential getCredential(String game){
		Credential cred = new Credential();
		
		ResourceBundle props = ResourceBundle.getBundle("config." + game);
		cred.url = props.getString("url");
		cred.uid = props.getString("uid");
		cred.pwd = props.getString("pwd");
		
		return cred;
	}
	
	class Credential{
		String url;
		String uid;
		String pwd;
	}
	
	public static void main(String[] args){
		System.out.println(Locale.PRC.toString());
		System.out.println(Locale.CHINA.toString());
	}
}
