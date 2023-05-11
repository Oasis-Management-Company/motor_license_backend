package com.app.IVAS.controller;

import com.app.IVAS.Enum.ActivityStatusConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.dto.PlateNumberDto;
import com.app.IVAS.dto.data_transfer.DataTransferAssign;
import com.app.IVAS.dto.data_transfer.DataTransferStock;
import com.app.IVAS.dto.data_transfer.DataTransferUser;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.PlateNumberRequest;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.security.PasswordService;
import com.app.IVAS.service.PlateNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/api/data-transfer")
public class DataExportController {

    private final RoleRepository roleRepository;
    private final PrefixRepository prefixRepository;
    private final PlateNumberService plateNumberService;
    private final PlateNumberTypeRepository plateNumberTypeRepository;
    private final PortalUserRepository portalUserRepository;
    private final ZonalOfficeRepository zonalOfficeRepository;
    private final PasswordService passwordService;
    private final PlateNumberRepository plateNumberRepository;
    private final JwtService jwtService;


    @PostMapping("/create")
    @Transactional
    public ResponseEntity<?> transferUser(@RequestBody List<DataTransferUser> dtos) {
        for (DataTransferUser dto:dtos) {
            PortalUser portalUser = new PortalUser();
            portalUser.setCreatedAt(LocalDateTime.now());
            portalUser.setEmail(dto.getEmail());
            portalUser.setFirstName(dto.getName());
            portalUser.setLastName("");
            portalUser.setDisplayName(dto.getName());
            portalUser.setUsername(dto.getEmail());
            portalUser.setStatus(GenericStatusConstant.ACTIVE);
            portalUser.setPhoneNumber(dto.getPhone());
            portalUser.setUserVerified(false);
            portalUser.setAddress("12 parakou street");
            portalUser.setGeneratedPassword(passwordService.hashPassword("password"));
            portalUser.setRegType(RegType.REGISTRATION);

            if (dto.getStation() != null) {
                portalUser.setOffice(zonalOfficeRepository.findByName(dto.getStation()));
            }
            if (dto.getMla_id() != null) {
                portalUser.setRole(roleRepository.findByNameIgnoreCase("mla").orElseThrow(RuntimeException::new));
            } else {
                if (dto.getName().equalsIgnoreCase("chairman")) {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("chairman").orElseThrow(RuntimeException::new));
                } else if (dto.getName().equalsIgnoreCase("smr")) {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("smr").orElseThrow(RuntimeException::new));
                } else if  (dto.getName().equalsIgnoreCase("chairman2")) {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("chairman").orElseThrow(RuntimeException::new));
                } else if (dto.getName().equalsIgnoreCase("store")) {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("store").orElseThrow(RuntimeException::new));
                } else {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("data processor").orElseThrow(RuntimeException::new));
                }
            }

            portalUserRepository.save(portalUser);
            log.info("user created ========= with email ================ " + portalUser.getEmail() );
        }
        return ResponseEntity.ok("");
    }

    @PostMapping("/stock")
    @Transactional
    public ResponseEntity<?> transferStock(@RequestBody List<DataTransferStock> dtos){
        for (DataTransferStock dto: dtos){
            PlateNumberDto stock = new PlateNumberDto();
            stock.setStartCode(prefixRepository.findByCode(dto.getStartletters()) != null ? prefixRepository.findByCode(dto.getStartletters()).getId() : null);
            stock.setEndCode(dto.getAlphabets());
            stock.setFirstNumber(dto.getFirst_number());
            stock.setLastNumber(dto.getLast_number());
            stock.setType(plateNumberTypeRepository.findByName(dto.getPlate_number_type().toUpperCase()).getId());

            if (dto.getFirst_number() != null && stock.getStartCode() != null){
                plateNumberService.createStock(stock);
                log.info("============ Stock created ================");
            } else if (stock.getStartCode() == null){
                log.info("============ invalid startCode ================" + dto.getStartletters());
            } else {
                log.info("============ invalid stock ================" + dto.getFirst_number() +  dto.getLast_number() + dto.getStartletters() + dto.getAlphabets() + dto.getPlate_number_type());
            }
        }

        return ResponseEntity.ok("");
    }

    @PostMapping("/plate-assignment")
    @Transactional
    public ResponseEntity<?> transferAssigment(@RequestBody List<DataTransferAssign> dtos){
        for (DataTransferAssign  dto:dtos){
            PortalUser mla = portalUserRepository.findFirstByUsernameIgnoreCase(dto.getMla());
            PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(dto.getPlatenumber());
           if (mla != null && plateNumber != null){
               plateNumber.setAgent(mla);
               plateNumber.setPlateNumberStatus(PlateNumberStatus.ASSIGNED);
               plateNumber.setLastUpdatedBy(jwtService.user);
               plateNumberRepository.save(plateNumber);
               log.info("============ plate number assigned ================" + dto.getPlatenumber());
           } else if (mla == null){
               log.info("============ invalid user ================" + dto.getMla());

           } else {
               log.info("============ invalid plate ================" + dto.getPlatenumber());
           }
        }
        return ResponseEntity.ok("");
    }
}
