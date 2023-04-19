package com.app.IVAS.service;

import com.app.IVAS.Enum.PermissionTypeConstant;
import com.app.IVAS.api_response.LoginResponse;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.userManagement.Area;
import com.app.IVAS.entity.userManagement.Lga;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.Role;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author uhuegbulem chinomso
 * email: chimaisaac60@gmail.com
 * Oct, 2022
 **/

public interface UserManagementService {

    PortalUser createUser(UserDto user, PortalUser createdBy, Role role);

    void deactivateUser(Long userId);

    void activateUser(Long userId);

    LoginResponse authenticateUser(LoginRequestDto dto) throws Exception;

    void logout();

    Role createRole(String name, List<PermissionTypeConstant> permissionTypeConstants);

    Role updateRole(String name, List<PermissionTypeConstant> permissionTypeConstants, String action);

    PortalUserPojo get(PortalUser user);

    List<PortalUserPojo> get(List<PortalUser> users);

    List<String> getRoles();

    List<Lga> getLGAs();

    List<Area> getAreas(Long id);

    void changePassword(PasswordDto dto) throws Exception;

    String generateOTP(String phoneNumber) throws URISyntaxException;

    void resetPassword(PasswordDto dto);

    PortalUserPojo UpdateUser(Long id,UserDto user, PortalUser updatedBy);

    void resetPasswordMobile(PasswordDto dto) throws Exception;

    List<PortalUserPojo>  searchOtherUsers(List<PortalUser> results);
}
