package com.app.IVAS.nimc.others;

import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.entity.UserDemographicIndividual;

public interface PublicService {
    UserDemographicIndividual getInformalSectioFromTax(String asin);
}
