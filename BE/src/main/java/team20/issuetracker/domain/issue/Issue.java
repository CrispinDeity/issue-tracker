package team20.issuetracker.domain.issue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import team20.issuetracker.domain.assginee.Assignee;
import team20.issuetracker.domain.comment.Comment;
import team20.issuetracker.domain.label.Label;
import team20.issuetracker.domain.milestone.Milestone;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String title;
    private String content;

    @Enumerated(value = EnumType.STRING)
    private IssueStatus status = IssueStatus.OPEN;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    @OneToMany(mappedBy = "issue")
    private List<IssueAssignee> issueAssignees = new ArrayList<>();

    @OneToMany(mappedBy = "issue")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "issue")
    private List<IssueLabel> issueLabels = new ArrayList<>();

    private Issue(String author, String title, String content, LocalDateTime createdAt, Milestone milestone) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.milestone = milestone;
    }

    public static Issue of(String author, String title, String content, LocalDateTime createdAt, Milestone milestone) {
        return new Issue(author, title, content, createdAt, milestone);
    }

    public void addAssignees(List<Assignee> assignees) {
        for (Assignee assignee : assignees) {
            this.issueAssignees.add(IssueAssignee.of(this, assignee));
        }
    }

    public void addLabels(List<Label> labels) {
        for (Label label : labels) {
            this.issueLabels.add(IssueLabel.of(this, label));
        }
    }
}