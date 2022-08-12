import { useParams } from 'react-router-dom';

import * as S from './style';

import Comment from '@/components/Comment';
import CommentForm from '@/components/CommentForm';
import Loading from '@/components/common/Loading';
import IssueDetailHeader from '@/components/IssueDetailHeader';
import SideBar from '@/components/SideBar';
import { useGetIssueDetail } from '@/hooks/useIssue';
import { ColumnWrapper } from '@/styles/common';

// TODO: CommentForm onSubmit - 댓글 등록 기능
const IssueDetail = () => {
  const { id } = useParams();
  const { data, isLoading } = useGetIssueDetail(Number(id));

  return (
    <ColumnWrapper>
      {isLoading || !data ? (
        <Loading />
      ) : (
        <>
          <IssueDetailHeader data={data} />
          <S.ContentsWrapper>
            <S.CommentsConatiner>
              <Comment
                issueAuthor={data.author}
                imgUrl={data.image}
                userId={data.author}
                createdAt={data.createdAt}
                description={data.content}
              />
              {data.comments.map(({ id: commentId, author, image, content, createdAt }) => (
                <Comment
                  key={commentId}
                  imgUrl={image}
                  createdAt={createdAt}
                  description={content}
                  userId={author}
                  issueAuthor={data.author === author && author}
                />
              ))}
              <CommentForm />
            </S.CommentsConatiner>
            <SideBar assignees={data.assignees} labels={data.labels} milestone={data.milestone} />
          </S.ContentsWrapper>
        </>
      )}
    </ColumnWrapper>
  );
};

export default IssueDetail;
