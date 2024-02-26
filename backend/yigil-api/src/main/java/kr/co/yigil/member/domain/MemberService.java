package kr.co.yigil.member.domain;

import kr.co.yigil.member.domain.MemberCommand.TravelsVisibilityRequest;
import kr.co.yigil.member.domain.MemberInfo.CourseListResponse;
import kr.co.yigil.member.domain.MemberInfo.SpotListResponse;
import kr.co.yigil.member.domain.MemberInfo.VisibilityChangeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface MemberService {

    MemberInfo.Main retrieveMemberInfo(Long memberId);

    void withdrawal(Long memberId);

    boolean updateMemberInfo(Long memberId, MemberCommand.MemberUpdateRequest request);

    CourseListResponse retrieveCourseList(Long memberId, Pageable pageable, String selected);
    SpotListResponse retrieveSpotList(Long memberId, Pageable pageable, String selected);

    MemberInfo.FollowerResponse getFollowerList(Long memberId, Pageable pageable);
    MemberInfo.FollowingResponse getFollowingList(Long memberId, Pageable pageable);

    VisibilityChangeResponse setTravelsVisibility(Long memberId, TravelsVisibilityRequest memberCommand);
}
