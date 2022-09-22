import { ILabel } from '@/types/labelTypes';
import { MilestoneType } from '@/types/milestoneTypes';

export type IssueStatusType = 'open' | 'closed';

export type ApiType = 'issues' | 'labels' | 'milestones' | 'assignees';

export type Assignee = {
  id: number;
  userId: string;
  image: string;
};

export type AssigneeDataType = {
  assignees: Assignee[];
};

export type CommentDataType = {
  author: string;
  image: string;
  content: string;
  createdAt: string;
};

type Comment = CommentDataType & {
  id: number;
};

export type IssueEditDataType = {
  title: string;
  author: string;
  createdAt: string;
  image: string;
  content: string;
};

export type IssueType = IssueEditDataType & {
  id: number;
  commentCount: number;
  milestones: MilestoneType[] | [];
  comments: Comment[] | [];
  labels: ILabel[] | [];
  assignees: Assignee[] | [];
  issueStatus: IssueStatusType;
};
export interface IssuesDataType {
  openIssueCount: number;
  closedIssueCount: number;
  issues: IssueType[] | [];
}

export interface IssueFilter {
  is: IssueStatusType;
  title: string | undefined;
  author: string | undefined;
  label: string[];
  milestone: string | undefined;
  assignee: string | undefined;
  commentedBy: string | undefined;
}
