package bookstore.service;

import bookstore.model.Role;

public interface RoleService {
    Role findRoleByRoleName(Role.RoleName roleName);
}
