// If you refresh the page or revisit the page, then the userState will be disappeared
// So this time, use Refresh Token to maintain the login state
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useSetRecoilState } from 'recoil';

import { REFRESH_TOKEN, REFRESH_TOKEN_OPTIONS } from '@/constants/login';
import { userState } from '@/stores/atoms/user';
import { getCookie, setCookie } from '@/utils/cookie';

const getUserToken = async () => {
  const refreshToken = getCookie(REFRESH_TOKEN);
  const response = await axios.post(`${process.env.OAUTH_URL_GITHUB}/maintain`, {
    refreshToken,
  });
  const data = await response.data;

  return data;
};

const useMaintainLogin = (path = '/login') => {
  const navigate = useNavigate();
  const setUserState = useSetRecoilState(userState);

  const maintainLogin = async () => {
    try {
      const { name, email, accessToken, refreshToken, profileImageUrl } = await getUserToken();
      setUserState({ name, email, profileImageUrl });
      setCookie(REFRESH_TOKEN, refreshToken, REFRESH_TOKEN_OPTIONS);
      axios.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
      navigate(path);
    } catch (error) {
      console.log(error);
      navigate('/login');
    }
  };

  return maintainLogin;
};

export default useMaintainLogin;
