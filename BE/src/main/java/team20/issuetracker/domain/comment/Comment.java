package team20.issuetracker.domain.comment;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team20.issuetracker.domain.AuditingFields;
import team20.issuetracker.domain.issue.Issue;
import team20.issuetracker.service.dto.request.RequestCommentDto;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String image;
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;


    private Comment(String memberName, String image, String content, Issue issue) {
        this.author = memberName;
        this.image = image;
        this.content = content;
        this.issue = issue;
    }

    public static Comment of(String memberName, String profileImageUrl, String content, Issue issue) {
        return new Comment(memberName, profileImageUrl, content, issue);
    }

    public Long update(RequestCommentDto requestCommentDto) {
        this.content = requestCommentDto.getContent();
        return this.id;
    }
}

