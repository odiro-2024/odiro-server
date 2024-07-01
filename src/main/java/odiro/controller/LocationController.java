package odiro.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import odiro.domain.Location;
import odiro.dto.PostLocationRequest;
import odiro.dto.PostLocationResponse;
import odiro.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @ResponseBody
    @PostMapping("/plan/{dayPlanId}/location/create")
    public ResponseEntity<PostLocationResponse> postLocation(@PathVariable("dayPlanId") Long dayPlanId, @RequestBody PostLocationRequest request) {


        Location savedLocation = locationService.postLocation(
                dayPlanId, request.getAddressName(), request.getKakaoMapId(), request.getPhone(), request.getPlaceName(), request.getPlaceUrl(), request.getLat(), request.getLng(), request.getRoadAddressName(), request.getCategoryGroupName(), request.getImgUrl()
        );

        PostLocationResponse response = new PostLocationResponse(savedLocation.getId());

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{locationId}")
    public ResponseEntity<Location> updateLocation(@PathVariable Long locationId, @RequestBody PostLocationRequest request) {
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

        return ResponseEntity.ok(updatedLocation);
    }


    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long locationId) {
        locationService.deleteLocation(locationId);
        return ResponseEntity.noContent().build();
    }


}
