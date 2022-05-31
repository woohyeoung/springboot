package com.example.springboot.user.domain.user;

import com.example.springboot.board.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_user")
@Entity
public class UserEntity extends BaseTimeEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100, nullable = false, unique = true)  private String email;
	@Column(length = 300, nullable = false)					private String password;
	@Column(length = 20, nullable = false)					private String name;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { return null; }

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
