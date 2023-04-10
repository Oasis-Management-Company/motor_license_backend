package com.app.IVAS.service.cloud;

import com.app.IVAS.Enum.DataStorageConstant;
import com.app.IVAS.service.FileUploadToRemoteServerService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;


@Service(value = "cloudinary")
public class CloudinaryService implements FileUploadToRemoteServerService {

    private final Logger logger = Logger.getLogger(CloudinaryService.class.getName());

    private Cloudinary cloudinary;

    private final Gson gson;

    @Value(value = "${cloud_name}")
    private String cloudName;

    @Value(value = "${api_secret}")
    private String apiSecret;

    @Value(value = "${api_key}")
    private String apiKey;

    public CloudinaryService(Gson gson) {
        this.gson = gson;
    }

    private Cloudinary init() {
        return new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret)
        );
    }


    @Override
    public DataStorageConstant getName() {
        return DataStorageConstant.CLOUDINARY;
    }

    public String upload(File file) throws IOException {

        if(cloudinary == null) {
            this.cloudinary = this.init();
        }

        Map<String, Object> params = ObjectUtils.asMap(
                "public_id", "myfolder/mysubfolder/"+ file.getName(),
                "overwrite", true,
                "notification_url", "https://mysite.com/notify_endpoint",
                "resource_type", "image"
        );

        Map<String, String> uploadResult = (Map<String, String>) cloudinary.uploader().upload(file, params);
        return uploadResult.get("secure_url");
    }


}
