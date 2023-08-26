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
import {userLoginUsingPOST} from "@/services/gdapi-backend/userController";
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
      // 登录
      const res = await userLoginUsingPOST({
        ...values,
      });
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
                key: 'mobile',
                label: '手机号登录',
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

          {status === 'error' && loginType === 'mobile' && <LoginMessage content="验证码错误" />}
          {type === 'mobile' && (
            <>
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <MobileOutlined />,
                }}
                name="mobile"
                placeholder={'请输入手机号！'}
                rules={[
                  {
                    required: true,
                    message: '手机号是必填项！',
                  },
                  {
                    pattern: /^1\d{10}$/,
                    message: '不合法的手机号！',
                  },
                ]}
              />
              <ProFormCaptcha
                fieldProps={{
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
                name="captcha"
                rules={[
                  {
                    required: true,
                    message: '验证码是必填项！',
                  },
                ]}
                onGetCaptcha={async (phone) => {
                  const result = await getFakeCaptcha({
                    phone,
                  });
                  if (!result) {
                    return;
                  }
                  message.success('获取验证码成功！验证码为：1234');
                }}






              />
            </>
          )}


          {type === 'register' && (
            <>
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                name="userAccount"
                placeholder={'账号'}
                rules={[
                  {
                    required: true,
                    message: '请输入账号！',
                  },
                  {
                    min:4,
                    message:'账号长度不能小于4'
                  }
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder={intl.formatMessage({
                  id: 'pages.login.password.placeholder',
                  defaultMessage: '密码',
                })}
                rules={[
                  {
                    required: true,
                    message: (
                      <FormattedMessage
                        id="pages.login.password.required"
                        defaultMessage="请输入密码！"
                      />
                    ),
                  },
                  {
                    min:8,
                    message:'密码长度不能小于8'
                  }
                ]}
              />
              <ProFormText.Password
                name="checkPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder={intl.formatMessage({
                  id: 'pages.login.password.placeholder',
                  defaultMessage: '确认密码',
                })}
                rules={[
                  {
                    validator(role,value){
                      if (value !==formRef.current?.getFieldValue("userPassword")){
                        return Promise.reject("两次密码输入不一致")
                      }
                      return Promise.resolve()
                    },
                  }
                ]}
              />
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <MobileOutlined className={'prefixIcon'} />,
                }}
                name="mobile"
                placeholder={'手机号'}
                rules={[
                  {
                    required: true,
                    message: '请输入手机号！',
                  },
                  {
                    pattern: /^1[3-9]\d{9}$/,
                    message: '手机号格式错误！',
                  },
                ]}
              />
              <ProFormCaptcha
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={'prefixIcon'} />,
                }}
                captchaProps={{
                  size: 'large',
                }}
                placeholder={'请输入验证码'}
                captchaTextRender={(timing, count) => {
                  if (timing) {
                    return `${count} ${'后重新获取'}`;
                  }
                  return '获取验证码';
                }}
                name="code"
                // 手机号的 name，onGetCaptcha 会注入这个值
                phoneName="mobile"
                rules={[
                  {
                    required: true,
                    message: '请输入验证码！',
                  },
                  {
                    pattern: /^[0-9]\d{4}$/,
                    message: '验证码格式错误！',
                  },
                ]}
                onGetCaptcha={async (mobile) => {
                  //获取验证成功后才会进行倒计时
                  try {
                    const result = await captchaUsingGET({
                      mobile,
                    });
                    if (!result) {
                      return;
                    }
                    message.success(result.data);
                  }catch (e) {
                  }
                }}
              />
              <div style={{display:"flex"}}>
                <ProFormText
                  fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined className={'prefixIcon'} />,
                  }}
                  name="captcha"
                  placeholder={'请输入右侧验证码'}
                  rules={[
                    {
                      required: true,
                      message: '请输入图形验证码！',
                    },
                    {
                      pattern: /^[0-9]\d{3}$/,
                      message: '验证码格式错误！',
                    },
                  ]}
                />
                <img src={imageUrl} onClick={getCaptcha} style={{marginLeft:18}} width="100px" height="39px"/>
              </div>
              <Vertify
                width={320}
                height={160}
                visible={visible}
                // 默认可以不用设置
                // imgUrl={'/失落深渊葬礼2_4k_b1c03.jpg'}
                onSuccess={handleRegisterSubmit}
                // onFail={() => alert('fail')}
                // onRefresh={() => alert('refresh')}
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
