package com.app.IVAS.Utils;


import com.google.gson.Gson;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utils {

    public static String removeWhiteSpace(String input) {
        return input.replaceAll("\\s+", "");
    }


    public static String removeSpecialCharacters(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", " ");
    }

    public static String removeAllExceptNumbers(String input) {
        return input.replaceAll("[^0-9]", " ");
    }


    public static Date formatDate(String inputDate, String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(inputDate);
    }


    public static String dateToStringFormat(Date inputDate, String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(inputDate);
    }

    public static boolean isValidPhoneNumber(String input) {
//        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
//
//        try {
//            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(input, "NG");
//            return phoneUtil.isValidNumber(swissNumberProto);
//        } catch (NumberParseException | NullPointerException e) {
//            return false;
//        }

       // return true;
        //(0/91): number starts with (0/91)
        //[7-9]: starting of the number may contain a digit between 0 to 9
        //[0-9]: then contains digits 0 to 9
//        Pattern pattern = Pattern.compile("(0/91)?[7-9][0-9]{9}");
//        //the matcher() method creates a matcher that will match the given input against this pattern
//        Matcher match = pattern.matcher(input);
//        //returns a boolean value
//        return (match.find() && match.group().equals(input));
        return true;
    }


    public static Long getDifferenceBetweenTwoDates(Date startDate) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        //3. Instant + system default time zone + toLocalDateTime() = LocalDateTime
        LocalDateTime localDateTime = startDate.toInstant().atZone(defaultZoneId).toLocalDateTime();

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, now);
        return duration.toHours();
    }


//    public static boolean isEmailValid(String email) {
//        return EmailValidator.getInstance(true).isValid(email.toLowerCase());
//    }
//
//
//    public static Date addTimeToDate(Date date, TimeType type, int timeToAdd) {
//
//        Calendar calendar = Calendar.getInstance();
//
//        calendar.setTime(date);
//
//        if (type.equals(TimeType.HOURS)) {
//            calendar.add(Calendar.HOUR, timeToAdd);
//        } else if (type.equals(TimeType.MINUTES)) {
//            calendar.add(Calendar.MINUTE, timeToAdd);
//        } else {
//            calendar.add(Calendar.SECOND, timeToAdd);
//        }
//
//        return calendar.getTime();
//    }


    public static int randomNumberGenerator(int min, int max) {
        // System.out.println("Random value of type int between "+min+" to "+max+ ":");
        return (int) (Math.random() * (max - min + 1) + min);
    }


    public static String removeDoubleQuoteAndIsNullCheck(String input) {
        // Remove double quote
        return input.replace("\"", "");
    }

    public static String removeUnderscore(String input) {
        // Remove double quote
        return input.replace("_", " ");
    }

    public static String[] reverseArr(String[] arr) {
        int size = arr.length;
        String[] newArr = new String[arr.length];
        int y = 0;
        for(int i = size - 1; i > -1; i--) {
            newArr[y] = arr[i];
            y++;
        }
        return newArr;
    }

    public static int getLastTowDigitsOfYear(int input) {
        String value = String.valueOf(input);
        return Integer.parseInt(value.substring(2, 4));
    }

    public static int getLastAsInt(int value, int numberOfCharacters, boolean doReverse) {
        String toString = String.valueOf(value);
        String[] arr = new String[toString.length()];
        int count = 0;
        while (count < toString.length()){
            arr[count] = String.valueOf(toString.charAt(count));
            count++;
        }

        if(doReverse) {
            arr = reverseArr(arr);
        }

        StringBuilder sb = new StringBuilder();
        count = 0;
        while (count < numberOfCharacters) {
            sb.append(arr[count]);
            count++;
        }
        return Integer.parseInt(sb.toString());
    }


    public static Long getLongFromString(String input) {
        try {
            return Long.valueOf(input);
        } catch (Exception e) {
            return 0L;
        }
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    public static String capitalize(String text) {
        String str = text.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String removeNewLineAndSpace(String base64) {
        base64 = base64.replace("\n", "")
                .replace("\r", "");
        base64 = base64.replaceAll(" ", "");
        base64 = base64.replaceAll("\\s+", "");
        return base64;
    }


    public static String truncateSentence(String sentence, int lengthOfSentence) {

        if(sentence == null || sentence.isEmpty()) {
            return "";
        }

        if(lengthOfSentence == 0) {
            lengthOfSentence = 40;
        }

        if(sentence.length() < lengthOfSentence) {
            return sentence;
        } else {
            return sentence.substring(0, lengthOfSentence) + "...";
        }
    }

    public static String removeFirstChar(String str){
        if (str.charAt(0) == '0'){
            return str.substring(1);
        }else if(str.contains("+234")){
            String result = str.substring(4);
            if (result.charAt(0) == '0'){
                return str.substring(1);
            }
            return result;
        }else{
            return str;
        }
    }

    public static void sendSms(String userNumber, String sector, String code){
        Twilio.init("AC932aa126e7abb759c35a15a388466348", "3f957f2e812d46035e99122e6d765fde");
        Message.creator(new PhoneNumber(userNumber), new PhoneNumber("+16815081808"), "Please use this code " + code + ", \n" +
                "to validate your " + sector + " Registration. \n " +
                "AIRS.").create();
    }

}
