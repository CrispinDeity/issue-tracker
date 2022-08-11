package team20.issuetracker.service.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team20.issuetracker.domain.milestone.MilestoneStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestUpdateMilestoneDto {

    @NotEmpty
    @Size(max = 50, message = "Milestone 의 제목은 20글자를 넘을 수 없습니다.")
    private String title;

    @Size(max = 800, message = "Milestone 의 본문은 800글자를 넘을 수 없습니다.")
    private String description;

    @FutureOrPresent(message = "해당 시간은 이미 지나간 시간입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private MilestoneStatus milestoneStatus;

    public static RequestUpdateMilestoneDto of(String title, String description, LocalDate startDate, LocalDate endDate, MilestoneStatus milestoneStatus) {
        return new RequestUpdateMilestoneDto(title, description, startDate, endDate, milestoneStatus);
    }
}