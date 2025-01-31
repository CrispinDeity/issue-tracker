import { useMutation, useQueryClient, useQuery } from 'react-query';

import { fetchAPI } from '@/apis/common';
import { deleteMilestone } from '@/apis/milestoneApi';
import { API } from '@/constants/api';
import { MilestoneDataType, MilestoneStatus } from '@/types/milestoneTypes';

const useGetMilestone = (milestoneStatus: MilestoneStatus) => {
  return useQuery([API.MILESTONES, milestoneStatus], () =>
    fetchAPI<MilestoneDataType>(API.MILESTONES, { is: milestoneStatus }),
  );
};

const useDeleteMilestone = (id: number) => {
  const queryClient = useQueryClient();
  return useMutation(() => deleteMilestone(id), {
    onSuccess: () => {
      queryClient.invalidateQueries('milestones');
    },
  });
};

const useRefetchMilestone = () => {
  const queryClient = useQueryClient();
  return useMutation(() => fetchAPI('milestones'), {
    onSuccess: () => {
      queryClient.invalidateQueries('milestones');
    },
  });
};

const useGetAllMilestone = () => {
  return useQuery([API.MILESTONES], () => fetchAPI<MilestoneDataType>(API.MILESTONES));
};

export { useGetMilestone, useDeleteMilestone, useRefetchMilestone, useGetAllMilestone };
