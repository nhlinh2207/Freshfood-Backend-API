package com.linh.freshfoodbackend.utils;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class ShorCodeUtils {

    public static List<String> analyzeShortCode(String shortCode){
        List<String> result = new ArrayList<>();
        shortCode = shortCode.substring(shortCode.indexOf("[") + 1, shortCode.indexOf("]"));
        String[] shortCodeComponents = shortCode.split(":");
        if (shortCodeComponents.length > 1){
            result.add(shortCodeComponents[0]);
            result.add(shortCodeComponents[1]);
            return result;
        }
        result.add(shortCodeComponents[0]);
        return result;
    }

    public String getAccessTokenFromResponse(String tokenField, Object response){
        String[] responseComponents = tokenField.split("\\.");
        JSONObject jsonObject = new JSONObject(new Gson().toJson(response));
        if (responseComponents.length == 1) {
            return jsonObject.getString(responseComponents[0]);
        }else{
            for (int i = 0 ; i < responseComponents.length - 1 ; i++){
                jsonObject = jsonObject.getJSONObject(responseComponents[i]);
            }
            return jsonObject.getString(responseComponents[responseComponents.length - 1]);
        }
    }

    public List<String> getShortCodesFromHTMLString(String htmlString){
//        String regex = "\\[[^\\]]+\\]";
        String regex = "\\[(lib|credential|api|baseurl|config|API|LIB|CREDENTIAL|BASEURL|CONFIG):[^\\]]+\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(htmlString);
        ArrayList<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    public String replaceLinks(String input){
        String regex = "(\\bhttp[s]?://\\S+\\b)";
        return input.replaceAll(regex, "");
    }

    public String replaceHTMLShortCode(String htmlString){
//        htmlString = replaceLinks(htmlString);
        if (htmlString == null)
            return htmlString;
        List<String> shortCodes = getShortCodesFromHTMLString(htmlString);
        if (shortCodes.size() == 0)
            return htmlString;
        for (String code : shortCodes){
            List<String> codeComponents = analyzeShortCode(code);
            if (codeComponents.get(0).equalsIgnoreCase("credential")){
                htmlString = htmlString.replace(code, "LINH-CREDENTIAL");
            }else if(codeComponents.get(0).equalsIgnoreCase("lib")){
                htmlString = htmlString.replace(code, "LINH-LIB");
            }else if(codeComponents.get(0).equalsIgnoreCase("baseurl")){
                htmlString = htmlString.replace(code, "LINH-BASEURL");
            }else if(codeComponents.get(0).equalsIgnoreCase("config")){
                htmlString = htmlString.replace(code, "LINH-CONFIG");
            }else{
                htmlString = htmlString.replace(code, "LINH-API");
            }
        }
        return htmlString;
    }


}
