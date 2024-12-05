package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.dto.mapper.ProductMapper;
import com.linh.freshfoodbackend.dto.request.SearchRequest;
import com.linh.freshfoodbackend.dto.sqlSearchDto.CustomRsqlVisitor;
import com.linh.freshfoodbackend.entity.Product;
import com.linh.freshfoodbackend.exception.UnSuccessException;
import com.linh.freshfoodbackend.repository.IProductRepo;
import com.linh.freshfoodbackend.utils.RestTemplateUtil;
import com.linh.freshfoodbackend.utils.ShorCodeUtils;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.io.BufferedReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

@RestController
@RequestMapping(path = "/test")
@Slf4j
@AllArgsConstructor
public class TestController {

    private final IProductRepo productRepo;
    private final ShorCodeUtils shorCodeUtils;
    private static final String urlBase = "https://data-gw.vncare.vn/";

    @GetMapping(path = "/emch")
    @Cacheable(value = "emchCache", key = "#id", unless = "#result == null")
    public String loginemch(@RequestParam("id") Long id){
        try{
            log.info("Login emch  ->>> okokok");
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("tenDangNhap", "02929");
            requestBody.add("matKhau", "MTIzNEBBYmNk");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");
            HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> responseEntity = restTemplate.postForEntity("https://skbmte-qa.orenda.vn/api/lien-thong/tai-khoan/dang-nhap", requestEntity, Map.class);
            Map<String, String> responseBody = responseEntity.getBody();
            return responseBody.get("accessToken");
        }catch (Exception e){
            e.printStackTrace();
            throw new UnSuccessException("Login EMCH failed !");
        }
    }

//    Tìm kiếm : {{product}}/test/search?page=0&size=10&filter=createTime>="14/11/2023 00:00:00";createTime<="12/05/2024 23:59:59";name=="*wi*"&sort=id,desc
    @GetMapping(path = "/search")
    public ResponseEntity<?> searchProduct(@Valid SearchRequest req){
        Node rootNode = new RSQLParser().parse(req.getFilter());
        Specification<Product> spec = rootNode.accept(new CustomRsqlVisitor<>());
        log.info("search: {}", req.getFilter());

        // Create Page
        String[] sortList = req.getSort().split(",");
        Sort.Direction direction = sortList[1].equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = req.getSize() != null ? PageRequest.of(req.getPage(), req.getSize(), direction, sortList[0]) : Pageable.unpaged();

        Page<Product> productPage = productRepo.findAll(spec, pageable);
        return ResponseEntity.ok(productPage.map(ProductMapper::mapEntityToDto));
    }

