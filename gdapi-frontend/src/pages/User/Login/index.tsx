import Footer from '@/components/Footer';
import { getFakeCaptcha } from '@/services/ant-design-pro/login';
import {
  AlipayCircleOutlined,
  LockOutlined,
  MobileOutlined,
  TaobaoCircleOutlined,
  UserOutlined,
  WeiboCircleOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormCaptcha,
  ProFormText,
} from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import {Helmet, history, Link, useModel} from '@umijs/max';
import {Alert, Divider, message, Tabs} from 'antd';
import React, { useState } from 'react';
import { flushSync } from 'react-dom';
import Settings from '../../../../config/defaultSettings';
import {
  userLoginUsingPOST,
  userLoginBySmsUsingPOST,
  sendCodeUsingGET,
  getCaptchaUsingGET
} from "@/services/gdapi-backend/userController";
import {AO_TE_MAN} from "@/constant";
import {randomStr} from "@antfu/utils";

//几个登录按钮
const ActionIcons = () => {
  const langClassName = useEmotionCss(({ token }) => {
    return {
      marginLeft: '8px',
      color: 'rgba(0, 0, 0, 0.2)',
      fontSize: '24px',
      verticalAlign: 'middle',
      cursor: 'pointer',
      transition: 'color 0.3s',
      '&:hover': {
        color: token.colorPrimaryActive,
      },
    };
  });
  return (
    <>
      <AlipayCircleOutlined key="AlipayCircleOutlined" className={langClassName} />
      <TaobaoCircleOutlined key="TaobaoCircleOutlined" className={langClassName} />
      <WeiboCircleOutlined key="WeiboCircleOutlined" className={langClassName} />
    </>
  );
};

