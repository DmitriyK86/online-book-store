package bookstore.service.impl;

import bookstore.model.Role;
import bookstore.repository.role.RoleRepository;
import bookstore.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role findRoleByRoleName(Role.RoleName roleName) {
        return roleRepository.findRoleByRoleName(roleName);
    }
}
