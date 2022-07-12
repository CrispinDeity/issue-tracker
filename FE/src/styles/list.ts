import styled from 'styled-components';

import { FONT_MIXIN, mixins } from '@/styles/mixins';

type ListStyleType = {
  type?: 'large';
};

const ListHeader = styled.div<ListStyleType>`
  ${FONT_MIXIN.small(400)}
  ${mixins.flexBox({ justifyContent: 'space-between' })};
  padding: ${({ type }) => (type === 'large' ? '15px 20px' : '15px')};
  color: ${({ theme: { color }, type }) =>
    type === 'large' ? color.text : color.cell.font.initial};
  background: ${({ theme: { color } }) => color.cell.bg.cellHeaderBg};
`;

const Item = styled.li<ListStyleType>`
  ${mixins.flexBox({ justifyContent: 'space-between' })}
  border-top: 1px solid ${({ theme: { color } }) => color.line};
  padding: ${({ type }) => (type === 'large' ? '15px 20px' : '5px 15px')};

  :hover {
    background-color: ${({ theme: { color } }) => color.cell.bg.cellHeaderBg};
  }
`;

const ListContainer = styled.div`
  margin: 24px 0;
  border: 1px solid ${({ theme: { color } }) => color.line};
  border-radius: 16px;
  overflow: hidden;
`;

export { ListHeader, Item, ListContainer };