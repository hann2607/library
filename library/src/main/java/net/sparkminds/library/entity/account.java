//package net.sparkminds.library.entity;
//
//import java.math.BigInteger;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Inheritance;
//import jakarta.persistence.InheritanceType;
//import jakarta.persistence.Table;
//import lombok.RequiredArgsConstructor;
//
//@Entity
//@Table(name = "account")
//@RequiredArgsConstructor
//@Inheritance(strategy = InheritanceType.JOINED)
//public class account{
//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	private BigInteger id;
//	
//	@Column(name = "Name")
//	private String name;
//	
//	@Column(name = "Age")
//	private BigInteger age;
//}
