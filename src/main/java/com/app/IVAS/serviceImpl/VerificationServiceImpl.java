package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.InvoiceServiceTypeDto;
import com.app.IVAS.dto.VerificationDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final InvoiceRepository invoiceRepository;
    private final PortalUserRepository portalUserRepository;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;
    private final VehicleRepository vehicleRepository;
    private final PlateNumberRepository plateNumberRepository;

    @Override
    public VerificationDto getScannedInvoice(String invoiceNumber) {

        VerificationDto verificationDto = new VerificationDto();

        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber);
        if (invoice.isPresent()) {

            if (invoice.get().getVehicle() != null) {

                Optional<Vehicle> vehicle = vehicleRepository.findById(invoice.get().getVehicle().getId());

                verificationDto.setInvoiceNumber(invoice.get().getInvoiceNumber());
                verificationDto.setPaymentDate(invoice.get().getPaymentDate());
                verificationDto.setAmount(invoice.get().getAmount());
                verificationDto.setPaymentStatus(invoice.get().getPaymentStatus().name());
                if (invoice.get().getPaymentDate() != null) {
                    verificationDto.setExpiryDate(invoice.get().getPaymentDate().plusYears(1).minusDays(1));
                }


                PortalUser portalUser = portalUserRepository.findById(invoice.get().getPayer().getId()).get();

                if (portalUser.getDisplayName() != null) {
                    verificationDto.setName(portalUser.getDisplayName());
                }

                if (portalUser.getPhoneNumber() != null) {
                    verificationDto.setPhoneNumber(portalUser.getPhoneNumber());
                }

                if (portalUser.getAddress() != null) {
                    verificationDto.setAddress(portalUser.getAddress());
                }

                if (portalUser.getEmail() != null) {
                    verificationDto.setEmail(portalUser.getEmail());
                }

                if (portalUser.getAsin() != null) {
                    verificationDto.setAsin(portalUser.getAsin());
                }

                if (vehicle.isPresent()) {
                    if (vehicle.get().getChasisNumber() != null) {
                        verificationDto.setChasis(vehicle.get().getChasisNumber());
                    }
                    if (vehicle.get().getEngineNumber() != null) {
                        verificationDto.setEngine(vehicle.get().getEngineNumber());
                    }
                    if (vehicle.get().getVehicleCategory() != null) {
                        verificationDto.setCategory(vehicle.get().getVehicleCategory().getName());
                    }
                    if (vehicle.get().getVehicleCategory().getWeight() != null) {
                        verificationDto.setWeight(vehicle.get().getVehicleCategory().getWeight());
                    }
                    if (vehicle.get().getVehicleModel() != null) {
                        verificationDto.setModel(vehicle.get().getVehicleModel().getName());
                    }

                    if (vehicle.get().getVehicleModel().getVehicleMake().getName() != null) {
                        verificationDto.setMake(vehicle.get().getVehicleModel().getVehicleMake().getName());
                    }

                    if (vehicle.get().getVehicleModel().getYear() != null) {
                        verificationDto.setYear(vehicle.get().getVehicleModel().getYear());
                    }

                    if (vehicle.get().getColor() != null) {
                        verificationDto.setColor(vehicle.get().getColor());
                    }

                    if (vehicle.get().getPlateNumber().getPlateNumber() != null) {
                        verificationDto.setPlate(vehicle.get().getPlateNumber().getPlateNumber().toUpperCase());
                    }

                }


                Optional<List<InvoiceServiceType>> invoiceServiceTypes = invoiceServiceTypeRepository.findAllByInvoice(invoice.get());

                List<InvoiceServiceTypeDto> invoiceServiceTypeDtos = new ArrayList<>();

                if (invoiceServiceTypes.isPresent()) {
                    for (InvoiceServiceType invoiceServiceType : invoiceServiceTypes.get()) {

                        InvoiceServiceTypeDto invoiceServiceTypeDto = new InvoiceServiceTypeDto();

                        if (invoiceServiceType.getServiceType().getName() != null) {
                            invoiceServiceTypeDto.setName(invoiceServiceType.getServiceType().getName());
                        }

                        if (invoiceServiceType.getServiceType().getPrice() != null) {
                            invoiceServiceTypeDto.setPrice(invoiceServiceType.getServiceType().getPrice());
                        }

                        if (invoiceServiceType.getServiceType().getDurationInMonth() != null) {
                            invoiceServiceTypeDto.setDurationInMonth(invoiceServiceType.getServiceType().getDurationInMonth());
                        }

                        if (invoiceServiceType.getServiceType().getCategory() != null) {
                            invoiceServiceTypeDto.setVehicleCategory(invoiceServiceType.getServiceType().getCategory().getName());
                        }

                        if (invoiceServiceType.getReference() != null) {
                            invoiceServiceTypeDto.setReference(invoiceServiceType.getReference());
                        }

                        if (invoiceServiceType.getPaymentDate() != null) {
                            invoiceServiceTypeDto.setPaymentDate(invoiceServiceType.getPaymentDate());
                        }

                        if (invoiceServiceType.getServiceType().getDurationInMonth() != null && invoiceServiceType.getPaymentDate() != null) {
                            invoiceServiceTypeDto.setExpiryDate(invoiceServiceType.getPaymentDate().plusMonths(invoiceServiceType.getServiceType().getDurationInMonth()));
                        }

                        invoiceServiceTypeDtos.add(invoiceServiceTypeDto);
                    }

                    verificationDto.setInvoiceServices(invoiceServiceTypeDtos);
                }

                return verificationDto;

            }
        }
        return null;
    }

    @Override
    public VerificationDto getInvoiceByPlateNumber(String plateNumber) {

        VerificationDto verificationDto = new VerificationDto();

        Optional<PlateNumber> plate = plateNumberRepository.findPlateNumberByPlateNumberIgnoreCase(plateNumber);

        if (plate.isPresent()) {

            Optional<Vehicle> vehicle = Optional.ofNullable(vehicleRepository.findVehicleByPlateNumberId(plate.get().getId()));

            if (vehicle.isPresent()) {

                if (vehicle.get().getChasisNumber() != null) {
                        verificationDto.setChasis(vehicle.get().getChasisNumber());
                    }
                    if (vehicle.get().getEngineNumber() != null) {
                        verificationDto.setEngine(vehicle.get().getEngineNumber());
                    }
                    if (vehicle.get().getVehicleCategory() != null) {
                        verificationDto.setCategory(vehicle.get().getVehicleCategory().getName());
                    }
                    if (vehicle.get().getVehicleCategory().getWeight() != null) {
                        verificationDto.setWeight(vehicle.get().getVehicleCategory().getWeight());
                    }
                    if (vehicle.get().getVehicleModel() != null) {
                        verificationDto.setModel(vehicle.get().getVehicleModel().getName());
                    }

                    if (vehicle.get().getVehicleModel().getVehicleMake().getName() != null) {
                        verificationDto.setMake(vehicle.get().getVehicleModel().getVehicleMake().getName());
                    }

                    if (vehicle.get().getVehicleModel().getYear() != null) {
                        verificationDto.setYear(vehicle.get().getVehicleModel().getYear());
                    }

                    if (vehicle.get().getColor() != null) {
                        verificationDto.setColor(vehicle.get().getColor());
                    }

                    if (vehicle.get().getPlateNumber().getPlateNumber() != null) {
                        verificationDto.setPlate(vehicle.get().getPlateNumber().getPlateNumber().toUpperCase());
                    }


                Optional<PortalUser> portalUser = portalUserRepository.findById(vehicle.get().getPortalUser().getId());

                if (portalUser.isPresent()) {

                    if (portalUser.get().getDisplayName() != null) {
                    verificationDto.setName(portalUser.get().getDisplayName());
                }

                if (portalUser.get().getPhoneNumber() != null) {
                    verificationDto.setPhoneNumber(portalUser.get().getPhoneNumber());
                }

                if (portalUser.get().getAddress() != null) {
                    verificationDto.setAddress(portalUser.get().getAddress());
                }

                if (portalUser.get().getEmail() != null) {
                    verificationDto.setEmail(portalUser.get().getEmail());
                }

                if (portalUser.get().getAsin() != null) {
                    verificationDto.setAsin(portalUser.get().getAsin());
                }

                    Optional<Invoice> invoice = Optional.ofNullable(invoiceRepository.findLastByVehicle(vehicle.get()));

                    if (invoice.isPresent()) {

                        verificationDto.setInvoiceNumber(invoice.get().getInvoiceNumber());
                        verificationDto.setPaymentDate(invoice.get().getPaymentDate());
                        verificationDto.setAmount(invoice.get().getAmount());
                        verificationDto.setPaymentStatus(invoice.get().getPaymentStatus().name());
                        if (invoice.get().getPaymentDate() != null) {
                            verificationDto.setExpiryDate(invoice.get().getPaymentDate().plusYears(1).minusDays(1));
                        }

                        Optional<List<InvoiceServiceType>> invoiceServiceTypes = invoiceServiceTypeRepository.findAllByInvoice(invoice.get());

                        if (invoiceServiceTypes.isPresent()) {

                            List<InvoiceServiceTypeDto> invoiceServiceTypeDtos = new ArrayList<>();

                            for (InvoiceServiceType invoiceServiceType : invoiceServiceTypes.get()) {

                                InvoiceServiceTypeDto invoiceServiceTypeDto = new InvoiceServiceTypeDto();

                                if (invoiceServiceType.getServiceType().getName() != null) {
                                    invoiceServiceTypeDto.setName(invoiceServiceType.getServiceType().getName());
                                }

                                if (invoiceServiceType.getServiceType().getPrice() != null) {
                                    invoiceServiceTypeDto.setPrice(invoiceServiceType.getServiceType().getPrice());
                                }

                                if (invoiceServiceType.getServiceType().getDurationInMonth() != null) {
                                    invoiceServiceTypeDto.setDurationInMonth(invoiceServiceType.getServiceType().getDurationInMonth());
                                }

                                if (invoiceServiceType.getServiceType().getCategory() != null) {
                                    invoiceServiceTypeDto.setVehicleCategory(invoiceServiceType.getServiceType().getCategory().getName());
                                }

                                if (invoiceServiceType.getReference() != null) {
                                    invoiceServiceTypeDto.setReference(invoiceServiceType.getReference());
                                }

                                if (invoiceServiceType.getPaymentDate() != null) {
                                    invoiceServiceTypeDto.setPaymentDate(invoiceServiceType.getPaymentDate());
                                }

                                if (invoiceServiceType.getServiceType().getDurationInMonth() != null && invoiceServiceType.getPaymentDate() != null) {
                                    invoiceServiceTypeDto.setExpiryDate(invoiceServiceType.getPaymentDate().plusMonths(invoiceServiceType.getServiceType().getDurationInMonth()));
                                }

                                invoiceServiceTypeDtos.add(invoiceServiceTypeDto);
                            }


                            verificationDto.setInvoiceServices(invoiceServiceTypeDtos);

                        }


                    }


                }


            }

            return verificationDto;

        }

        return null;
    }
}