    @PostMapping(path = "/l2")
    public ResponseEntity<?> tsstEntity(@RequestBody String jsonObject, @RequestParam String token) throws IOException {
        String output;
        String rs = "";
        URL url = new URL(urlBase + "api-gateway/v2/lienthong-all");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        OutputStream os = conn.getOutputStream();
        os.write(jsonObject.getBytes());
        os.flush();
        if (conn.getResponseCode() == 200 || conn.getResponseCode() == 500  ) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            output = br.readLine();
//            JSONObject JsonObj = new JSONObject(output);
//            rs = JsonObj.toString();
            rs = output;
        } else {
            rs = "-1";
        }
        System.out.println(rs);
        conn.disconnect();
        return ResponseEntity.ok(rs);
    }

    @PostMapping(path = "/linh")
    public ResponseEntity<?> login(@RequestBody String input) throws NoSuchAlgorithmException, KeyManagementException, TemplateException, IOException {

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.add("Content-Type", "application/json; charset=utf-8");
        HttpEntity<String> dataRequest = new HttpEntity<>(input, loginHeaders);
        RestTemplate restTemplate = new RestTemplate(factory);
//        ResponseEntity<Object> responseEntity = restTemplate.exchange(
//            "https://haiduongapi.congdulieuyte.vn/hoc-user/api/authenticate",
//            HttpMethod.POST, dataRequest,
//            Object.class);
//        System.out.println(responseEntity.getBody());

        String o = "const paramsURL = new URLSearchParams(window.location.search);\n" +
                "\n" +
                "let color = {\n" +
                "    colorChart: [\n" +
                "        \"#6BA8E5\",\n" +
                "        \"#3E7CB1\",\n" +
                "        \"#1E506E\",\n" +
                "        \"#79C36D\",\n" +
                "        \"#528B4E\",\n" +
                "        \"#2E5A30\",\n" +
                "        \"#FFB74D\",\n" +
                "        \"#FFA726\",\n" +
                "        \"#D9822B\",\n" +
                "        \"#FF8A80\",\n" +
                "        \"#FF6659\",\n" +
                "        \"#D63C3A\",\n" +
                "        \"#B39DDB\",\n" +
                "        \"#8561C5\",\n" +
                "        \"#553E83\",\n" +
                "        \"#FFD54F\",\n" +
                "        \"#FFC107\",\n" +
                "        \"#D98F0A\",\n" +
                "        \"#66ADA3\",\n" +
                "        \"#3A8379\",\n" +
                "        \"#20584D\",\n" +
                "        \"#F48FB1\",\n" +
                "        \"#F06292\",\n" +
                "        \"#C7386D\",\n" +
                "        \"#B0BEC5\",\n" +
                "        \"#78909C\"\n" +
                "    ],\n" +
                "    colorBackgroudChart: '#f0f0f0',\n" +
                "    colorBackgroudLight: 'white',\n" +
                "    colorBackgroudDark: 'rgb(38, 43, 73)',\n" +
                "    colorBackgroudChartD: '#303558',\n" +
                "    darkMode: paramsURL.get('darkMode') ? paramsURL.get('darkMode') : '0',\n" +
                "    fullScreen: paramsURL.get('fullScreen') ? paramsURL.get('fullScreen') : '0',\n" +
                "    hiddenNumber: paramsURL.get('hiddenNumber') ? paramsURL.get('hiddenNumber') : '0',\n" +
                "\n" +
                "}\n" +
                "const iframes = document.querySelectorAll('iframe')\n" +
                "for (var i = 0; i < iframes.length; i++) {\n" +
                "    const iframe = iframes[i];\n" +
                "    iframe.addEventListener('load', function () {\n" +
                "        iframe.contentWindow.postMessage({\n" +
                "            event: 'color',\n" +
                "            data: color\n" +
                "        }, '*');\n" +
                "    })\n" +
                "}\n" +
                "\n" +
                "\n" +
                "let isDarkMode = false\n" +
                "let isSetting = false\n" +
                "let hiddenNumber = false\n" +
                "// let fullScreen = false\n" +
                "isDarkMode = paramsURL.get('darkMode') == '1' ? true : false;\n" +
                "changeBackground(isDarkMode)\n" +
                "document.addEventListener('keyup', (event) => {\n" +
                "    console.log(event)\n" +
                "    sendMessage({\n" +
                "        event: 'keyup',\n" +
                "        data: {\n" +
                "            key: event.code\n" +
                "        }\n" +
                "    });\n" +
                "})\n" +
                "document.addEventListener('DOMContentLoaded', () => {\n" +
                "    var param = {\n" +
                "        mt: paramsURL.get('mt') ? paramsURL.get('mt') : '30',\n" +
                "        mh: paramsURL.get('mh') ? paramsURL.get('mh') : '',\n" +
                "        mx: paramsURL.get('mx') ? paramsURL.get('mx') : '',\n" +
                "        uid: paramsURL.get('uid') ? paramsURL.get('uid') : '',\n" +
                "        type_date: paramsURL.get('type_date') ? paramsURL.get('type_date') : '1',\n" +
                "        appToken: `[CREDENTIAL:HOC-TOKEN]`,\n" +
                "        meta: paramsURL.get('meta') ? paramsURL.get('meta') : '',\n" +
                "    }\n" +
                "    console.log('cha ', param)\n" +
                "    for (var i = 0; i < iframes.length; i++) {\n" +
                "        const iframe = iframes[i];\n" +
                "        checkIframeLoaded(iframe, param, 'paramAPIDashboarch')\n" +
                "        // checkIframeLoaded(iframe, color, 'color');\n" +
                "    }\n" +
                "    const index = paramsURL.get('index') ? parseInt(paramsURL.get('index')) : null, maxIndex = paramsURL.get('max_index') ? parseInt(paramsURL.get('max_index')) : null;\n" +
                "    if (index !== null && maxIndex !== null) {\n" +
                "        console.log(index)\n" +
                "        if (index === 0) { document.getElementById('leftArrow').classList.add('d-none'); }\n" +
                "        else {\n" +
                "            document.getElementById('leftArrow').addEventListener('click', () => {\n" +
                "                sendMessage({\n" +
                "                    event: 'navigateTo',\n" +
                "                    data: index - 1\n" +
                "                });\n" +
                "            })\n" +
                "        }\n" +
                "        if (index === maxIndex) { document.getElementById('rightArrow').classList.add('d-none'); }\n" +
                "        else {\n" +
                "            document.getElementById('rightArrow').addEventListener('click', () => {\n" +
                "                sendMessage({\n" +
                "                    event: 'navigateTo',\n" +
                "                    data: index + 1\n" +
                "                });\n" +
                "            })\n" +
                "        }\n" +
                "    } else {\n" +
                "        document.getElementById('leftArrow').classList.add('d-none');\n" +
                "        document.getElementById('rightArrow').classList.add('d-none')\n" +
                "    }\n" +
                "});\n" +
                "const checkIframeLoaded = (iframe, param, event) => {\n" +
                "    var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;\n" +
                "    if (iframeDoc.readyState === 'complete') {\n" +
                "        iframe.contentWindow.onload = function () {\n" +
                "            iframe.contentWindow.postMessage({\n" +
                "                event: event,\n" +
                "                data: param\n" +
                "            }, '*');\n" +
                "        };\n" +
                "        return;\n" +
                "    }\n" +
                "    window.setTimeout(() => checkIframeLoaded(iframe, param, event), 100);\n" +
                "};\n" +
                "\n" +
                "// function settingIframe() {\n" +
                "//     let settingIframeE = document.getElementById('settingIframe')\n" +
                "//     if (!isSetting) {\n" +
                "//         settingIframeE.style.display = 'none'\n" +
                "//     } else {\n" +
                "//         settingIframeE.style.display = 'block'\n" +
                "//     }\n" +
                "// }\n" +
                "\n" +
                "// settingIframe()\n" +
                "function getToken\n" +
                "function fullScreen(isFullScreen) {\n" +
                "    var elem = document.body;\n" +
                "    changeBackground(isDarkMode)\n" +
                "    if (isFullScreen) {\n" +
                "        if (elem.requestFullscreen) {\n" +
                "            elem.requestFullscreen();\n" +
                "        } else if (elem.mozRequestFullScreen) { /* Firefox */\n" +
                "            elem.mozRequestFullScreen();\n" +
                "        } else if (elem.webkitRequestFullscreen) { /* Chrome, Safari & Opera */\n" +
                "            elem.webkitRequestFullscreen();\n" +
                "        } else if (elem.msRequestFullscreen) { /* IE/Edge */\n" +
                "            elem.msRequestFullscreen();\n" +
                "        }\n" +
                "        elem.style.overflow = 'auto';\n" +
                "    } else {\n" +
                "        if (document.exitFullscreen) {\n" +
                "            document.exitFullscreen();\n" +
                "        } else if (document.mozCancelFullScreen) { /* Firefox */\n" +
                "            document.mozCancelFullScreen();\n" +
                "        } else if (document.webkitExitFullscreen) { /* Chrome, Safari & Opera */\n" +
                "            document.webkitExitFullscreen();\n" +
                "        } else if (document.msExitFullscreen) { /* IE/Edge */\n" +
                "            document.msExitFullscreen();\n" +
                "        }\n" +
                "        elem.style.overflow = 'hidden';\n" +
                "    }\n" +
                "}\n" +
                "function sendMessage(event) {\n" +
                "    window.parent.postMessage(event, '*');\n" +
                "    for (var i = 0; i < iframes.length; i++) {\n" +
                "        iframes[i].contentWindow.postMessage(event, '*'); // '*' ^đ^ại di^ện cho t^ất c^ả c^ác domain\n" +
                "    }\n" +
                "}\n" +
                "function changeBackground(isDarkMode) {\n" +
                "    if (isDarkMode) {\n" +
                "        document.getElementById('wapperDashbroad').style.backgroundColor = color.colorBackgroudDark\n" +
                "    } else {\n" +
                "        document.getElementById('wapperDashbroad').style.backgroundColor = color.colorBackgroudLight\n" +
                "    }\n" +
                "}\n" +
                "function getToken(){\n" +
                "    \n" +
                "}\n" +
                "function catchMessage(event) {\n" +
                "    console.log(event)\n" +
                "    switch (event.data.event) {\n" +
                "        case 'keyup':\n" +
                "            sendMessage(event.data);\n" +
                "            break;\n" +
                "        case 'darkMode':\n" +
                "            isDarkMode = event.data.data\n" +
                "            changeBackground(isDarkMode)\n" +
                "            sendMessage(event.data);\n" +
                "            break;\n" +
                "        case 'hiddenNumber':\n" +
                "            hiddenNumber = event.data.data\n" +
                "            sendMessage(event.data);\n" +
                "            break;\n" +
                "        case 'fullScreen':\n" +
                "            // fullScreen = event.data.data\n" +
                "            fullScreen(event.data.data);\n" +
                "            break;\n" +
                "        case 'color':\n" +
                "            color.colorChart = event.data.data;\n" +
                "            document.querySelectorAll('.icon-arrow').forEach((item) => {\n" +
                "                item.style.color = color.colorChart[0]\n" +
                "            })\n" +
                "            color.darkMode = isDarkMode;\n" +
                "            // color.fullScreen = fullScreen,\n" +
                "            color.hiddenNumber = hiddenNumber\n" +
                "            // console.log(\"Máu: \", color)\n" +
                "            sendMessage({\n" +
                "                event: 'color',\n" +
                "                data: color\n" +
                "            });\n" +
                "            break;\n" +
                "        case 'locThoiGian':\n" +
                "            sendMessage(event.data);\n" +
                "            break;\n" +
                "        // case 'setting':\n" +
                "        //     isSetting = event.data.data\n" +
                "        //     sendMessage(event.data);\n" +
                "        //     settingIframe()\n" +
                "        //     break;\n" +
                "        case 'settingParamAPI':\n" +
                "            sendMessage(event.data);\n" +
                "            break;\n" +
                "        default:\n" +
                "            break;\n" +
                "    }\n" +
                "}\n" +
                "window.addEventListener(\"message\", catchMessage);";
        String js = shorCodeUtils.replaceHTMLShortCode(o);

//        return ResponseEntity.ok(responseEntity.getBody());
        return ResponseEntity.ok(js);
    }

    @PostMapping(path = "/vncare")
    public ResponseEntity<?> testVNCare() throws NoSuchAlgorithmException, KeyManagementException {

        RestTemplate restTemplate = RestTemplateUtil.getRIgnoreCertTemplate();

        HttpHeaders httpHeaders = buildHeaderRequest();
        HttpEntity<String> themBVRequest = new HttpEntity<>(buildThemmoiBVRequest().toString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange("https://staging-datlichkham.vncare.vn/function/Vncare/themMoiBenhVien", HttpMethod.POST, themBVRequest, String.class);
        String themBVStatus = getResponseStatus(responseEntity.getBody());
        if (!themBVStatus.equals("200"))
            throw new UnSuccessException(themBVStatus);
        // Đăng ký BV
        themBVRequest = new HttpEntity<>(buildDangkyBVRequét().toString(), httpHeaders);
        responseEntity = restTemplate.exchange("https://staging-datlichkham.vncare.vn/function/Vncare/dangKyBenhVien", HttpMethod.POST, themBVRequest, String.class);
        themBVStatus = getResponseStatus(responseEntity.getBody());
        if (!themBVStatus.equals("200"))
            throw new UnSuccessException(themBVStatus);
        // Cập nhật Dịch vụ
        themBVRequest = new HttpEntity<>(buildCapnhatDichvuRequest().toString(), httpHeaders);
        responseEntity = restTemplate.exchange("https://staging-datlichkham.vncare.vn/function/Vncare/capNhatTrangThaiDichVuDatLich", HttpMethod.POST, themBVRequest, String.class);
        themBVStatus = getResponseStatus(responseEntity.getBody());
        if (!themBVStatus.equals("200"))
            throw new UnSuccessException(themBVStatus);
        return null;
    }

    private org.json.JSONObject buildCapnhatDichvuRequest(){
        org.json.JSONObject jsonRequest = new org.json.JSONObject();
        jsonRequest.put("maCSYT", "y6");
        jsonRequest.put("dsLoaiKham", new Integer[]{0, 1, 2, 3});
        return jsonRequest;
    }

    private org.json.JSONObject buildThemmoiBVRequest(){
        org.json.JSONObject jsonRequest = new org.json.JSONObject();
        jsonRequest.put("maCoSoYTe", "y6");
        jsonRequest.put("tenCoSoYTe", "Bệnh viện y6");
        jsonRequest.put("tinhID", "1");
        jsonRequest.put("diaChi", "49 Đường Tả Thanh Oai");
        jsonRequest.put("trangThai", "1");
        jsonRequest.put("dangKy", "2");
        jsonRequest.put("taiKhoan", "");
        jsonRequest.put("matKhau", "");
        jsonRequest.put("url", "");
        return jsonRequest;
    }

    private org.json.JSONObject buildDangkyBVRequét(){
        org.json.JSONObject jsonRequest = new org.json.JSONObject();
        jsonRequest.put("maCoSoYTe", "y6");
        jsonRequest.put("hisId", "142552");
        jsonRequest.put("username", "y6.APPBN");
        jsonRequest.put("password", "Pm2^@142552");
        jsonRequest.put("url", "http://qlhk-api-service:8080/patient_api");
        jsonRequest.put("maPhuongXa", "00649");
        jsonRequest.put("xepHang", "Chưa xếp hạng");
        return jsonRequest;
    }

    private HttpHeaders buildHeaderRequest() {
        HttpHeaders headers = new HttpHeaders();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJBRE1JTiIsImhpc0lkIjowLCJzTmFtZSI6IlF14bqjbiB0cuG7iyBo4buHIHRo4buRbmciLCJyb2xlcyI6WyJST0xFX0FETUlOIl0sImlzcyI6InN5cy1xbGhrLWFwaSIsImZ1bGxOYW1lIjoiUXXhuqNuIHRy4buLIGjhu4cgdGjhu5FuZyIsImhvc3BpdGFsTmFtZSI6IiIsInVzZXJJRCI6MTU2NzYsImhOYW1lIjoiIiwiaG9zcGl0YWxDb2RlIjoiMCIsInVzZXJHcm91cElkIjowLCJleHAiOjE3MjgwMDcwMzMsImlhdCI6MTcyNzQwMjIzMywidXNlckxldmVsSWQiOjEsImp0aSI6IjMzZDVkOGY3LWQ1ZTktNGRjZi1iZGRkLTI0NmY1MWU5YWQ4ZiIsInVzZXJuYW1lIjoiQURNSU4ifQ.W5QQzrZYAULIpovL8eKNyL3h3WD0gTl1Rg-z7VhFHDtF7m33PJ81UWv5UsPiOycsZnPS8zaOSQG8WM80zrdT4lClh4lMZKHLDDXB0EQylqVIj9nIyPjKeue30q2SI3kPDf-xJmBojOyAejCDxW8Tgn6qJ5wPzyXGBFP1UASudzK0CltwPFh8-fhoQsxUChpgziaS8_sFb_jm4V_c3ZEk2-Wp92PFkF206FORB2P70deM7uKd-qWtf-_OFo_Nd4XQRhw-qGY83VO6b4czZCFtfqcBWT_UYsF8DVS93B1donKNdzNDKWng_LUI2DdrgiKYqlSr2SP8NWJTiHMlBr3phTOjLhMMnaEoljInRJIHqkmkGERGrqMVfozdIMYHtEUtpK5NM-nRGMKmEr7egGh7EA3xs4tO0InSauAe3dpgDkF4j42cYJRTytpdYO2T-dP6aVvD719gEAyUG9IvXBnF08TrvTGf3wNobPdO2WVrgkZqX6JG7H0R0GnE5dnzOQIdcrM6aZF8hv8zHALifWuDv0e41FUFnrLzeWOnuqULfpW0i2Jl6lxfITvaEse2lriuaFn4bpPEWuDjlp4n7SjWDybPu-DrtdOqebtyfweoNG11kbNxSEXDB5iVTQqwi1DNiWeId6-B9m-O34F_V14VXEYkbex9CLTZcyoeSiGrs_Y";
        headers.add("Accept", "*/*");
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Cookie", "Authorization=" + token);
        return headers;
    }

    private String getResponseStatus(String response){
        String responseObject = response.split(";")[1];
        org.json.JSONObject jsonObject = new JSONObject(responseObject);
        String status = jsonObject.get("statusCode").toString();
        if (!status.equals("200"))
            return jsonObject.get("messageError").toString();
        return status;
    }

}
