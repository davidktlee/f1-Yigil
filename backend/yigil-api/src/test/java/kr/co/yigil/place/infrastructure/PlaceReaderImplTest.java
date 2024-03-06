package kr.co.yigil.place.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kr.co.yigil.global.exception.BadRequestException;
import kr.co.yigil.member.Ages;
import kr.co.yigil.member.Gender;
import kr.co.yigil.place.domain.DemographicPlace;
import kr.co.yigil.place.domain.Place;
import kr.co.yigil.place.domain.PlaceCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PlaceReaderImplTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private DemographicPlaceRepository demographicPlaceRepository;

    @InjectMocks
    private PlaceReaderImpl placeReader;

    @DisplayName("findByNameAndAddress 메서드가 Place의 Optional 객체를 잘 반환하는지")
    @Test
    void findByNameAndAddress_ReturnsOptionalOfPlace() {
        String placeName = "name";
        String address = "address";
        Place expectedPlace = mock(Place.class);
        when(placeRepository.findByNameAndAddress(placeName, address)).thenReturn(
                Optional.of(expectedPlace));

        Optional<Place> result = placeReader.findPlaceByNameAndAddress(placeName, address);

        assert (result.isPresent());
        assertEquals(result.get(), expectedPlace);
    }

    @DisplayName("getPlace 메서드가 Place을 잘 반환하는지")
    @Test
    void getPlace_ReturnsPlace() {
        Long placeId = 1L;
        Place expectedPlace = mock(Place.class);
        when(placeRepository.findById(placeId)).thenReturn(Optional.of(expectedPlace));

        Place result = placeReader.getPlace(placeId);

        assertEquals(expectedPlace, result);
    }

    @DisplayName("getPlace 메서드가 placeId가 유효하지 않을 때 예외를 잘 발생시키는지")
    @Test
    void getPlace_ThrowsBadRequestException_WhenNotFound() {
        Long placeId = 1L;
        when(placeRepository.findById(placeId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> placeReader.getPlace(placeId));
    }

    @DisplayName("getPopularPlace 메서드가 Place의 리스트를 잘 반환하는지")
    @Test
    void getPopularPlace_ReturnsListOfPlace() {
        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);
        Place place3 = mock(Place.class);
        Place place4 = mock(Place.class);
        Place place5 = mock(Place.class);
        when(placeRepository.findTop5ByOrderByIdAsc()).thenReturn(
                Arrays.asList(place1, place2, place3, place4, place5));

        assertEquals(placeReader.getPopularPlace().size(), 5);
    }

    @DisplayName("getPlaceInRegion 메서드가 Place의 리스트를 잘 반환하는지")
    @Test
    void getPlaceInRegion_ReturnsListOfPlace() {
        Long regionId = 1L;
        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);
        Place place3 = mock(Place.class);
        Place place4 = mock(Place.class);
        Place place5 = mock(Place.class);
        when(placeRepository.findTop5ByRegionIdOrderByIdDesc(regionId)).thenReturn(
                Arrays.asList(place1, place2, place3, place4, place5));

        assertEquals(placeReader.getPlaceInRegion(regionId).size(), 5);
    }

    @DisplayName("getNearPlace 메서드가 Page<Place>를 잘 반환하는지")
    @Test
    void getNearPlace_ReturnsPageOfPlace() {
        PlaceCommand.NearPlaceRequest command = mock(PlaceCommand.NearPlaceRequest.class);
        when(command.getPageNo()).thenReturn(1);
        PlaceCommand.Coordinate mockCoordinate = mock(PlaceCommand.Coordinate.class);
        when(mockCoordinate.getX()).thenReturn(1.0);
        when(mockCoordinate.getY()).thenReturn(1.0);
        when(command.getMinCoordinate()).thenReturn(mockCoordinate);
        when(command.getMaxCoordinate()).thenReturn(mockCoordinate);

        Page<Place> mockPage = mock(Page.class);

        when(placeRepository.findWithinCoordinates(
                anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(Pageable.class))).thenReturn(mockPage);

        var result = placeReader.getNearPlace(command);

        assertEquals(result, mockPage);
    }

    @DisplayName("getPopularPlaceByDemographics 메서드가 Place의 리스트를 잘 반환하는지")
    @Test
    void getPopularPlaceByDemographics_ReturnsListOfPlace() {
        DemographicPlace demographicPlace1 = mock(DemographicPlace.class);
        Place place1 = mock(Place.class);
        when(demographicPlace1.getPlace()).thenReturn(place1);
        when(demographicPlaceRepository.findTop5ByAgesAndGenderOrderByReferenceCountDesc(
                any(), any())).thenReturn(List.of(demographicPlace1));

        List<Place> result = placeReader.getPopularPlaceByDemographics(Ages.TWENTIES, Gender.MALE);

        assertEquals(result.size(), 1);
        assertTrue(result.containsAll(Arrays.asList(place1)));

    }

    @DisplayName("getPopularPlaceByDemographicsMore 메서드가 Place의 리스트를 잘 반환하는지")
    @Test
    void getPopularPlaceByDemographicsMore_ReturnsListOfPlace() {
        DemographicPlace demographicPlace1 = mock(DemographicPlace.class);
        Place place1 = mock(Place.class);
        when(demographicPlace1.getPlace()).thenReturn(place1);
        when(demographicPlaceRepository.findTop20ByAgesAndGenderOrderByReferenceCountDesc(
                any(), any())).thenReturn(List.of(demographicPlace1));

        List<Place> result = placeReader.getPopularPlaceByDemographicsMore(Ages.TWENTIES,
                Gender.NONE);

        assertEquals(result.size(), 1);
        assertTrue(result.containsAll(Arrays.asList(place1)));
    }

}
