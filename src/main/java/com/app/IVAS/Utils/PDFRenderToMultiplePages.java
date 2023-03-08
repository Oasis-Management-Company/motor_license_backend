package com.app.IVAS.Utils;

import com.app.IVAS.configuration.AppConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * @author uhuegbulem chinomso
 * email: chimaisaac60@gmail.com
 * Oct, 2022
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFRenderToMultiplePages {

    private final AppConfigurationProperties appConfigurationProperties;

    public String multiPage(List<String> templates) throws Exception {
        String file = "";

        OutputStream os = null;
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy-hh-mm-ss");
            final File outputFile = new File(appConfigurationProperties.getPrintDirectory() + LocalDateTime.now().format(df) +".pdf");
            os = new FileOutputStream(outputFile);

            ITextRenderer renderer = new ITextRenderer();

            renderer.setDocumentFromString(templates.get(0));
            renderer.layout();
            renderer.createPDF(os, false);

            // each page after the first we add using layout() followed by writeNextDocument()
            for (int i = 1; i < templates.size(); i++) {
                renderer.setDocumentFromString(templates.get(i));
                renderer.layout();
                renderer.writeNextDocument();
            }

            // complete the PDF
            renderer.finishPDF();

            System.out.println("Sample file with " + templates.size() + " documents rendered as PDF to " + outputFile);

            file = outputFile.getName();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) { /*ignore*/ }
            }
        }

        return file;
    }


    public String createDirectory(Workbook workbook) throws IOException {
        String pathToFile = "";
        String today = LocalDate.now().toString();
        String fileSeparator = File.separator;
        String path = appConfigurationProperties.getPrintDirectory() + fileSeparator + "property_list" + today + fileSeparator;

        String fileName = path + fileSeparator + "Properties" + System.currentTimeMillis() + ".xlsx";
        DataOutputStream dataOutputStream = null;
        try {
            Path filePath = Paths.get(fileName);
            try {
                filePath.toFile().setExecutable(true, false);
                filePath.toFile().setReadable(true, false);
                filePath.toFile().setWritable(true, false);
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            } catch (AccessDeniedException aes) {
                aes.printStackTrace();
            }

            dataOutputStream = new DataOutputStream(Files.newOutputStream(filePath,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            workbook.write(dataOutputStream);
            workbook.close();
            pathToFile = filePath.toFile().toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataOutputStream != null) {
                dataOutputStream.flush();
                dataOutputStream.close();
            }
        }

        return pathToFile;
    }

    public Resource loadFileAsResource(String fileName) {
        Path fileStorageLocation = Paths.get(fileName).normalize();
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException | FileNotFoundException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }

}

