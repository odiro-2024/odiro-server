package odiro.service;

import lombok.RequiredArgsConstructor;
import odiro.domain.DayPlan;
import odiro.domain.Memo;
import odiro.repository.MemoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final DayPlanService dayPlanService;

    public Memo postMemo(Long dayPlanId, String content, Long PathdayPlanId, Long userId) {

        // DayPlan 검색
        DayPlan dayPlan = dayPlanService.findById(dayPlanId)
                .orElseThrow(() -> new RuntimeException("DayPlan not found"));
        if(dayPlan.getPlan().getInitializer().getId().equals(userId)&&dayPlan.getPlan().getId().equals(PathdayPlanId)) {
            // Memo 생성 후 저장
            Memo memo = new Memo(dayPlan, content);
            memoRepository.save(memo);

            //저장된 플랜 반환
            return memo;
        }else{
            throw new RuntimeException("유저 정보 혹은 플랜 정보가 일치하지 않습니다");
        }
    }

    public Memo editMemo(Long memoId, String content, Long PathdayPlanId, Long userId) {

        //Memo 검색
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new RuntimeException("Memo not found with id: " + memoId));
        if(memo.getDayPlan().getPlan().getInitializer().getId().equals(userId)&&memo.getDayPlan().getPlan().getId().equals(PathdayPlanId)) {
            //Memo 수정 후 저장
            memo.setContent(content);
            memoRepository.save(memo);

            return memo;
        }else{
            throw new RuntimeException("유저 정보 혹은 플랜 정보가 일치하지 않습니다");
        }
    }

    public void deleteMemo(Long memoId, Long PathdayPlanId, Long userId) {

        //Memo 검색
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new RuntimeException("Memo not found with id: " + memoId));
        if(memo.getDayPlan().getPlan().getInitializer().getId().equals(userId)&&memo.getDayPlan().getPlan().getId().equals(PathdayPlanId)) {
        // Memo 삭제
            memoRepository.delete(memo);
        }else{
            throw new RuntimeException("유저 정보 혹은 플랜 정보가 일치하지 않습니다");
        }
    }

}