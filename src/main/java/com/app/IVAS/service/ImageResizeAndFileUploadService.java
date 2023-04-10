package com.app.IVAS.service;

import com.app.IVAS.Enum.DataStorageConstant;
import com.app.IVAS.dto.FileUploadDto;
import com.app.IVAS.dto.Nimc.ImageResolutionDto;
import com.app.IVAS.entity.FileModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import sun.awt.image.BufferedImageGraphicsConfig;
import com.app.IVAS.service.FileUploadService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@Service
public class ImageResizeAndFileUploadService {

    private final Logger logger = Logger.getLogger(InitializeFileUploadService.class.getSimpleName());

   private final FileUploadService fileUploadService;

    private final FileUploadToRemoteServerService fileUploadToRemoteServerService;

    @ImageResolutionSequence
    private final ImageResolutionSequenceService imageResolutionSequenceService;

    public ImageResizeAndFileUploadService(FileUploadService fileUploadService,
                                           ImageResolutionSequenceService imageResolutionSequenceService,
                                           @Qualifier(value = "cloudinary") FileUploadToRemoteServerService fileUploadToRemoteServerService) {

        this.fileUploadService = fileUploadService;
        this.imageResolutionSequenceService = imageResolutionSequenceService;

        this.fileUploadToRemoteServerService = fileUploadToRemoteServerService;
    }

