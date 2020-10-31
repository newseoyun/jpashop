package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/vi/members")
    public CreateMemberResponse saveMemberV1 (@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    // 위의 경우 도메인을 수정했을 경우 api 스펙이 변경되니까 해당 파라미터로는 장애가 유발된다

    // 아래의 경우 member 엔티티를 새로 생성했기 때문에 도메인 수정해도 리퀘스트 파라미터는 상관이 없고
    // 전용 엔티티에(CreateMemberRequest)에 api 스펙을 정의할 수 있다.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }




    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
