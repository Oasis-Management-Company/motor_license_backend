package com.app.IVAS.controller;


import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PermissionTypeConstant;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.api_response.LoginResponse;
import com.app.IVAS.dto.LoginRequestDto;
import com.app.IVAS.dto.PasswordDto;
import com.app.IVAS.dto.PortalUserPojo;
import com.app.IVAS.dto.UserDto;
import com.app.IVAS.dto.filters.PortalUserSearchFilter;
import com.app.IVAS.entity.userManagement.*;
import com.app.IVAS.entity.userManagement.QPortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.UserManagementService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserManagementService userManagementService;
    private final RoleRepository roleRepository;
    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final JwtService jwtService;
    private final PortalUserRepository portalUserRepository;
    private final ZoneRepository zoneRepository;
    private final ZonalOfficeRepository zonalOfficeRepository;
    private final PermissionRepository permissionRepository;


    @GetMapping("/search")
    @Transactional
    public QueryResults<PortalUserPojo> searchUsers(PortalUserSearchFilter filter){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        JPAQuery<PortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QPortalUser.portalUser)
                .where(predicateExtractor.getPredicate(filter))
                .where(QPortalUser.portalUser.role.name.notEqualsIgnoreCase("GENERAL_USER"))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.getCreatedAfter() != null){
            portalUserJPAQuery.where(QPortalUser.portalUser.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            portalUserJPAQuery.where(QPortalUser.portalUser.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QPortalUser.portalUser);
        QueryResults<PortalUser> portalUserQueryResults = portalUserJPAQuery.select(QPortalUser.portalUser).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(userManagementService.get(portalUserQueryResults.getResults()), portalUserQueryResults.getLimit(), portalUserQueryResults.getOffset(), portalUserQueryResults.getTotal());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequestDto dto) throws Exception {
        LoginResponse response = userManagementService.authenticateUser(dto);
        return ResponseEntity.ok(response);

    }
    @PostMapping("/create")
    public PortalUserPojo createUser(@RequestBody @Valid UserDto dto) {
        Role role = roleRepository.findByNameIgnoreCase(dto.getRole()).orElseThrow(RuntimeException::new);
        return userManagementService.get(userManagementService.createUser(dto, jwtService.user, role));
    }

    @PostMapping("/deactivate")
    public HttpStatus deactivateUser(@RequestParam Long userId) {
        userManagementService.deactivateUser(userId);
        return HttpStatus.OK;
    }

    @PostMapping("/activate")
    public HttpStatus activateUser(@RequestParam Long userId) {
        userManagementService.activateUser(userId);
        return HttpStatus.OK;
    }

    @PostMapping("/change-password")
    public HttpStatus changePassword(@RequestBody PasswordDto dto) throws Exception {
        userManagementService.changePassword(dto);
        return HttpStatus.OK;
    }

    @PostMapping("/reset-password")
    public HttpStatus resetPassword(@RequestBody PasswordDto dto) {
        userManagementService.resetPassword(dto);
        return HttpStatus.OK;
    }

    @PostMapping("/generate-otp")
    public String getOTP(@RequestParam String username) throws URISyntaxException {
        PortalUser user =  portalUserRepository.findByUsernameIgnoreCaseAndStatus(username, GenericStatusConstant.ACTIVE).get();
        if (user.getPhoneNumber().startsWith("234")){
            return userManagementService.generateOTP("+" + user.getPhoneNumber());
        } else if (user.getPhoneNumber().startsWith("0")){
            String phoneNumber = user.getPhoneNumber().replaceFirst("0", "+234");
            return userManagementService.generateOTP(phoneNumber);
        }
        return userManagementService.generateOTP(user.getPhoneNumber());
    }

    @GetMapping("/roles")
    public List<String> getRoles(){
        return userManagementService.getRoles();
    }

    @GetMapping("/permissions")
    public List<Permission> getPermissions(@RequestParam String roleName){
        return permissionRepository.findAllByRole(roleRepository.findByNameIgnoreCase(roleName).get());
    }

    @GetMapping("/lga")
    public List<Lga> getLGAs(){
        return userManagementService.getLGAs();
    }

    @GetMapping("/area")
    public List<Area> getAreas(@RequestParam Long id){
        return userManagementService.getAreas(id);
    }


    @PostMapping("/create-roles")
    @Transactional
    public ResponseEntity<Role> createRole(@RequestParam String name,
                                           @RequestParam List<PermissionTypeConstant> permissionTypeConstantList,
                                           @RequestParam String action){
        if (action.equalsIgnoreCase("CREATE")){
            return ResponseEntity.ok(userManagementService.createRole(name, permissionTypeConstantList));
        }
            return ResponseEntity.ok(userManagementService.updateRole(name, permissionTypeConstantList, action));

    }

    @PostMapping("/update-user")
    @Transactional
    public ResponseEntity<PortalUserPojo> updatePortalUser(@RequestBody UserDto dto,
                                                           @RequestParam Long id){
        PortalUserPojo pojo = userManagementService.UpdateUser(id, dto, jwtService.user);

        return ResponseEntity.ok(pojo);
    }

    @GetMapping("/get-user/{id}")
    public PortalUser getPortalUser(@PathVariable Long id){
        return portalUserRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @GetMapping("/get-user")
    public PortalUser getPortalUserByEmail(@RequestParam String email){
        return portalUserRepository.findByUsernameIgnoreCaseAndStatus(email, GenericStatusConstant.ACTIVE).orElseThrow(RuntimeException::new);
    }

    @PostMapping("/reset-password/mobile")
    public HttpStatus resetPasswordMobile(@RequestBody PasswordDto dto) throws Exception {
        userManagementService.resetPasswordMobile(dto);
        return HttpStatus.OK;
    }

    @GetMapping("/report/zones")
    public List<Zone> getZones(){
        return  zoneRepository.findAll();
    }

    @GetMapping("/report/office")
    public List<ZonalOffice> getZonalOffice(@RequestParam Long id) {
        Zone zone = zoneRepository.findById(id).get();
        return  zonalOfficeRepository.findByZone(zone);
    }

    @GetMapping("/search/others")
    @Transactional
    public QueryResults<PortalUserPojo> searchOtherUsers(PortalUserSearchFilter filter){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        JPAQuery<PortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QPortalUser.portalUser)
                .where(predicateExtractor.getPredicate(filter))
                .where(QPortalUser.portalUser.role.name.equalsIgnoreCase("GENERAL_USER"))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.getCreatedAfter() != null){
            portalUserJPAQuery.where(QPortalUser.portalUser.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            portalUserJPAQuery.where(QPortalUser.portalUser.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QPortalUser.portalUser);
        QueryResults<PortalUser> portalUserQueryResults = portalUserJPAQuery.select(QPortalUser.portalUser).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(userManagementService.searchOtherUsers(portalUserQueryResults.getResults()), portalUserQueryResults.getLimit(), portalUserQueryResults.getOffset(), portalUserQueryResults.getTotal());
    }


}