    public FileModel fileUpload(File file,
                                String newFileLocation,
                                String format,
                                String parentImageCode,
                                DataStorageConstant dataStorageType,
                                String fileCode) {

        List<ImageResolutionDto> imageResolutionDtoListWithFile = new ArrayList<ImageResolutionDto>();

        try {

            FileUploadDto fileUploadDto = new FileUploadDto();

            //String fileCode = generateFileUploadSequence();

            FileModel fileModel = null;

            String outputFileLocation = "";

            outputFileLocation = newFileLocation + File.separator + fileCode + "." + format;

            InputStream initialStream = new FileInputStream(file);

            byte[] buffer = new byte[initialStream.available()];

            initialStream.read(buffer);

            File targetFile = new File(outputFileLocation);

            OutputStream outStream = new FileOutputStream(targetFile);

            outStream.write(buffer);

            fileUploadDto.setDataStorageConstant(fileUploadToRemoteServerService.getName());

            outputFileLocation = fileUploadToRemoteServerService.upload(targetFile);

            fileUploadDto.setFileOutputLocation(outputFileLocation);

            fileUploadDto.setCode(fileCode);

            fileUploadDto.setImageResolutionDtos(imageResolutionDtoListWithFile);

            fileModel = fileUploadService.save(fileUploadDto, parentImageCode);

            return fileModel;

        } catch (Exception e) {
            logger.info(e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public FileModel resizeAndUpload(File file,
                                     String newFileLocation,
                                     String format,
                                     String parentImageCode,
                                     String dataStorageType,
                                     List<ImageResolutionDto> requestImageResolutionDtoList,
                                     String fileCode) {

        BufferedImage image = null;
        List<ImageResolutionDto> imageResolutionDtoListWithFile = new ArrayList<ImageResolutionDto>();
        List<ImageResolutionDto> imageResolutionDtoList = new ArrayList<ImageResolutionDto>();

        try {

            image = ImageIO.read(file);
            logger.info("original height: " + image.getHeight());
            logger.info("original width: " + image.getWidth());


            for (ImageResolutionDto dto : requestImageResolutionDtoList) {

                if (dto.getWidth() != 0 && dto.getHeight() != 0) {
                    dto.setHeight(this.calculateHeight(image.getWidth(), image.getHeight(), dto.getWidth()));
                } else if (dto.getWidth() == 0) {
                    dto.setWidth(this.calculateWidth(image.getWidth(), image.getHeight(), dto.getHeight()));
                } else {
                    dto.setHeight(this.calculateHeight(image.getWidth(), image.getHeight(), dto.getWidth()));
                }

                ImageResolutionDto imageResolutionDto = new ImageResolutionDto();
                imageResolutionDto.setWidth(dto.getWidth());
                imageResolutionDto.setHeight(dto.getHeight());
                imageResolutionDtoList.add(imageResolutionDto);
            }


            ImageResolutionDto imageResolutionDto = new ImageResolutionDto();
            imageResolutionDto.setOriginal(true);
            imageResolutionDto.setHeight(image.getHeight());
            imageResolutionDto.setWidth(image.getWidth());
            imageResolutionDtoList.add(imageResolutionDto);

            try {

                for (ImageResolutionDto resolutionDto : imageResolutionDtoList) {

                    String code = generateImageResolutionSequence();

                    logger.info("width: " + resolutionDto.getWidth());

                    resolutionDto.setCode(code);

                    String newlyCreatedFileLocation = newFileLocation + File.separator + code + "." + format;

                    //ImageIO.write(resizeTrick(image, width, height), format, new File(newFileLocation));

                    File convertedToImageFile = null;

                    InputStream targetStream = new FileInputStream(file);

                    if (resolutionDto.getWidth() <= 200) {

                        BufferedImage croppedImage = cropImageSquare(targetStream);

                        // InputStream newInputStream = new ByteArrayInputStream(os.toByteArray());

                        resolutionDto.setDataStorageConstant(DataStorageConstant.DEFAULT_SERVER);

                        resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);

                        convertedToImageFile = new File(newlyCreatedFileLocation);

                        ImageIO.write(croppedImage, format, convertedToImageFile);

                    } else if (resolutionDto.getWidth() != 0 || resolutionDto.getHeight() != 0) {

                        InputStream inputStream = resizeImage(targetStream,
                                resolutionDto.getWidth(),
                                resolutionDto.getHeight(),
                                format);

                        BufferedImage bufferedImage = ImageIO.read(inputStream);

                        resolutionDto.setBufferedImage(bufferedImage);

                        resolutionDto.setDataStorageConstant(this.fileUploadToRemoteServerService.getName());

                        resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);

                        convertedToImageFile = new File(newlyCreatedFileLocation);

                        ImageIO.write(bufferedImage, format, convertedToImageFile);

                    } else {

                        resolutionDto.setBufferedImage(image);

                        resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);

                        convertedToImageFile = new File(newlyCreatedFileLocation);

                        ImageIO.write(image, format, convertedToImageFile);
                    }

                    resolutionDto.setFileOutputLocation(this.fileUploadToRemoteServerService.upload(convertedToImageFile));

                    //resolutionDto.setFileOutputLocation(newFileLocation);
                    imageResolutionDtoListWithFile.add(resolutionDto);

                }

                FileUploadDto fileUploadDto = new FileUploadDto();
                fileUploadDto.setCode(fileCode);
                fileUploadDto.setFileOutputLocation(newFileLocation);
                fileUploadDto.setImageResolutionDtos(imageResolutionDtoListWithFile);
                return fileUploadService.save(fileUploadDto, parentImageCode);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } catch (IOException e) {
            logger.info(e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public FileModel resizeAndUploadPreCroppedImage(
            File croppedFile,
            File file,
            String newFileLocation,
            String format,
            String parentImageCode,
            String dataStorageType,
            List<ImageResolutionDto> requestImageResolutionDtoList, String fileCode) {

        BufferedImage image = null;
        BufferedImage croppedImage = null;
        List<ImageResolutionDto> imageResolutionDtoListWithFile = new ArrayList<ImageResolutionDto>();
        List<ImageResolutionDto> imageResolutionDtoList = new ArrayList<ImageResolutionDto>();
        // String fileCode = generateFileUploadSequence();

        try {

            croppedImage = ImageIO.read(croppedFile);

            image = ImageIO.read(file);

            for (ImageResolutionDto dto : requestImageResolutionDtoList) {

                if (dto.getWidth() != 0 && dto.getHeight() != 0) {
                    dto.setHeight(this.calculateHeight(image.getWidth(), image.getHeight(), dto.getWidth()));
                } else if (dto.getWidth() == 0) {
                    dto.setWidth(this.calculateWidth(image.getWidth(), image.getHeight(), dto.getHeight()));
                } else {
                    dto.setHeight(this.calculateHeight(image.getWidth(), image.getHeight(), dto.getWidth()));
                }

                ImageResolutionDto imageResolutionDto = new ImageResolutionDto();
                imageResolutionDto.setWidth(dto.getWidth());
                imageResolutionDto.setHeight(dto.getHeight());
                imageResolutionDtoList.add(imageResolutionDto);
            }


            ImageResolutionDto imageResolutionDto = new ImageResolutionDto();
            imageResolutionDto.setOriginal(true);
            imageResolutionDto.setHeight(image.getHeight());
            imageResolutionDto.setWidth(image.getWidth());
            imageResolutionDtoList.add(imageResolutionDto);

            try {

                for (ImageResolutionDto resolutionDto : imageResolutionDtoList) {

                    String code = generateImageResolutionSequence();

                    resolutionDto.setCode(code);

                    String newlyCreatedFileLocation = newFileLocation + File.separator + code + "." + format;

                    File convertedToImageFile = null;

                    InputStream targetStream = new FileInputStream(file);

                    if (resolutionDto.getWidth() <= 200) {

                        resolutionDto.setDataStorageConstant(this.fileUploadToRemoteServerService.getName());

                        resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);

                        convertedToImageFile = new File(newlyCreatedFileLocation);

                        BufferedImage rgbImage = new BufferedImage(croppedImage.getWidth(), croppedImage.getHeight(),
                                BufferedImage.TYPE_3BYTE_BGR);

                        ColorConvertOp op = new ColorConvertOp(null);

                        op.filter(croppedImage, rgbImage);

                        ImageIO.write(rgbImage, format, convertedToImageFile);

                    } else if (resolutionDto.getWidth() != 0 || resolutionDto.getHeight() != 0) {

                        InputStream inputStream = resizeImage(targetStream,
                                resolutionDto.getWidth(),
                                resolutionDto.getHeight(),
                                format);

                        BufferedImage bufferedImage = ImageIO.read(inputStream);

                        resolutionDto.setBufferedImage(bufferedImage);

                        resolutionDto.setDataStorageConstant(this.fileUploadToRemoteServerService.getName());

                        resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);

                        convertedToImageFile = new File(newlyCreatedFileLocation);

                        ImageIO.write(bufferedImage, format, convertedToImageFile);

                    } else {

                        resolutionDto.setBufferedImage(image);

                        resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);

                        convertedToImageFile = new File(newlyCreatedFileLocation);

                        ImageIO.write(image, format, convertedToImageFile);
                    }

                    fileUploadToRemoteServerService.upload(convertedToImageFile);
                    //resolutionDto.setFileOutputLocation(newFileLocation);
                    imageResolutionDtoListWithFile.add(resolutionDto);
                }


                FileUploadDto fileUploadDto = new FileUploadDto();
                fileUploadDto.setCode(fileCode);
                fileUploadDto.setFileOutputLocation(newFileLocation);
                fileUploadDto.setImageResolutionDtos(imageResolutionDtoListWithFile);
                return fileUploadService.save(fileUploadDto, parentImageCode);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } catch (IOException e) {
            logger.info(e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }


//    public FileModel customResizeAndUpload(String fileLocation,
//                                           String newFileLocation,
//                                           String format,
//                                           String parentImageCode,
//                                           String dataStorageType,
//                                           List<ImageResolutionDto> requestImageResolutionDtoList,
//                                           String fileCode) {
//
//        BufferedImage image = null;
//        List<ImageResolutionDto> imageResolutionDtoListWithFile = new ArrayList();
//        List<ImageResolutionDto> imageResolutionDtoList = new ArrayList();
//        // String fileCode = generateFileUploadSequence();
//
//        try {
//
//            InputStream in = getClass().getResourceAsStream(fileLocation);
//
//            image = ImageIO.read(in);
//            //logger.info("original height: " + image.getHeight());
//            //logger.info("original width: " + image.getWidth());
//
//
//            for (int i = 0; i < requestImageResolutionDtoList.size(); i++) {
//
//                if (requestImageResolutionDtoList.get(i).getWidth() != 0 && requestImageResolutionDtoList.get(i).getHeight() != 0) {
//                    requestImageResolutionDtoList.get(i).setHeight(this.calculateHeight(image.getWidth(), image.getHeight(), requestImageResolutionDtoList.get(i).getWidth()));
//                } else if (requestImageResolutionDtoList.get(i).getWidth() == 0) {
//                    requestImageResolutionDtoList.get(i).setWidth(this.calculateWidth(image.getWidth(), image.getHeight(), requestImageResolutionDtoList.get(i).getHeight()));
//                } else {
//                    requestImageResolutionDtoList.get(i).setHeight(this.calculateHeight(image.getWidth(), image.getHeight(), requestImageResolutionDtoList.get(i).getWidth()));
//                }
//
//                ImageResolutionDto imageResolutionDto = new ImageResolutionDto();
//                imageResolutionDto.setWidth(requestImageResolutionDtoList.get(i).getWidth());
//                imageResolutionDto.setHeight(requestImageResolutionDtoList.get(i).getHeight());
//                imageResolutionDtoList.add(imageResolutionDto);
//
//            }
//
//
//            ImageResolutionDto imageResolutionDto = new ImageResolutionDto();
//            imageResolutionDto.setOriginal(true);
//            imageResolutionDto.setHeight(image.getHeight());
//            imageResolutionDto.setWidth(image.getWidth());
//            imageResolutionDtoList.add(imageResolutionDto);
//
//            try {
//
//                for (ImageResolutionDto resolutionDto : imageResolutionDtoList) {
//
//                    // resolutionDto.setCode();
//
//                    String code = generateImageResolutionSequence();
//
//                    //logger.info("width: " + resolutionDto.getWidth());
//
//                    resolutionDto.setCode(code);
//
//                    String newlyCreatedFileLocation = newFileLocation + File.separator + code + "." + format;
//                    //ImageIO.write(resizeTrick(image, width, height), format, new File(newFileLocation));
//
//                    File convertedToImageFile = null;
//
//                    if (DataStorageConstant.DEFAULT_SERVER.equals(DataStorageConstant.valueOf(dataStorageType))) {
//
//                        if (resolutionDto.getWidth() != 0 || resolutionDto.getHeight() != 0) {
//                            BufferedImage resizeImageHintJpg = resizeImageWithHint(image, 1, resolutionDto.getWidth(), resolutionDto.getHeight());
//                            resolutionDto.setBufferedImage(resizeImageHintJpg);
//                            resolutionDto.setDataStorageConstant(DataStorageConstant.DEFAULT_SERVER);
//                            resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);
//                            convertedToImageFile = new File(newlyCreatedFileLocation);
//                            //ImageIO.write(resizeImageHintJpg, format, convertedToImageFile);
//
//                        } else {
//                            resolutionDto.setBufferedImage(image);
//                            resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);
//                            convertedToImageFile = new File(newlyCreatedFileLocation);
//                            //ImageIO.write(image, format, convertedToImageFile);
//                        }
//
//                    } else if (DataStorageConstant.DROP_BOX.equals(DataStorageConstant.valueOf(dataStorageType))) {
//
//
//                        File file = new ClassPathResource(fileLocation).getFile();
//                        InputStream targetStream = new FileInputStream(file);
//                        InputStream outputStream = null;
//
//                        if (resolutionDto.getWidth() == 200) {
//
//                            BufferedImage croppedImage = cropImageSquare(targetStream);
//                            ByteArrayOutputStream os = new ByteArrayOutputStream();
//                            ImageIO.write(croppedImage, format, os);
//                            InputStream newInputStream = new ByteArrayInputStream(os.toByteArray());
//
//                            outputStream = resizeImage(newInputStream,
//                                    resolutionDto.getWidth(),
//                                    resolutionDto.getHeight(),
//                                    format);
//
//                            resolutionDto.setDataStorageConstant(DataStorageConstant.DROP_BOX);
//                            resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);
//
//                        } else if (resolutionDto.getWidth() != 0 || resolutionDto.getHeight() != 0) {
//
//                            resolutionDto.setDataStorageConstant(DataStorageConstant.DROP_BOX);
//                            resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);
//
//                            logger.info(format);
//                            outputStream = resizeImage(targetStream,
//                                    resolutionDto.getWidth(),
//                                    resolutionDto.getHeight(),
//                                    format);
//
//                            //resolutionDto.setBufferedImage(resizeImageHintJpg);
//
//                            //ImageIO.write(resizeImageHintJpg, format, convertedToImageFile);
//
//                        } else {
//
//                           // logger.info(format);
//
//                            resolutionDto.setBufferedImage(image);
//                            resolutionDto.setFileOutputLocation(newlyCreatedFileLocation);
//
//                            outputStream = resizeImage(in,
//                                    resolutionDto.getWidth(),
//                                    resolutionDto.getHeight(),
//                                    format);
//                        }
//
//                        String data = null;
//
//                        try {
//                            data = dropboxService.apiFileUpload(code + "." + format, outputStream, accessToken);
//                        } catch (DbxException e) {
//                            e.printStackTrace();
//                        }
//                        resolutionDto.setImageType(format);
//                        resolutionDto.setFileOutputLocation(data);
//                    }
//
//                    //resolutionDto.setFileOutputLocation(newFileLocation);
//                    imageResolutionDtoListWithFile.add(resolutionDto);
//                }
//
//
//                FileUploadDto fileUploadDto = new FileUploadDto();
//                fileUploadDto.setCode(fileCode);
//                fileUploadDto.setFileOutputLocation(newFileLocation);
//                fileUploadDto.setImageResolutionDtos(imageResolutionDtoListWithFile);
//                return fileUploadService.save(fileUploadDto, parentImageCode);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//
//        } catch (Exception e) {
//            logger.info(e.getLocalizedMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static BufferedImage blurImage(BufferedImage image) {
        float ninth = 1.0f / 9.0f;
        float[] blurKernel = {
                ninth, ninth, ninth,
                ninth, ninth, ninth,
                ninth, ninth, ninth
        };

        Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
        map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RenderingHints hints = new RenderingHints(map);
        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
        return op.filter(image, null);
    }

    private static BufferedImage createCompatibleImage(BufferedImage image) {
        GraphicsConfiguration gc = BufferedImageGraphicsConfig.getConfig(image);
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage result = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage(image, null);
        g2.dispose();
        return result;
    }

    private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type, int width, int height) {

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

    private int calculateWidth(int originalWidth, int originalHeight, int newHeight) {
        try {
            return (newHeight * originalWidth) / originalHeight;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int calculateHeight(int originalWidth, int originalHeight, int newWidth) {
        try {
            return (newWidth * originalHeight) / originalWidth;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static InputStream resizeImage(InputStream inputStream, int width, int height, String formatName) throws IOException {
        BufferedImage sourceImage = ImageIO.read(inputStream);
        Image thumbnail = sourceImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedThumbnail = new BufferedImage(thumbnail.getWidth(null),
                thumbnail.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedThumbnail, formatName, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private BufferedImage cropImageSquare(InputStream in) throws IOException {


        BufferedImage originalImage = ImageIO.read(in);

        int newSquareSize = 0;

        // Get image dimensions
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();

        // The image is already a square
        if (height == width) {
            return originalImage;
        }

        // Compute the size of the square
        int squareSize = (height > width ? width : height);

        if (squareSize > 200) {
            newSquareSize = 200;
        }
        // Coordinates of the image's middle
        int xc = width / 2;
        int yc = height / 2;

        // Crop
        BufferedImage croppedImage = originalImage.getSubimage(
                0, 0,
                squareSize,            // widht
                squareSize             // height
        );

        return croppedImage;
    }


    private String generateImageResolutionSequence() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return String.format("IMG_%04d%02d%02d%07d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                timestamp.getTime(),
                imageResolutionSequenceService.getNextId()
        );
    }

}