const Lang = () => {
  const langClassName = useEmotionCss(({ token }) => {
    return {
      width: 42,
      height: 42,
      lineHeight: '42px',
      position: 'fixed',
      right: 16,
      borderRadius: token.borderRadius,
      ':hover': {
        backgroundColor: token.colorBgTextHover,
      },
    };
  });
  return;
};
const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => {
  return (
    <Alert
      style={{
        marginBottom: 24,
      }}
      message={content}
      type="error"
      showIcon
    />
  );
};
const Login: React.FC = () => {

  const [registerLoading,setRegisterLoading] = useState(false)
  const [loginLoading ,setLoginLoading] = useState(false)
  const [imageUrl,setImageUrl] =useState<any>(null);

  const [userLoginState, setUserLoginState] = useState<API.LoginResult>({});
  const [type, setType] = useState<string>('account');
  const { initialState, setInitialState } = useModel('@@initialState');
  const containerClassName = useEmotionCss(() => {
    return {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      backgroundImage:
        "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
      backgroundSize: '100% 100%',
    };
  });
  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();
    if (userInfo) {
      flushSync(() => {
        setInitialState((s) => ({
          ...s,
          currentUser: userInfo,
        }));
      });
    }
  };

  /**
   * 跳转注册账号表单
   */
  const register = async () =>{
    await getCaptcha()
    setType("register")
    setRegisterLoading(false)
  }

  /**
   * 延迟动画价值
   * @param time
   */
  const waitTime = (time: number = 100) => {
    return new Promise((resolve) => {
      setLoginLoading(true)
      setTimeout(() => {
        resolve(true);
      }, time);
    });
  };
  /**
   * 获取图形验证码
   */
  const getCaptcha = async () =>{
    let randomString
    const temp = localStorage.getItem("api-open-platform-randomString")
    if (temp){
      randomString = temp
    }else {
      randomString = randomStr(32, '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ');
      localStorage.setItem("api-open-platform-randomString",randomString)
    }
    //携带浏览器请求标识
    const  res = await getCaptchaUsingGET({
      headers: {
        "signature": randomString
      },
      responseType: 'blob' //必须指定为'blob'
    })
    let url = window.URL.createObjectURL(res)
    setImageUrl(url)
  }

  const handleSubmit = async (values: API.UserLoginRequest) => {
    try {
      let res;
      if (type === 'account') {
        res = await userLoginUsingPOST({...values});
      } else {
        res = await userLoginBySmsUsingPOST({...values});
      }
      console.log(res)
      if (res.data) {
        const urlParams = new URL(window.location.href).searchParams;
        history.push(urlParams.get('redirect') || '/');
        setInitialState({
          loginUser: res.data
        });
        const userToken = res.data.userToken.toString();
        sessionStorage.setItem("token", userToken);
        //return;
      }

    } catch (error) {
      const defaultLoginFailureMessage = '登录失败，请重试！';
      console.log(error);
      message.error(defaultLoginFailureMessage);
    }
  };
  const { status, type: loginType } = userLoginState;
  return (
    <div className={containerClassName}>
      <Helmet>
        <title>
          {'登录'}- {Settings.title}
        </title>
      </Helmet>
      <Lang />
      <div
        style={{
          flex: '1',
          padding: '32px 0',
        }}
      >
        <LoginForm
          contentStyle={{
            minWidth: 280,
            maxWidth: '75vw',
          }}
          logo={<img alt="logo" src="/cute.svg" />}
          title="GDApi开放平台"
          subTitle={' '}
          initialValues={{
            autoLogin: true,
          }}
          actions={['其他登录方式 :', <ActionIcons key="icons" />]}
          onFinish={async (values) => {
            await handleSubmit(values as API.UserLoginRequest);
          }}
        >
          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'account',
                label: '账号密码登录',
              },
              {
                key: 'email',
                label: '邮箱登录',
              },
            ]}
          />

          {status === 'error' && loginType === 'account' && (
            <LoginMessage content={'错误的账号和密码'} />
          )}
          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                placeholder={'账号:'}
                rules={[
                  {
                    required: true,
                    message: '账号是必填项！',
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder={'密码:'}
                rules={[
                  {
                    required: true,
                    message: '密码是必填项！',
                  },
                  {
                    min: 8,
                    type: 'string',
                    message: '密码长度不能小于8! ',
                  }
                ]}
              />
            </>
          )}

          {status === 'error' && loginType === 'email' && <LoginMessage content="验证码错误" />}
          {type === 'email' && (
            <>
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <MobileOutlined />,
                }}
                name="emailNum"
                placeholder={'请输入邮箱！'}
                rules={[
                  {
                    required: true,
                    message: '邮箱是必填项！',
                  },
                  {
                    // pattern: /^1\d{10}$/,    手机号码正则表达式
                    pattern: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
                    message: '不合法的邮箱！',
                  },
                ]}
              />
              <ProFormCaptcha
                fieldProps={{
                  autoComplete:"new-password",
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                captchaProps={{
                  size: 'large',
                }}
                placeholder={'请输入验证码！'}
                captchaTextRender={(timing, count) => {
                  if (timing) {
                    return `${count} ${'秒后重新获取'}`;
                  }
                  return '获取验证码';
                }}
                name="emailCaptcha"
                phoneName="emailNum"
                rules={[
                  {
                    required: true,
                    message: '验证码是必填项！',
                  },
                ]}
                onGetCaptcha={async (emailNum) => {
                  const captchaType:string = 'login';
                  const result = await sendCodeUsingGET({
                    emailNum,
                    captchaType,
                  });
                  if (result.data === false) {
                    return;
                  }
                  message.success(result.data);
                }}



              />
            </>
          )}



          <div
            style={{
              marginBottom: 24,
            }}
          >

            <Divider></Divider>
            <Link to = "/user/register">新用户注册</Link>
            <Divider  type={"vertical"}></Divider>
            <a
              style={{
                float: 'right',
              }}
              href={AO_TE_MAN}

              target={"_blank"} rel="noreferrer"
            >
              忘记密码请找奥特曼
            </a>
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};
export default Login;
