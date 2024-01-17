package net.sparkminds.library.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.sparkminds.library.dto.jwt.JwtRequest;

@Component
public class AuthenticationEventListener {
	
	@EventListener
	public void handleAuthenticationSuccess(AuthenticationEvent authenticationEvent) {
		JwtRequest jwtRequest = authenticationEvent.getJwtRequest();
		// Xử lý logic sau khi đăng nhập thành công
		// Lưu ý: Bạn có thể autowire các service cần thiết để xử lý logic ở đây
	}

	@EventListener
	public void handleAuthenticationFailure(AuthenticationEvent authenticationEvent) {
		JwtRequest jwtRequest = authenticationEvent.getJwtRequest();
		// Xử lý logic sau khi đăng nhập thất bại
		// Lưu ý: Bạn có thể autowire các service cần thiết để xử lý logic ở đây
	}
}
