package odiro.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import odiro.config.auth.PrincipalDetails;
import odiro.domain.DayPlan;
import odiro.domain.Location;
import odiro.domain.Plan;
import odiro.dto.dayPlan.PostDayPlanResponse;
import odiro.dto.location.*;
import odiro.dto.member.HomeResponse;
import odiro.service.DayPlanService;
import odiro.service.LocationService;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import org.springframework.boot.configurationprocessor.json.JSONArray;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LocationController {

    private final LocationService locationService;
    private final DayPlanService dayPlanService;

    //장소 새로 등록
    @PostMapping("/location/create")
    public ResponseEntity<PostLocationResponse> postLocation(@RequestBody PostLocationRequest request) {

        // https://place.map.kakao.com/placePrint.daum?confirmid={장소 고유 ID} 형식으로 로직 통일 가능
        String url = "https://place.map.kakao.com/placePrint.daum?confirmid=" + request.getKakaoMapId();
        String imagePath = null;

        //이미지 크롤링
        try {   //이미지가 없을경우 예외처리
            Document doc = Jsoup.connect(url).get();
            Elements images = doc.getElementsByClass("thumb_g");
            if (!images.isEmpty()) {
                imagePath = images.get(0).attr("src");
            } else {
                imagePath = null; // or provide a default image path
            }
        } catch (IOException e) {
            e.printStackTrace();
            imagePath = null; // or provide a default image path
        }

        Location savedLocation = locationService.postLocation(
                request.getDayPlanId(), request.getAddressName(), request.getKakaoMapId(), request.getPhone(), request.getPlaceName(), request.getPlaceUrl(), request.getLat(), request.getLng(), request.getRoadAddressName(), imagePath, request.getCategoryGroupName()
        );

        PostLocationResponse response = new PostLocationResponse(savedLocation.getId(), imagePath);

        return ResponseEntity.ok(response);
    }

    //장소 삭제
    @DeleteMapping("/location/delete/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable("locationId") Long locationId) {

        //삭제
        locationService.deleteLocation(locationId);

        //결과 반환
        return ResponseEntity.noContent().build();
    }

    //장소 DayPlan에서의 배치 순서 변경
    @PostMapping("/location/reorder")
    public ResponseEntity<PostDayPlanResponse> postLocation(@RequestBody ReorderLocationRequest request) {

        DayPlan reorderedDayplan = dayPlanService.reorderLocations(request.getDayPlanId(), request.getReorderedLocationIds());

        PostDayPlanResponse response = new PostDayPlanResponse(reorderedDayplan.getId());

        return ResponseEntity.ok(response);
    }

    /* //장소 수정 불필요
    @PutMapping("/location/{locationId}")
    public ResponseEntity<PostLocationResponse> updateLocation(@PathVariable("locationId") Long locationId, @RequestBody PostLocationRequest request) {

        //수정된 내용 저장
        Location updatedLocation = locationService.updateLocation(
                locationId,
                request.getAddressName(),
                request.getKakaoMapId(),
                request.getPhone(),
                request.getPlaceName(),
                request.getPlaceUrl(),
                request.getLat(),
                request.getLng(),
                request.getRoadAddressName(),
                request.getCategoryGroupName(),
                request.getImgUrl());

        //결과 반환
        PostLocationResponse response = new PostLocationResponse(updatedLocation.getId());
        return ResponseEntity.ok(response);
    }

     */

    // 찜하기
    @PostMapping("/wishLocation/create")
    public ResponseEntity<PostLocationResponse> postWishLocation(@RequestBody PostWishLocationRequest request) {

        // https://place.map.kakao.com/placePrint.daum?confirmid={장소 고유 ID} 형식으로 로직 통일 가능
        String url = "https://place.map.kakao.com/placePrint.daum?confirmid=" + request.getKakaoMapId();
        String imagePath = null;

        //이미지 크롤링
        try {
            Document doc = Jsoup.connect(url).get();

            // "https:" 가 빠진채로 image src가 저장되어 있으므로 "https:" prefix 추가
            imagePath = doc.getElementsByClass("thumb_g").get(0).attr("src");

        } catch (IOException e) {
            e.printStackTrace();
        }

        Location savedLocation = locationService.postWishLocation(
                request.getPlanId(), request.getAddressName(), request.getKakaoMapId(), request.getPhone(), request.getPlaceName(), request.getPlaceUrl(), request.getLat(), request.getLng(), request.getRoadAddressName(), imagePath, request.getCategoryGroupName()
        );

        PostLocationResponse response = new PostLocationResponse(savedLocation.getId(), imagePath);

        return ResponseEntity.ok(response);
    }

    //찜한것을 DayPlan에 등록
    @PostMapping("/wishLocation/bring")
    public ResponseEntity<LocationRegisterResponse> registerWishLoction(@RequestBody RegisterWishLocationRequest request) {


        Location savedLocation = locationService.registerWishLocation(
                request.getLocationId(), request.getDayPlanId()
        );

        LocationRegisterResponse response = new LocationRegisterResponse(savedLocation.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/wishLocation/delete/{locationId}")
    public ResponseEntity<Void> deleteWishLocation(@PathVariable("locationId") Long locationId) {

        //삭제
        locationService.deleteLocation(locationId);

        //결과 반환
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/location/festival/research")
//    public ResponseEntity<FestivalResearchResponse> FestivalResearch(@RequestBody FestivalResearchRequest request) {
//
//        try {
//            String servicekey = "szHH6COt5YFTsdBxmYiQHMud7PenOjVtlp3UgLc9a16gRpnoLPcSlKecg9w7Rd%2Bhz0bOAHMnfpQfMDx3KaYpNA%3D%3D";
//            String infourl = "http://apis.data.go.kr/B551011/KorService1/searchFestival1?";
//            StringBuilder infosb = new StringBuilder();
//            infosb.append(infourl);
//            infosb.append("MobileOS=ETC");
//            infosb.append("&MobileApp=odiro");
//            infosb.append("&_type=json");
//            infosb.append("&eventStartDate=").append(request.getYyyymmdd());
//            infosb.append("&serviceKey=").append(servicekey);
//
//            //http 요청 수행
//            URL inurl = new URL(infosb.toString());
//            HttpURLConnection inconn = (HttpURLConnection) inurl.openConnection();
//
//            if (inconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                try (BufferedReader br2 = new BufferedReader(new InputStreamReader(inconn.getInputStream(), "UTF-8"))) {
//                    StringBuilder inst = new StringBuilder();
//                    String infoline;
//                    while ((infoline = br2.readLine()) != null) {
//                        inst.append(infoline);
//                    }
//
//                    System.out.println("Response Data: " + inst.toString());
//
//                    // JSON 파싱
//                    JSONObject json1 = new JSONObject(inst.toString());
//                    JSONObject resp1 = json1.getJSONObject("response");
//                    JSONObject body1 = resp1.getJSONObject("body");
//                    JSONObject items1 = body1.getJSONObject("items");
//                    JSONArray itemar = items1.getJSONArray("item");
//
//                    List<FestivalDto> itemList = new ArrayList<>();
//
//                    // JSONArray의 길이 얻기
//                    int length = itemar.length();
//                    for (int i = 0; i < length; i++) {
//                        JSONObject item1 = itemar.getJSONObject(i);
//
//                        // 필요한 데이터 추출 및 ItemDTO 생성
//                        FestivalDto itemDTO = new FestivalDto(
//                                item1.optString("addr1", ""),
//                                item1.optString("addr2", ""),
//                                item1.optString("eventstartdate", ""),
//                                item1.optString("eventenddate", ""),
//                                item1.optString("firstimage", ""),
//                                item1.optString("firstimage2", ""),
//                                item1.optString("tel", ""),
//                                item1.optString("title", "")
//                        );
//                        itemList.add(itemDTO);
//                    }
//
//                    // LocationResponseDTO 생성 및 성공적인 응답 반환
//                    FestivalResearchResponse responseDTO = new FestivalResearchResponse(itemList);
//                    return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//                }
//            } else {
//                // 오류 응답
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 예외 발생 시 에러 응답
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping("/location/festival/research")
    public ResponseEntity<FestivalResearchResponse> festivalResearch(@RequestBody FestivalResearchRequest request) {
        String serviceKey = "szHH6COt5YFTsdBxmYiQHMud7PenOjVtlp3UgLc9a16gRpnoLPcSlKecg9w7Rd%2Bhz0bOAHMnfpQfMDx3KaYpNA%3D%3D";
        String apiUrl = "http://apis.data.go.kr/B551011/KorService1/searchFestival1?";

        try {
            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("MobileOS=ETC");
            urlBuilder.append("&MobileApp=odiro");
            urlBuilder.append("&_type=json");
            urlBuilder.append("&eventStartDate=").append(request.getYyyymmdd());
            urlBuilder.append("&serviceKey=").append(serviceKey);

            // HTTP 요청 수행
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                System.out.println("Response Data: " + response.toString());

                // JSON 파싱
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject body = jsonResponse.getJSONObject("response").getJSONObject("body");
                JSONArray items = body.getJSONObject("items").getJSONArray("item");

                List<FestivalDto> festivalList = new ArrayList<>();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    FestivalDto festival = new FestivalDto(
                            item.optString("addr1", ""),
                            item.optString("addr2", ""),
                            item.optString("eventstartdate", ""),
                            item.optString("eventenddate", ""),
                            item.optString("firstimage", ""),
                            item.optString("firstimage2", ""),
                            item.optString("tel", ""),
                            item.optString("title", "")
                    );
                    festivalList.add(festival);
                }

                // Response 생성 및 반환
                FestivalResearchResponse responseDto = new FestivalResearchResponse(festivalList);
                return new ResponseEntity<>(responseDto, HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/location/search")
//    public ResponseEntity<InformationVO> init(@RequestParam String contenttypeid, @RequestParam String contentid) {
//        try {
//            String servicekey = "VZwsEBpKrcOmbKb2y%2FszpWMkbfTx9GLvm2dZ96N6fn9bubmU0iPfGKNkuGSqCvCgpqL611HousPLRFN2KBEk9w%3D%3D";
//            String infourl = "http://apis.data.go.kr/B551011/KorService1/detailCommon1?";
//            StringBuilder infosb = new StringBuilder();
//            infosb.append(infourl);
//            infosb.append("MobileOS=ETC");
//            infosb.append("&MobileApp=dajuu");
//            infosb.append("&_type=json");
//            infosb.append("&contentId=").append(contentid);
//            infosb.append("&contentTypeId=").append(contenttypeid);
//            infosb.append("&numOfRows=10");
//            infosb.append("&pageNo=1");
//            infosb.append("&serviceKey=").append(servicekey);
//            infosb.append("&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y");
//
//            //http 요청 수행
//            URL inurl = new URL(infosb.toString());
//            HttpURLConnection inconn = (HttpURLConnection) inurl.openConnection();
//
//            //응답 처리
//            if (inconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                try (BufferedReader br2 = new BufferedReader(new InputStreamReader(inconn.getInputStream(), "UTF-8"))) {
//                    StringBuilder inst = new StringBuilder();
//                    String infoline;
//                    while ((infoline = br2.readLine()) != null) {
//                        inst.append(infoline);
//                    }
//
//                    // JSON 파싱
//                    JSONParser parser1 = new JSONParser();
//                    JSONObject json1 = (JSONObject) parser1.parse(inst.toString());
//                    JSONObject resp1 = (JSONObject) json1.get("response");
//                    JSONObject body1 = (JSONObject) resp1.get("body");
//                    JSONObject items1 = (JSONObject) body1.get("items");
//                    JSONArray itemar = (JSONArray) items1.get("item");
//
//                    InformationVO infoVO = null;
//
//                    // JSONArray의 길이 얻기
//                    int length = itemar.size(); // size() 대신 length() 사용
//                    for (int i = 0; i < length; i++) {
//                        JSONObject item1 = (JSONObject) itemar.get(i);
//
//                        // 필요한 데이터 추출 및 InformationVO 생성
//                        infoVO = new InformationVO(
//                                item1.get("contentid") != null ? item1.get("contentid").toString() : "",
//                                item1.get("contenttypeid") != null ? item1.get("contenttypeid").toString() : "",
//                                item1.get("title") != null ? item1.get("title").toString() : "",
//                                item1.get("createdtime") != null ? item1.get("createdtime").toString() : "",
//                                item1.get("modifiedtime") != null ? item1.get("modifiedtime").toString() : "",
//                                item1.get("tel") != null ? item1.get("tel").toString() : "",
//                                item1.get("telname") != null ? item1.get("telname").toString() : "",
//                                item1.get("homepage") != null ? item1.get("homepage").toString() : "",
//                                item1.get("booktour") != null ? item1.get("booktour").toString() : "",
//                                item1.get("firstimage") != null ? item1.get("firstimage").toString() : "",
//                                item1.get("firstimage2") != null ? item1.get("firstimage2").toString() : "",
//                                item1.get("cpyrhtDivCd") != null ? item1.get("cpyrhtDivCd").toString() : "",
//                                item1.get("areacode") != null ? item1.get("areacode").toString() : "",
//                                item1.get("sigungucode") != null ? item1.get("sigungucode").toString() : "",
//                                item1.get("cat1") != null ? item1.get("cat1").toString() : "",
//                                item1.get("cat2") != null ? item1.get("cat2").toString() : "",
//                                item1.get("cat3") != null ? item1.get("cat3").toString() : "",
//                                item1.get("addr1") != null ? item1.get("addr1").toString() : "",
//                                item1.get("addr2") != null ? item1.get("addr2").toString() : "",
//                                item1.get("zipcode") != null ? item1.get("zipcode").toString() : "",
//                                item1.get("mapx") != null ? item1.get("mapx").toString() : "",
//                                item1.get("mapy") != null ? item1.get("mapy").toString() : "",
//                                item1.get("mlevel") != null ? item1.get("mlevel").toString() : "",
//                                item1.get("overview") != null ? item1.get("overview").toString() : ""
//                        );
//                    }
//
//                    // 성공적인 응답
//                    return new ResponseEntity<>(infoVO, HttpStatus.OK);
//                }
//            } else {
//                // 오류 응답
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 예외 발생 시 에러 응답
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


//    @PostMapping("/location/image/crawl")
//    public CrawlResponse extractImageFromWeb(@RequestBody CrawlRequest request) {
//        CrawlResponse response = new CrawlResponse(null);
//        try {
//            // Jsoup을 사용하여 URL에서 HTML을 가져옵니다.
//            Document doc = Jsoup.connect(request.getUrl())
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
//                    .timeout(10 * 1000)
//                    .get();
//
//            // 'bg_present' 클래스를 가진 요소를 선택합니다.
//            Elements elements = doc.select(".bg_present");
//
//            if (elements.isEmpty()) {
//                throw new IOException("No elements found with the specified class.");
//            }
//
//            // 선택된 요소의 background-image URL을 출력합니다.
//            for (Element element : elements) {
//                String style = element.attr("style");
//                String imageUrl = extractBackgroundImageUrl(style);
//                if (imageUrl != null) {
//                    response.setImageUrl(imageUrl);
//                    break;  // 이미지 URL을 찾으면 반복문을 중지합니다.
//                }
//            }
//
//            if (response.getImageUrl() == null) {
//                throw new IOException("No background-image found in the specified element.");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            // 예외 상황에 대해 적절한 응답을 설정합니다.
//            response.setImageUrl("Error: Unable to extract image URL.");
//        }
//
//        return response;
//    }
//
//    private static String extractBackgroundImageUrl(String style) {
//        String url = null;
//        String prefix = "background-image: url(";
//        int startIndex = style.indexOf(prefix);
//        if (startIndex != -1) {
//            startIndex += prefix.length();
//            int endIndex = style.indexOf(")", startIndex);
//            if (endIndex != -1) {
//                url = style.substring(startIndex, endIndex);
//                // URL에서 불필요한 따옴표를 제거합니다.
//                url = url.replace("\"", "").replace("'", "");
//            }
//        }
//        return url;
//    }




//    @PostMapping("/location/image/crawl")
//    public CrawlResponse extractImageFromWeb(CrawlRequest request) {
//        CrawlResponse response = new CrawlResponse(null);
//        try {
//            // 로그인 필요한 경우 인증 정보를 포함하여 로그인
//            Map<String, String> loginCookies = performLoginAndGetCookies();
//
//            // Jsoup을 사용하여 URL에서 HTML을 가져옵니다.
//            Document doc = Jsoup.connect(request.getUrl())
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
//                    .header("Referer", "https://your.referer.url/")
//                    .header("Authorization", "Bearer your-token")  // JWT 또는 다른 인증 토큰이 필요할 경우
//                    .cookies(loginCookies)  // 로그인 시 받은 쿠키를 추가
//                    .timeout(10 * 1000)
//                    .get();
//
//            Elements elements = doc.select(".bg_present");
//            if (!elements.isEmpty()) {
//                String style = elements.get(0).attr("style");
//                String imageUrl = extractBackgroundImageUrl(style);
//                response.setImageUrl(imageUrl);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            response.setImageUrl("Error: Unable to extract image URL due to " + e.getMessage());
//        }
//        return response;
//    }
//
//    private Map<String, String> performLoginAndGetCookies() throws IOException {
//        // 예시: 로그인 요청을 수행하고 쿠키를 받아오는 메소드
//        Connection.Response loginResponse = Jsoup.connect("https://example.com/login")
//                .data("username", "your-username")
//                .data("password", "your-password")
//                .method(Connection.Method.POST)
//                .execute();
//
//        return loginResponse.cookies();
//    }
//
//    private String extractBackgroundImageUrl(String style) {
//        String prefix = "background-image: url(";
//        int startIndex = style.indexOf(prefix);
//        if (startIndex != -1) {
//            startIndex += prefix.length();
//            int endIndex = style.indexOf(")", startIndex);
//            if (endIndex != -1) {
//                return style.substring(startIndex, endIndex).replace("\"", "").replace("'", "");
//            }
//        }
//        return null;
//    }

//    @PostMapping("/location/image/crawl")
//    public CrawlResponse CrawlResponse(@RequestBody CrawlRequest request) {
//
//        try {
//            // 요청에서 전달받은 URL을 사용하여 HTML을 가져옵니다.
//            Document doc = Jsoup.connect(request.getUrl()).get();
//
//            // 'link_figure' 클래스를 가진 요소를 선택합니다.
//            Elements elements = doc.select(".link_figure img");
//
//            // 선택된 요소에서 data-org-src 속성 값을 반환합니다.
//            for (Element element : elements) {
//                String imageUrl = element.attr("data-org-src");
//                if (!imageUrl.isEmpty()) {
//                    CrawlResponse response = new CrawlResponse(imageUrl);;
//                    return response;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            CrawlResponse response = new CrawlResponse(null);
//            return response;
//        }
//        CrawlResponse response = new CrawlResponse(null);
//        return response;
//    }

//    @PostMapping("/location/image/crawl")
//    public CrawlResponse extractImageFromWeb(@RequestBody CrawlRequest request) {
//        CrawlResponse response = new CrawlResponse(null);
//
//        try {
//            // 요청에서 전달받은 URL을 사용하여 HTML을 가져옴
//            Document doc = Jsoup.connect(request.getUrl()).get();
//
//            // 'thumb_g' 클래스를 가진 요소를 선택하고, src 속성 값을 추출하여 이미지 경로를 완성
//            String imagePath = "https:" + doc.getElementsByClass("thumb_g").get(0).attr("src");
//
//            // CrawlResponse 객체에 이미지 URL을 설정
//            response.setImageUrl(imagePath);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return response;
//    }
}
