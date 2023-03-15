package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PermissionTypeConstant;
import com.app.IVAS.api_response.LoginResponse;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.userManagement.Lga;
import com.app.IVAS.entity.userManagement.Permission;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.Role;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.security.PasswordService;
import com.app.IVAS.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final PortalUserRepository portalUserRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final LgaRepository lgaRepository;
    private final AreaRepository areaRepository;
    private final PasswordService passwordService;
    private final ZonalOfficeRepository zonalOfficeRepository;

    @Override
    @Transactional
    public PortalUser createUser(UserDto user, PortalUser createdBy, Role role) {
        System.out.println(user);
        PortalUser portalUser = new PortalUser();
        portalUser.setCreatedAt(LocalDateTime.now());
        portalUser.setEmail(user.getEmail());
        portalUser.setFirstName(user.getFirstName());
        portalUser.setLastName(user.getLastName());
        portalUser.setDisplayName(String.format("%s %s", user.getFirstName(), user.getLastName()));
        portalUser.setUsername(user.getEmail());
        portalUser.setStatus(GenericStatusConstant.ACTIVE);
        portalUser.setPhoneNumber(user.getPhoneNumber());
        portalUser.setNationalIdentificationNumber(user.getNin());
        portalUser.setGeneratedPassword(passwordService.hashPassword(user.getPassword()));
        portalUser.setImage(user.getPhoto());
        portalUser.setAddress(user.getAddress());
        if (user.getLga() != null){
            portalUser.setLga(lgaRepository.findById(user.getLga()).orElseThrow(RuntimeException::new));
        }
        if (user.getArea() !=null){
            portalUser.setArea(areaRepository.findById(user.getArea()).orElseThrow(RuntimeException::new));
        }
        if (user.getZonalOffice() != null){
            portalUser.setOffice(zonalOfficeRepository.findById(user.getZonalOffice()).orElseThrow(RuntimeException::new));
        }
        portalUser.setRole(role);

        portalUserRepository.save(portalUser);
        return portalUser;
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        PortalUser portalUser = portalUserRepository.findByIdAndStatus(userId, GenericStatusConstant.ACTIVE).get();
        LocalDateTime now = LocalDateTime.now();

        portalUser.setStatus(GenericStatusConstant.INACTIVE);
        portalUser.setLastUpdatedAt(now);
        portalUser.setLastUpdatedBy(jwtService.user);
        portalUserRepository.save(portalUser);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        PortalUser portalUser = portalUserRepository.findByIdAndStatus(userId, GenericStatusConstant.INACTIVE).get();

        portalUser.setStatus(GenericStatusConstant.ACTIVE);
        portalUser.setLastUpdatedAt(LocalDateTime.now());
        portalUser.setLastUpdatedBy(jwtService.user);
        portalUserRepository.save(portalUser);
    }

    @Override
    public LoginResponse authenticateUser(LoginRequestDto dto) throws Exception {

        PortalUser user = portalUserRepository.findByUsernameIgnoreCaseAndStatus(dto.getUsername(), GenericStatusConstant.ACTIVE)
                .orElseThrow(() -> new Exception("Invalid Username - " + dto.getUsername()));

       if (passwordService.comparePassword(user.getGeneratedPassword(), dto.getPassword())){
           String token = jwtService.generateJwtToken(user.getId());
           LoginResponse loginResponse = new LoginResponse();
           loginResponse.setFirstName(user.getFirstName());
           loginResponse.setLastName(user.getLastName());
           loginResponse.setImage(user.getImage());
           loginResponse.setToken(token);
           loginResponse.setUsername(user.getEmail());
           loginResponse.setRole(user.getRole().getName());
           loginResponse.setPermissions(getPermission(user.getRole()));

           return loginResponse;
       } else throw new Exception("Invalid Password");
    }

    @Override
    @Transactional
    public Role createRole(String name, List<PermissionTypeConstant> permissionTypeConstants) {
        return roleRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(name);
            roleRepository.save(newRole);
            createPermission(permissionTypeConstants, newRole);
            return newRole;
        });
    }

    @Override
    public Role updateRole(String name, List<PermissionTypeConstant> permissionTypeConstants, String action) {
        return null;
    }

    @Override
    public PortalUserPojo get(PortalUser user) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");
        PortalUserPojo pojo = new PortalUserPojo();
        pojo.setName(user.getDisplayName());
        pojo.setEmail(user.getEmail());
        pojo.setDateCreated(user.getCreatedAt().format(df));
        pojo.setRole(user.getRole());
        pojo.setStatus(user.getStatus());
        pojo.setId(user.getId());

        return pojo;
    }

    @Override
    public List<PortalUserPojo> get(List<PortalUser> users) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");
        return users.stream().map(user -> {

            PortalUserPojo pojo = new PortalUserPojo();
            pojo.setName(user.getDisplayName());
            pojo.setEmail(user.getEmail());
            pojo.setDateCreated(user.getCreatedAt().format(df));
            pojo.setRole(user.getRole());
            pojo.setStatus(user.getStatus());
            pojo.setId(user.getId());
            pojo.setPhoneNumber(user.getPhoneNumber());

            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(Role::getName).collect(Collectors.toList());
    }

    @Override
    public List<Lga> getLGAs() {
        return lgaRepository.findAll();
    }

//    @Override
//    public List<Area> getAreas(Long id) {
//        JPAQuery<>
//        return areaRepository.findByLgaId(id);
//    }

    @Override
    public void resetPassword(PasswordDto dto) throws Exception {
        PortalUser user = portalUserRepository.findByUsernameIgnoreCaseAndStatus(dto.getUsername(), GenericStatusConstant.ACTIVE)
                .orElseThrow(() -> new Exception("Invalid Username - " + dto.getUsername()));

        if (passwordService.comparePassword(user.getGeneratedPassword(), dto.getOldPassword())){
           user.setGeneratedPassword(passwordService.hashPassword(dto.getNewPassword()));
           user.setLastUpdatedAt(LocalDateTime.now());
           portalUserRepository.save(user);
        } else throw new Exception("Invalid Password");
    }

    @Override
    public PortalUserPojo UpdateUser(Long id,UserDto user, PortalUser updatedBy) {
        PortalUser portalUser = portalUserRepository.findById(id).orElseThrow(RuntimeException::new);

        portalUser.setLastUpdatedAt(LocalDateTime.now());
        portalUser.setLastUpdatedBy(updatedBy);

        if (user.getEmail() != null){
            portalUser.setEmail(user.getEmail());
            portalUser.setUsername(user.getEmail());
        }
        if (user.getFirstName() != null){
            portalUser.setFirstName(user.getFirstName());
            portalUser.setDisplayName(String.format("%s %s", user.getFirstName(), portalUser.getLastName()));
        }
        if (user.getLastName() != null){
            portalUser.setLastName(user.getLastName());
            portalUser.setDisplayName(String.format("%s %s", portalUser.getFirstName(), user.getLastName()));
        }
        if(user.getPassword() != null){
            portalUser.setGeneratedPassword(passwordService.hashPassword(user.getPassword()));
        }
        if (user.getRole() != null){
            portalUser.setRole(roleRepository.findByNameIgnoreCase(user.getRole()).orElseThrow(RuntimeException::new));
        }
        if (user.getAddress() != null){
            portalUser.setAddress(user.getAddress());
        }
        if (user.getPhoneNumber() != null){
            portalUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (user.getZonalOffice() != null){
            portalUser.setOffice(zonalOfficeRepository.findById(user.getZonalOffice()).orElseThrow(RuntimeException::new));
        }
        portalUserRepository.save(portalUser);
        return get(portalUser);
    }


    private void createPermission(List<PermissionTypeConstant> permissionTypeConstants, Role role) {
        permissionTypeConstants.forEach(permissionTypeConstant -> {
            Permission permission = new Permission();
            permission.setPermissionTypeConstant(permissionTypeConstant);
            permission.setRole(role);
            permissionRepository.save(permission);
        });
    }

    private List<PermissionTypeConstant> getPermission(Role role){
        List<Permission> permissions = permissionRepository.findAllByRole(role);

        List<PermissionTypeConstant> permissionTypeConstantList = new ArrayList<>();

        permissions.forEach(permission -> {
            permissionTypeConstantList.add(permission.getPermissionTypeConstant());
        });

        return permissionTypeConstantList;
    }
    @Override
    public void resetPasswordMobile(PasswordDto dto) throws Exception {
        PortalUser user = portalUserRepository.findByUsernameIgnoreCaseAndStatus(dto.getUsername(), GenericStatusConstant.ACTIVE)
                .orElseThrow(() -> new Exception("Invalid Username - " + dto.getUsername()));

            user.setGeneratedPassword(passwordService.hashPassword(dto.getNewPassword()));
            user.setLastUpdatedAt(LocalDateTime.now());
            portalUserRepository.save(user);

    }

}
