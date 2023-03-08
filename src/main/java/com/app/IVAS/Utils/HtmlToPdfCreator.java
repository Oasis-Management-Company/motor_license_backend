package com.app.IVAS.Utils;

import com.itextpdf.text.DocumentException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author
 */


@Component
@Data
@RequiredArgsConstructor
public class HtmlToPdfCreator {


    private final ServletContext servletContext;


    private final TemplateEngine templateEngine;

    @Autowired
    ResourceLoader resourceLoader;

    private Boolean scaleToFit = Boolean.FALSE;

    /**
     * @param filename
     * @return
     */
    public static String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * @param originalFilename
     * @return
     */
    public static String generateUploadToFilename(String originalFilename) {
        Date date = new Date();
        String timeStamp = date.toString().replace(" ", "_").replace(":", "_");
        String extension = getFileExtension(originalFilename);
        String name = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        if (name.length() > 80) {
            name = name.substring(0, 80);
        }
        return name + "_" + timeStamp + "." + extension;
    }

    /**
     * @param parameters
     * @param extraParameters
     * @return
     * @throws IllegalAccessException
     */
    public Context getContext(Object parameters, Map<String, Object> extraParameters) throws IllegalArgumentException, IllegalAccessException {
        Context context = new Context();
        Field[] fields = parameters.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            if (Modifier.isPublic(field.getModifiers())) {
                Object value = field.get(parameters);
                context.setVariable(name, value);
            } else {
                field.setAccessible(true);
                Object value = field.get(parameters);
                context.setVariable(name, value);
            }
        }

        if (extraParameters != null && !extraParameters.isEmpty()) {
            Set<String> keys = extraParameters.keySet();
            keys.forEach((field) -> {
                context.setVariable(field, extraParameters.get(field));
            });
        }
        return context;
    }

    /**
     * @param templateName
     * @param parameters
     * @param extraParameters
     * @return
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws MalformedURLException
     * @throws IOException
     * @throws DocumentException
     */
    public ITextRenderer createPDF(String templateName, Object parameters, Map<String, Object> extraParameters)
            throws UnsupportedEncodingException,
            IllegalArgumentException,
            IllegalAccessException,
            MalformedURLException,
            IOException,
            DocumentException {
        return createPDF(templateName, getContext(parameters, extraParameters));
    }

    /**
     * @param templateName
     * @param parameters
     * @return
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws MalformedURLException
     * @throws IOException
     * @throws DocumentException
     */
    public ITextRenderer createPDF(String templateName, Object parameters)
            throws UnsupportedEncodingException,
            IllegalArgumentException,
            IllegalAccessException,
            MalformedURLException,
            IOException,
            DocumentException {
        return createPDF(templateName, parameters, null);
    }

    /**
     * @param templateName
     * @param context
     * @return
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws MalformedURLException
     * @throws IOException
     * @throws DocumentException
     */
    public ITextRenderer createPDF(String templateName, Context context)
            throws UnsupportedEncodingException,
            IllegalArgumentException,
            IllegalAccessException,
            MalformedURLException,
            IOException,
            DocumentException {
        String htmlContentToRender = templateEngine.process(templateName, context);
        String xHtml = xhtmlConvert(htmlContentToRender);

        ITextRenderer renderer = new ITextRenderer();
        String baseUrl = resourceLoader.getResource("classpath:templates/").getURL().toString();
        if (scaleToFit) {
            renderer.setScaleToFit(true);
        }
        
        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();
        return renderer;
    }

    /**
     * @param html
     * @return
     * @throws UnsupportedEncodingException
     */
    public String xhtmlConvert(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString("UTF-8");
    }

    public String createPDFString(String templateName, Context context)
            throws UnsupportedEncodingException,
            IllegalArgumentException,
            IllegalAccessException,
            MalformedURLException,
            IOException,
            DocumentException {
        String htmlContentToRender = templateEngine.process(templateName, context);

        return xhtmlConvert(htmlContentToRender);
    }

}
