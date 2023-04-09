package com.app.IVAS.nimc.others;

import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.entity.UserDemographicIndividual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PublicController {

    @Autowired
    PublicService publicService;

    @RequestMapping(value = "/informal/sector/{asin}")
    public UserDemographicIndividual getInformalSectioFromTax(@PathVariable String asin) {
        return publicService.getInformalSectioFromTax(asin);
    }

}
