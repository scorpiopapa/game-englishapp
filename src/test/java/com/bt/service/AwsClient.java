package com.bt.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class AwsClient {

	public static void main(String[] args) {
		test2();
	}

	static void test1(){
		AmazonSNSClient client = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());
		PublishRequest request = new PublishRequest("arn:aws:sns:ap-northeast-1:651797352322:MyTopic", "test mesage", "test");
		
//		PublishResult result = client.publish("arn:aws:sns:ap-northeast-1:651797352322:MyTopic", "test mesage", "test");
		PublishResult result = client.publish(request);
		System.out.println(result.toString());	
	}
	
	static void test2(){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("com/bt/service/mail-context.xml");
		JavaMailSender sender = ctx.getBean(JavaMailSender.class);
		
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo("space.cowboys@qq.com");
		mail.setFrom("space.cowboys@qq.com");
		mail.setSubject("test subject");
		mail.setText("this is a test");
		sender.send(mail);
		
		System.out.println("done!");
	}
}
