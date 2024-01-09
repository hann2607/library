//package net.sparkminds.library.entity;
//
//import java.time.LocalDateTime;
//
//import org.springframework.data.annotation.CreatedBy;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedBy;
//import org.springframework.data.annotation.LastModifiedDate;
//
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.MappedSuperclass;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@MappedSuperclass
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
//public abstract class Auditable {
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @CreatedDate
//    private LocalDateTime createdAt;
//    
//    @CreatedBy
//    private Account createdBy;
//
//    @LastModifiedDate
//    private LocalDateTime updatedAt;
//    
//    @LastModifiedBy
//    private Account updatedBy;
//}
