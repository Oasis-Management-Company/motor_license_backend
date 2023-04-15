package com.app.IVAS.Utils;

import jdk.internal.org.objectweb.asm.TypeReference;
import org.codehaus.jackson.map.ObjectMapper;

import com.fasterxml.jackson.xml.XmlMapper;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonXmlConverter {

        private static final Pattern XML_TAG =
                Pattern.compile("(?m)(?s)(?i)(?<first><(/)?)(?<nonXml>.+?)(?<last>(/)?>)");

        private static final Pattern REMOVE_ILLEGAL_CHARS =
                Pattern.compile("(i?)([^\\s=\"'a-zA-Z0-9._-])|(xmlns=\"[^\"]*\")");

        private final ObjectMapper mapper = new ObjectMapper();

        private final XmlMapper xmlMapper = new XmlMapper();

        String convertToXml(Object obj) throws IOException {
            final String s = xmlMapper.writeValueAsString(obj);
            return removeIllegalXmlChars(s);
        }

        private String removeIllegalXmlChars(String s) {
            final Matcher matcher = XML_TAG.matcher(s);
            StringBuffer sb = new StringBuffer();
            while(matcher.find()) {
                String elementName = REMOVE_ILLEGAL_CHARS.matcher(matcher.group("nonXml"))
                        .replaceAll("").trim();
                matcher.appendReplacement(sb, "${first}" + elementName + "${last}");
            }
            matcher.appendTail(sb);
            return sb.toString();
        }

//        Map<String, Object> convertJson(String json) throws IOException {
//            return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
//        }
//
//        public String convertJsonToXml(String json) throws IOException {
//            return convertToXml(convertJson(json));
//        }
}
