package com.app.IVAS.configuration;


import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PermissionTypeConstant;
import com.app.IVAS.dto.UserDto;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.Role;
import com.app.IVAS.repository.PortalUserRepository;
import com.app.IVAS.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MasterRecordsLoader {

    private final TransactionTemplate transactionTemplate;
    private final UserManagementService userManagementService;
    private final PortalUserRepository portalUserRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        log.info("STARTING...........");
        transactionTemplate.execute(tx -> {
            try {
                loadDefaults();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private void loadDefaults(){

        List<PermissionTypeConstant> permissionTypeConstantList =  new ArrayList<>();
        permissionTypeConstantList.add(PermissionTypeConstant.CREATE_USER);
        permissionTypeConstantList.add(PermissionTypeConstant.UPDATE_USER);

        List<PermissionTypeConstant> permissionTypeConstantList1 =  new ArrayList<>();
        permissionTypeConstantList1.add(PermissionTypeConstant.DEFAULT);

        Role role = userManagementService.createRole("SUPER_ADMIN",permissionTypeConstantList);
        userManagementService.createRole("GENERAL_USER",permissionTypeConstantList1);

        UserDto dto = new UserDto();
        dto.setFirstName("IVAS");
        dto.setLastName("Admin");
        dto.setPhoneNumber("+2349088394985");
        dto.setEmail("IVAS@oasismgt.net");
        dto.setPassword("password");

        createSuperAdminUser(dto, role);

//        UserDto adminDto = new UserDto();
//        adminDto.setFirstName("CRO");
//        adminDto.setLastName("Admin");
//        adminDto.setPhoneNumber("+2349088394985");
//        adminDto.setEmail("cro@oasismgt.net");
//        adminDto.setPassword("password");
//        adminDto.setLga(74L);
//        adminDto.setArea(290L);
//
//        createAdminUser(adminDto, role3);
    }

    private void createSuperAdminUser(UserDto dto, Role role) {
        portalUserRepository.findByUsernameIgnoreCaseAndStatus(dto.getEmail(), GenericStatusConstant.ACTIVE)
                .orElseGet(() -> {
                    log.info("===========CREATING SUPER_ADMIN {} ============", dto.getEmail());
                    try {
                       PortalUser portalUser = userManagementService.createUser(dto, null, role);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

//    private void createAdminUser(UserDto dto, Role role) {
//        portalUserRepository.findByUsernameIgnoreCaseAndStatus(dto.getEmail(), GenericStatusConstant.ACTIVE)
//                .orElseGet(() -> {
//                    log.info("===========CREATING ADMIN {} ============", dto.getEmail());
//                    try {
//                        PortalUser portalUser = userManagementService.createUser(dto, null, role);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                });
//    }
}
