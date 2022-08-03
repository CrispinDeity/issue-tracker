package team20.issuetracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team20.issuetracker.domain.assginee.Assignee;
import team20.issuetracker.domain.assginee.AssigneeRepository;
import team20.issuetracker.domain.issue.*;
import team20.issuetracker.domain.label.Label;
import team20.issuetracker.domain.label.LabelRepository;
import team20.issuetracker.domain.milestone.Milestone;
import team20.issuetracker.domain.milestone.MilestoneRepository;
import team20.issuetracker.service.dto.request.RequestSaveIssueDto;
import team20.issuetracker.service.dto.request.RequestUpdateIssueTitleDto;
import team20.issuetracker.service.dto.response.ResponseIssueDto;
import team20.issuetracker.service.dto.response.ResponseReadAllIssueDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final MilestoneRepository milestoneRepository;
    private final AssigneeRepository assigneeRepository;
    private final LabelRepository labelRepository;
    private final IssueLabelRepository issueLabelRepository;
    private final IssueAssigneeRepository issueAssigneeRepository;

    @Transactional
    public Long save(RequestSaveIssueDto requestSaveIssueDto) {

        String title = requestSaveIssueDto.getTitle();
        String content = requestSaveIssueDto.getContent() == null ? "" : requestSaveIssueDto.getContent();

        List<Assignee> assignees = assigneeRepository.findAllById(requestSaveIssueDto.getAssigneeIds());
        List<Label> labels = labelRepository.findAllById(requestSaveIssueDto.getLabelIds());
        Milestone milestone = null;
        if (requestSaveIssueDto.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(requestSaveIssueDto.getMilestoneId())
                    .orElseThrow(() -> new NoSuchElementException("해당 Milestone 은 존재하지 않습니다."));
        }

        // TODO : Issue 엔티티 만들어서 값 넣어주기.
        Issue newIssue = Issue.of(title, content, milestone);

        // TODO : 연관관계 편의 메서드를 통해 빈 값 채워주기
        newIssue.addAssignees(assignees);
        newIssue.addLabels(labels);

        return issueRepository.save(newIssue).getId();
    }

    // TODO 모든 이슈 조회
    @Transactional(readOnly = true)
    public List<ResponseIssueDto> findAll() {
        List<Issue> findIssues = issueRepository.findAll();
        List<IssueLabel> findIssueLabels = issueLabelRepository.findAllIssueLabels();
        List<IssueAssignee> findIssueAssignees = issueAssigneeRepository.findAllAssignees();

        Set<Label> labels = findIssueLabels.stream()
                .map(IssueLabel::getLabel)
                .collect(Collectors.toSet());

        Set<Assignee> assignees = findIssueAssignees.stream()
                .map(IssueAssignee::getAssignee)
                .collect(Collectors.toSet());

        return findIssues.stream()
                .map(issue -> ResponseIssueDto.of(issue, labels, assignees))
                .collect(Collectors.toList());
    }

    // TODO 내가 할당된 모든 이슈 조회
    @Transactional(readOnly = true)
    public List<ResponseIssueDto> findAssigneeByMeIssues(String oauthId) {

        // 1,2,5,6,7,8 return / 즉, IssueAssignee 에서 assignee_id 가 1인 셀을 모두 가져온 것.
        // 다른 메서드 진행방식과 다르게 IssueAssignee 에서 먼저 거른 뒤 해당하는 Issue 를 뽑아낸다.
        List<IssueAssignee> findIssueAssignees = issueAssigneeRepository.findAllAssignees().stream()
                .filter(issueAssignee -> issueAssignee.getAssignee().getAuthorId().equals(oauthId))
                .collect(Collectors.toList());

        List<IssueLabel> findIssueLabels = issueLabelRepository.findAll();

        List<Issue> findIssues = findIssueAssignees.stream()
                .map(IssueAssignee::getIssue)
                .collect(Collectors.toList());

        Set<Assignee> assignees = findIssueAssignees.stream()
                .map(IssueAssignee::getAssignee)
                .collect(Collectors.toSet());

        Set<Label> labels = findIssueLabels.stream()
                .map(IssueLabel::getLabel)
                .collect(Collectors.toSet());

        return findIssues.stream()
                .map(issue -> ResponseIssueDto.of(issue, labels, assignees))
                .collect(Collectors.toList());
    }

    // TODO 내가 작성한 모든 이슈 조회
    @Transactional(readOnly = true)
    public List<ResponseIssueDto> findAllMyIssues(String oauthId) {
        List<Issue> findAllMyIssues = issueRepository.findAllMyIssues(oauthId);
        List<IssueLabel> findIssueLabels = issueLabelRepository.findAllIssueLabels().stream()
                .filter(issueLabel -> issueLabel.getIssue().getAuthorId().equals(oauthId))
                .collect(Collectors.toList());

        List<IssueAssignee> findIssueAssignees = issueAssigneeRepository.findAllAssignees().stream()
                .filter(issueAssignee -> issueAssignee.getIssue().getAuthorId().equals(oauthId))
                .collect(Collectors.toList());

        Set<Label> labels = findIssueLabels.stream().map(IssueLabel::getLabel).collect(Collectors.toSet());
        Set<Assignee> assignees = findIssueAssignees.stream().map(IssueAssignee::getAssignee).collect(Collectors.toSet());

        return findAllMyIssues.stream()
                .map(issue -> ResponseIssueDto.of(issue, labels, assignees))
                .collect(Collectors.toList());
    }

    // TODO 내가 작성한 모든 열린 또는 닫힌 이슈 조회
    @Transactional(readOnly = true)
    public List<ResponseIssueDto> findAllMyStatusIssues(String oauthId, String issueStatus) {
        List<Issue> findAllMyIssues = issueRepository.findAllMyIssues(oauthId).stream()
                .filter(issue -> issue.getStatus().toString().equals(issueStatus.toUpperCase()))
                .collect(Collectors.toList());

        List<IssueLabel> findIssueLabels = issueLabelRepository.findAllIssueLabels().stream()
                .filter(issueLabel -> issueLabel.getIssue().getAuthorId().equals(oauthId))
                .filter(issueLabel -> issueLabel.getIssue().getStatus().toString().equals(issueStatus.toUpperCase()))
                .collect(Collectors.toList());

        List<IssueAssignee> findIssueAssignees = issueAssigneeRepository.findAllAssignees().stream()
                .filter(issueAssignee -> issueAssignee.getIssue().getAuthorId().equals(oauthId))
                .filter(issueAssignee -> issueAssignee.getIssue().getStatus().toString().equals(issueStatus.toUpperCase()))
                .collect(Collectors.toList());

        Set<Label> labels = findIssueLabels.stream().map(IssueLabel::getLabel).collect(Collectors.toSet());
        Set<Assignee> assignees = findIssueAssignees.stream().map(IssueAssignee::getAssignee).collect(Collectors.toSet());

        return findAllMyIssues.stream()
                .map(issue -> ResponseIssueDto.of(issue, labels, assignees))
                .collect(Collectors.toList());
    }

    // TODO 특정 검색어에 포함되는 모든 이슈 조회
    @Transactional(readOnly = true)
    public List<ResponseIssueDto> findAllSearchIssues(String title) {
        List<Issue> findSearchIssues = issueRepository.findAllSearchIssues(title);

        List<IssueLabel> issueLabels = issueLabelRepository.findAllIssueLabels().stream()
                .filter(issueLabel -> issueLabel.getIssue().getTitle().contains(title))
                .collect(Collectors.toList());

        List<IssueAssignee> issueAssignees = issueAssigneeRepository.findAllAssignees().stream()
                .filter(issueAssignee -> issueAssignee.getIssue().getTitle().contains(title))
                .collect(Collectors.toList());

        Set<Label> labels = issueLabels.stream().map(IssueLabel::getLabel).collect(Collectors.toSet());
        Set<Assignee> assignees = issueAssignees.stream().map(IssueAssignee::getAssignee).collect(Collectors.toSet());

        return findSearchIssues.stream()
                .map(searchIssue -> ResponseIssueDto.of(searchIssue, labels, assignees))
                .collect(Collectors.toList());
    }

    // TODO 특정 Title 에 해당하며 열려있는 모든 이슈 검색
    @Transactional(readOnly = true)
    public List<ResponseIssueDto> findAllSearchStatusIssues(String title, String issueStatus) {
        List<Issue> findAllSearchOpenIssues = issueRepository.findAll().stream()
                .filter(issue -> issue.getStatus().toString().equals(issueStatus.toUpperCase()))
                .filter(issue -> issue.getTitle().contains(title))
                .collect(Collectors.toList());

        List<IssueLabel> issueLabels = issueLabelRepository.findAllIssueLabels().stream()
                .filter(issueLabel -> issueLabel.getIssue().getStatus().toString().equals(issueStatus.toUpperCase()))
                .filter(issueLabel -> issueLabel.getIssue().getTitle().contains(title))
                .collect(Collectors.toList());

        List<IssueAssignee> issueAssignees = issueAssigneeRepository.findAllAssignees().stream()
                .filter(issueAssignee -> issueAssignee.getIssue().getStatus().toString().equals(issueStatus.toUpperCase()))
                .filter(issueAssignee -> issueAssignee.getIssue().getTitle().contains(title))
                .collect(Collectors.toList());

        Set<Label> labels = issueLabels.stream().map(IssueLabel::getLabel).collect(Collectors.toSet());
        Set<Assignee> assignees = issueAssignees.stream().map(IssueAssignee::getAssignee).collect(Collectors.toSet());

        return findAllSearchOpenIssues.stream()
                .map(searchIssue -> ResponseIssueDto.of(searchIssue, labels, assignees))
                .collect(Collectors.toList());
    }

    // TODO Open 되어 있는 모든 이슈 조회
    @Transactional(readOnly = true)
    public List<ResponseIssueDto> findAllOpenIssue() {
        List<Issue> findOpenIssues = issueRepository.findAll().stream()
                .filter(issue -> issue.getStatus().equals(IssueStatus.OPEN))
                .collect(Collectors.toList());

        List<IssueLabel> findIssueLabels = issueLabelRepository.findAllIssueLabels().stream()
                .filter(issueLabel -> issueLabel.getIssue().getStatus().equals(IssueStatus.OPEN))
                .collect(Collectors.toList());

        List<IssueAssignee> findIssueAssignees = issueAssigneeRepository.findAllAssignees().stream()
                .filter(issueAssignee -> issueAssignee.getIssue().getStatus().equals(IssueStatus.OPEN))
                .collect(Collectors.toList());

        Set<Label> labels = findIssueLabels.stream().map(IssueLabel::getLabel).collect(Collectors.toSet());
        Set<Assignee> assignees = findIssueAssignees.stream().map(IssueAssignee::getAssignee).collect(Collectors.toSet());

        return findOpenIssues.stream()
                .map(openIssue -> ResponseIssueDto.of(openIssue, labels, assignees))
                .collect(Collectors.toList());
    }

    // TODO Close 되어 있는 모든 이슈 조회
    @Transactional(readOnly = true)
    public List<ResponseIssueDto> findAllCloseIssue() {
        List<Issue> findCloseIssues = issueRepository.findAll().stream()
                .filter(issue -> issue.getStatus().equals(IssueStatus.CLOSE))
                .collect(Collectors.toList());

        List<IssueLabel> findIssueLabels = issueLabelRepository.findAllIssueLabels().stream()
                .filter(issueLabel -> issueLabel.getIssue().getStatus().equals(IssueStatus.CLOSE))
                .collect(Collectors.toList());

        List<IssueAssignee> findIssueAssignees = issueAssigneeRepository.findAllAssignees().stream()
                .filter(issueAssignee -> issueAssignee.getIssue().getStatus().equals(IssueStatus.CLOSE))
                .collect(Collectors.toList());

        Set<Label> labels = findIssueLabels.stream().map(IssueLabel::getLabel).collect(Collectors.toSet());
        Set<Assignee> assignees = findIssueAssignees.stream().map(IssueAssignee::getAssignee).collect(Collectors.toSet());

        return findCloseIssues.stream()
                .map(openIssue -> ResponseIssueDto.of(openIssue, labels, assignees))
                .collect(Collectors.toList());
    }

    // TODO 특정 이슈 상세 조회
    @Transactional(readOnly = true)
    public ResponseIssueDto detail(Long id) {
        Issue findIssue = issueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 Issue 는 존재하지 않습니다"));

        List<IssueLabel> issueLabels = issueLabelRepository.findAllById(id);
        List<IssueAssignee> issueAssignees = issueAssigneeRepository.findAllById(id);

        Set<Label> labels = issueLabels.stream()
                .map(IssueLabel::getLabel)
                .collect(Collectors.toSet());

        Set<Assignee> assignees = issueAssignees.stream()
                .map(IssueAssignee::getAssignee)
                .collect(Collectors.toSet());

        return ResponseIssueDto.of(findIssue, labels, assignees);
    }

    // TODO 특정 이슈 삭제
    @Transactional
    public void delete(Long id) {
        Issue findIssue = issueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 Issue 는 존재하지 않습니다."));

        issueRepository.delete(findIssue);
    }

    // TODO 특정 이슈 제목 수정
    @Transactional
    public Long updateTitle(Long id, RequestUpdateIssueTitleDto requestUpdateIssueTitleDto) {
        Issue findIssue = issueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 Issue 는 존재하지 않습니다."));

        findIssue.updateTitle(requestUpdateIssueTitleDto);

        return findIssue.getId();
    }

    // TODO 124 라인부터 아래로 곂치는 메서드 리팩토링 필요
    /*
        170 ~ 176 Line - 모든 이슈 Response
        178 ~ 184 Line - 모든 열린 이슈 Response
        186 ~ 192 Line - 모든 닫힌 이슈 Response
        201 ~ 206 Line - 검색된 키워드의 전체 이슈
     */
    public ResponseReadAllIssueDto getAllIssueData(List<ResponseIssueDto> issues) {
        long openIssueCount = issues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.OPEN)).count();
        long closedIssueCount = issues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.CLOSE)).count();
        long labelCount = issues.stream().map(issue -> issue.getLabels().size()).count();

        return ResponseReadAllIssueDto.of(openIssueCount, closedIssueCount, labelCount, issues);
    }

    public ResponseReadAllIssueDto getAllIOpenIssueData(List<ResponseIssueDto> findAllOpenIssues, List<ResponseIssueDto> findAllIssues) {
        long openIssueCount = findAllIssues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.OPEN)).count();
        long closedIssueCount = findAllIssues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.CLOSE)).count();
        long labelCount = findAllIssues.stream().map(issue -> issue.getLabels().size()).count();

        return ResponseReadAllIssueDto.of(openIssueCount, closedIssueCount, labelCount, findAllOpenIssues);
    }

    public ResponseReadAllIssueDto getAllICloseIssueData(List<ResponseIssueDto> findAllCloseIssues, List<ResponseIssueDto> findAllIssues) {
        long openIssueCount = findAllIssues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.OPEN)).count();
        long closedIssueCount = findAllIssues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.CLOSE)).count();
        long labelCount = findAllIssues.stream().map(issue -> issue.getLabels().size()).count();

        return ResponseReadAllIssueDto.of(openIssueCount, closedIssueCount, labelCount, findAllCloseIssues);
    }

    public ResponseReadAllIssueDto getAllSearchStatusIssueData(List<ResponseIssueDto> findAllSearchIssues, List<ResponseIssueDto> findAllIssues) {
        long openIssueCount = findAllIssues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.OPEN)).count();
        long closedIssueCount = findAllIssues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.CLOSE)).count();
        long labelCount = findAllIssues.stream().map(issue -> issue.getLabels().size()).count();

        return ResponseReadAllIssueDto.of(openIssueCount, closedIssueCount, labelCount, findAllSearchIssues);
    }

    public ResponseReadAllIssueDto getStatusFilterAllIssueData(List<ResponseIssueDto> responseIssueDtos, List<ResponseIssueDto> findAllIssues) {
        long openIssueCount = findAllIssues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.OPEN)).count();
        long closedIssueCount = findAllIssues.stream().filter(issue -> issue.getIssueStatus().equals(IssueStatus.CLOSE)).count();
        long labelCount = findAllIssues.stream().map(issue -> issue.getLabels().size()).count();

        return ResponseReadAllIssueDto.of(openIssueCount, closedIssueCount, labelCount, responseIssueDtos);
    }
}
