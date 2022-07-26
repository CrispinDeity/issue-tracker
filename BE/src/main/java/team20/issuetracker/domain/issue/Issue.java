package team20.issuetracker.domain.issue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team20.issuetracker.domain.AuditingFields;
import team20.issuetracker.domain.assginee.Assignee;
import team20.issuetracker.domain.comment.Comment;
import team20.issuetracker.domain.label.Label;
import team20.issuetracker.domain.milestone.Milestone;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Issue extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @Enumerated(value = EnumType.STRING)
    private IssueStatus status = IssueStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    @OneToMany(mappedBy = "issue")
    private List<IssueAssignee> issueAssignees = new ArrayList<>();

    @OneToMany(mappedBy = "issue")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "issue")
    private List<IssueLabel> issueLabels = new ArrayList<>();

    private Issue(String title, String content, Milestone milestone) {
        this.title = title;
        this.content = content;
        this.milestone = milestone;
    }

    public static Issue of(String title, String content, Milestone milestone) {
        return new Issue(title, content, milestone);
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