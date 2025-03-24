package br.com.brunogodoif.projectmanagement.infrastructure.security;

import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.UserEntity;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                "User not found with username: " + username));

        return UserDetailsImpl.build(user);
    }
}
