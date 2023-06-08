package com.app.IVAS.controller;


import com.app.IVAS.Enum.ActivityStatusConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PermissionTypeConstant;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.api_response.LoginResponse;
import com.app.IVAS.dto.LoginRequestDto;
import com.app.IVAS.dto.PasswordDto;
import com.app.IVAS.dto.PortalUserPojo;
import com.app.IVAS.dto.UserDto;
import com.app.IVAS.dto.filters.EditPortalUserSearchFilter;
import com.app.IVAS.dto.filters.PortalUserSearchFilter;
import com.app.IVAS.entity.EditPortalUser;
import com.app.IVAS.entity.QEditPortalUser;
import com.app.IVAS.entity.userManagement.*;
import com.app.IVAS.entity.userManagement.QPortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.ActivityLogService;
import com.app.IVAS.service.UserManagementService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Transactional
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
    private final ActivityLogService activityLogService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @GetMapping("/search")
    @Transactional
    public QueryResults<PortalUserPojo> searchUsers(PortalUserSearchFilter filter){

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Long id){
        jwtService.invalidateToken(id);
        activityLogService.createActivityLog((jwtService.user.getDisplayName() + " logged out"), ActivityStatusConstant.LOGOUT);
        jwtService.user = null;
        return ResponseEntity.ok("");
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
    public char[] getOTP(@RequestParam String username) throws URISyntaxException {
        PortalUser user =  portalUserRepository.findByUsernameIgnoreCaseAndStatus(username, GenericStatusConstant.ACTIVE).get();
        return Hex.encode(userManagementService.generateOTP(user.getPhoneNumber()).getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/check-otp")
    public Boolean checkOtp(@RequestParam String otp, @RequestParam String hexOtp){
        String hex = String.valueOf(Hex.encode(otp.getBytes(StandardCharsets.UTF_8)));
        return hexOtp.equals(hex);
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
    public Map<String,Object> getPortalUserByEmail(@RequestParam String email){
        Tuple tuple = appRepository.startJPAQuery(QPortalUser.portalUser)
                .select(QPortalUser.portalUser.displayName, QPortalUser.portalUser.id)
                .where(QPortalUser.portalUser.username.equalsIgnoreCase(email)
                        .and(QPortalUser.portalUser.status.eq(GenericStatusConstant.ACTIVE))).fetchOne();

        Map<String, Object> data = new HashMap<>();
        assert tuple != null;
        data.put("id", tuple.get(QPortalUser.portalUser.id));
        data.put("displayName", tuple.get(QPortalUser.portalUser.displayName));
        return data;
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

    @GetMapping("/get-office")
    public List<ZonalOffice> getOffice() {
        return  zonalOfficeRepository.findAll();
    }

    @GetMapping("/search/others")
    @Transactional
    public QueryResults<PortalUserPojo> searchOtherUsers(PortalUserSearchFilter filter){

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

    @GetMapping("/search/pending-edit")
    @Transactional
    public QueryResults<PortalUserPojo> searchPendingTaxPayerEdit(PortalUserSearchFilter filter){

        JPAQuery<PortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QPortalUser.portalUser)
                .where(predicateExtractor.getPredicate(filter))
                .where(QPortalUser.portalUser.regType.eq(RegType.EDIT))
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

    @GetMapping("/search/pending/edit-portalUser")
    @Transactional
    public QueryResults<EditPortalUser> searchPendingPortalEdit(EditPortalUserSearchFilter filter){

        JPAQuery<EditPortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QEditPortalUser.editPortalUser)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));


        if (filter.getCreatedAfter() != null){
            portalUserJPAQuery.where(QEditPortalUser.editPortalUser.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            portalUserJPAQuery.where(QEditPortalUser.editPortalUser.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QEditPortalUser.editPortalUser);
        QueryResults<EditPortalUser> portalUserQueryResults = portalUserJPAQuery.select(QEditPortalUser.editPortalUser).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(userManagementService.searchEditPortalUsers(portalUserQueryResults.getResults()), portalUserQueryResults.getLimit(), portalUserQueryResults.getOffset(), portalUserQueryResults.getTotal());
    }


    @GetMapping("/get-user/phone")
    public PortalUserPojo getPortalUserByPhone(@RequestParam String phone){
        return userManagementService.portalUserByPhone(phone);
    }

    @GetMapping("/search/pending-edit/mla")
    @Transactional
    public QueryResults<PortalUserPojo> searchPendingTaxPayerEditMla(PortalUserSearchFilter filter){

        JPAQuery<PortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QPortalUser.portalUser)
                .where(predicateExtractor.getPredicate(filter))
                .where(QPortalUser.portalUser.regType.eq(RegType.EDIT))
                .where(QPortalUser.portalUser.createdBy.id.eq(jwtService.user.getId()))
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
