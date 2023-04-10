package com.app.IVAS.nimc;

import com.app.IVAS.Utils.OkHttp3Util;
import com.app.IVAS.dto.Nimc.CreateTokenDto;
import com.app.IVAS.dto.Nimc.SearchWithNINDto;
import com.app.IVAS.errors.ApiError;
import com.app.IVAS.errors.binding_result.BindingResultException;
import com.app.IVAS.nin.search_by_nin.InnerFindByNINResponsePojo;
import com.app.IVAS.response.JsonResponse;
import com.app.IVAS.service.NinSearchService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NincBaseController {
    @Value("${nin.domain}")
    private String ninDomain;

    @Value("${username}")
    private String username;

    @Value("${password}")
    private String password;

    @Value("${hash.password}")
    private String hashPassword;

    @Value("${org.id}")
    private String orgId;

    @Autowired
    private NinSearchService ninSearchService;


    @PostMapping("/api/nin/nin")
    public ResponseEntity<?> SearchNin(@RequestBody String nin, BindingResult bindingResult){

        System.out.println("Reached for nin" + nin);
        ApiError apiError = null;

        if (bindingResult.hasErrors()) {
            return new BindingResultException().bindingErrorException(bindingResult);
        }

        try {

            System.out.println("Here to resolve the nin"+ nin);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
//            headers.add("Accept", "application/json");
//            headers.add("Content-Type", "application/json");

            CreateTokenDto createTokenDto = new CreateTokenDto();
            createTokenDto.setOrgId(orgId);
            createTokenDto.setUsername("MAkinyele");

            createTokenDto.setPassword(hashPassword);
            String result = restTemplate.postForObject(ninDomain+"/create-token", createTokenDto, String.class);

            System.out.println(result);



            Gson gson = new Gson();
            CreateTokenDto responseToken = gson.fromJson(result, CreateTokenDto.class);

            System.out.println("here is the response: " + responseToken.getToken());


            MultiValueMap<String, String> headersAuth = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap<String, String>();
            map.put("Content-Type", "application/json");
            map.put("Authorization", "Bearer "+responseToken.getToken());
            headersAuth.setAll(map);

            System.out.println(responseToken.getToken());

            SearchWithNINDto searchWithNINDto = new SearchWithNINDto();
            searchWithNINDto.setNin(nin);
            searchWithNINDto.setToken(responseToken.getToken());

            String json = gson.toJson(searchWithNINDto);
            String xmlString;
            OkHttp3Util okHttp3Util = new OkHttp3Util();
            xmlString = okHttp3Util.post(ninDomain+"/search-with-nin", json);


            InnerFindByNINResponsePojo innerFindByNINResponsePojo = ninSearchService.ninSearch(xmlString);

            System.out.println(innerFindByNINResponsePojo);

//            ResponseEntity<Object> responseRC = null;
//
//            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headersAuth);
//            try {
//
//                responseRC = restTemplate.exchange(builder.toUriString(),
//                        HttpMethod.GET,
//                        entity, Object.class);
//                System.out.println("Here is the response:::" + responseRC.getBody());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            return ResponseEntity.ok(new JsonResponse("message", responseToken.getToken()));
        }
        catch (Exception e) {
        }

        return null;
    }
}
