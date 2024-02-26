package kr.co.yigil.member.domain;

import java.util.List;
import kr.co.yigil.file.FileUploadUtil;
import kr.co.yigil.file.FileUploader;
import kr.co.yigil.follow.domain.Follow;
import kr.co.yigil.follow.domain.FollowReader;
import kr.co.yigil.member.domain.MemberCommand.TravelsVisibilityRequest;
import kr.co.yigil.member.domain.MemberInfo.CourseListResponse;
import kr.co.yigil.member.domain.MemberInfo.FollowerResponse;
import kr.co.yigil.member.domain.MemberInfo.FollowingResponse;
import kr.co.yigil.member.domain.MemberInfo.Main;
import kr.co.yigil.member.domain.MemberInfo.SpotListResponse;
import kr.co.yigil.member.domain.MemberInfo.VisibilityChangeResponse;
import kr.co.yigil.travel.domain.TravelReader;
import kr.co.yigil.travel.domain.course.CourseReader;
import kr.co.yigil.travel.domain.spot.SpotReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberReader memberReader;
    private final MemberStore memberStore;
    private final CourseReader courseReader;
    private final TravelReader travelReader;
    private final SpotReader spotReader;
    private final FollowReader followReader;
    private final FileUploader fileUploader;

    @Override
    @Transactional
    public Main retrieveMemberInfo(Long memberId) {
        var member = memberReader.getMember(memberId);
        var followCount = followReader.getFollowCount(memberId);
        return new Main(member, followCount);
    }

    @Override
    @Transactional
    public void withdrawal(Long memberId) {
        memberStore.withdrawal(memberId);
    }

    @Override
    @Transactional
    public boolean updateMemberInfo(Long memberId, MemberCommand.MemberUpdateRequest request) {

        var member = memberReader.getMember(memberId);
        var currentProfileUrl = member.getProfileImageUrl();
        var updatedProfile = FileUploadUtil.predictAttachFile(request.getProfileImageFile());

        member.updateMemberInfo(request.getNickname(), request.getAges(), request.getGender(),
            updatedProfile.getFileUrl());

        return !currentProfileUrl.equals(updatedProfile.getFileUrl());
    }


    @Override
    @Transactional
    public SpotListResponse retrieveSpotList(Long memberId, Pageable pageable, String selectInfo) {
        var pageSpot = spotReader.getMemberSpotList(memberId, pageable, selectInfo);
        List<MemberInfo.SpotInfo> spotInfoList = pageSpot.getContent().stream()
            .map(MemberInfo.SpotInfo::new)
            .toList();
        return new SpotListResponse(spotInfoList, pageSpot.getTotalPages());
    }

    @Override
    @Transactional
    public CourseListResponse retrieveCourseList(Long memberId, Pageable pageable,
        String selectInfo) {
        var pageCourse = courseReader.getMemberCourseList(memberId, pageable, selectInfo);
        List<MemberInfo.CourseInfo> courseInfoList = pageCourse.getContent().stream()
            .map(MemberInfo.CourseInfo::new)
            .toList();
        return new CourseListResponse(courseInfoList, pageCourse.getTotalPages());
    }

    @Override
    @Transactional
    public FollowerResponse getFollowerList(Long memberId, Pageable pageable) {
        var followerSlice = followReader.getFollowerSlice(memberId, pageable);
        var followerList = followerSlice.getContent().stream()
            .map(Follow::getFollower)
            .map(MemberInfo.FollowInfo::new)
            .toList();
        return new MemberInfo.FollowerResponse(followerList, followerSlice.hasNext());
    }

    @Override
    @Transactional
    public FollowingResponse getFollowingList(Long memberId, Pageable pageable) {
        var followingSlice = followReader.getFollowingSlice(memberId, pageable);
        var followingList = followingSlice.getContent().stream()
            .map(Follow::getFollowing)
            .map(MemberInfo.FollowInfo::new)
            .toList();
        return new FollowingResponse(followingList, followingSlice.hasNext());
    }

    @Override
    @Transactional
    public VisibilityChangeResponse setTravelsVisibility(Long memberId,
        TravelsVisibilityRequest memberCommand) {
        List<Long> travelIds = memberCommand.getTravelIds();
        boolean visibility = memberCommand.getIsPrivate();
        travelReader.setTravelsVisibility(memberId, travelIds, visibility);
        return new VisibilityChangeResponse("공개여부가 변경되었습니다.");
    }
}
