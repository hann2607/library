package net.sparkminds.library.dto.membermanagement;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.library.enumration.EnumRole;
import net.sparkminds.library.enumration.EnumStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberManaResponse {

	private Long id;

	private String email;

	private String password;
	
	private String lastname;
	
	private String phone;
	
	private String address;
	
	private String avatar;

	private LocalDateTime blockedAt;

	private String reasonBlocked;
	
	private Integer loginAttempt;
	
	private boolean isFirstTimeLogin;

	private boolean isVerify;
	
	private boolean mfa;
	
	private String secret;

	private EnumStatus status;

	private EnumRole roleName;
}
