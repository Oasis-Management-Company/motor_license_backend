package com.app.IVAS.security;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.PortalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SystemUserDetailsService implements UserDetailsService {

  private final PortalUserRepository portalUserRepository;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Optional<PortalUser> user = portalUserRepository.findByUsernameIgnoreCaseAndStatus(username, GenericStatusConstant.ACTIVE);
    if (!user.isPresent()) {
      throw new UsernameNotFoundException("Could not find user");
    }

    return new IVASUserDetails(user.get());

  }
}
