import Footer from '@/components/Footer';
import { getFakeCaptcha } from '@/services/ant-design-pro/login';
import {
  AlipayCircleOutlined, ArrowRightOutlined,
  LockOutlined,
  MobileOutlined,
  TaobaoCircleOutlined,
  UserOutlined,
  WeiboCircleOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormCaptcha,
  ProFormCheckbox,
  ProFormText,
} from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { Helmet, history, useModel } from '@umijs/max';
import {Alert, Divider, message, Tabs} from 'antd';
import React, { useState } from 'react';
import { flushSync } from 'react-dom';
import Settings from '../../../../config/defaultSettings';
import {
  userLoginUsingPOST,
  userRegisterUsingPOST,
  getCaptchaUsingGET,
  sendCodeUsingGET, userEmailRegisterUsingPOST
} from "@/services/gdapi-backend/userController";
import {AO_TE_MAN} from "@/constant";
import {Link} from "umi";
import { randomStr } from '@antfu/utils';

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
const Register: React.FC = () => {
  const [userLoginState, setUserLoginState] = useState<API.LoginResult>({});
  const [type, setType] = useState<string>('register');
  const { initialState, setInitialState } = useModel('@@initialState');


  const [imageUrl, setImageUrl] = useState<any>(null);

  /**
   * 获取图形验证码
   */
  const getCaptcha = async () => {
    let randomString;
    const temp = localStorage.getItem('api-open-platform-randomString');
    if (temp) {
      randomString = temp;
    } else {
      randomString = randomStr(
        32,
        '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ',
      );
      localStorage.setItem('api-open-platform-randomString', randomString);
    }
    console.log(randomString)
    //携带浏览器请求标识
    const res = await getCaptchaUsingGET({
      headers: {
        signature: randomString
      },
      responseType: 'blob', //必须指定为'blob'
    });
    let url = window.URL.createObjectURL(res);
    setImageUrl(url);
  };


  React.useEffect(() => {

    const fetchData = async () => {
      await getCaptcha();
    }
    fetchData();

    // 空数组只调用一次
    return () => {};

  }, []);






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
  const handleSubmit = async (values: API.UserRegisterRequest) => {
    const {userAccount, userPassword, checkPassword } = values;
    if (userPassword !== checkPassword){
      message.error('用户密码两次输入不一致!');
      return;
    }
    try {
      // 注册
      const signature = localStorage.getItem("api-open-platform-randomString")
      let res;
      if (type === 'register') {
        res = await userRegisterUsingPOST(values,{
          headers: {
            "signature": signature
          },
        });
      } else {
        res = await userEmailRegisterUsingPOST({...values});
      }
      console.log(res);
      if (res.data) {
        const defaultRegisterSuccessMessage = '注册成功！';
        message.success(defaultRegisterSuccessMessage);

        /** 此方法会跳转到 redirect 参数所在的位置 */
        if (!history) return;
        const { query } = history.location;

        history.push({
          pathname: '/user/login',
          query,
        });
        return;
      }
    } catch (error: any) {
      const defaultRegisterFailureMessage = '注册失败，请重试！';
      message.error(defaultRegisterFailureMessage);
    }
  };
  const { status, type: loginType } = userLoginState;
  return (
    <div className={containerClassName}>
      <Helmet>
        <title>
          {'注册'}- {Settings.title}
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
          submitter={{
            searchConfig: {
              submitText: '注册'
            }
          }}

          logo={<img alt="logo" src="/silly.svg" />}
          title="GDApi开放平台"
          subTitle={' '}
          initialValues={{
            autoLogin: true,
          }}

          onFinish={async (values) => {
            await handleSubmit(values as API.UserRegisterRequest);
          }}
        >
          <Tabs activeKey={type} onChange={setType} centered items={[ {
            key: 'register',
            label: '账号密码注册',
          },
            {
              key: 'emailRegister',
              label: 'QQ邮箱注册',
            },]} />
          {status === 'error' && loginType === 'register' && (
            <LoginMessage content={'验证码错误!'} />
          )}
          {type === 'register' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                placeholder={'请输入您的账号'}
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
                placeholder={'请输入密码'}
                rules={[
                  {
                    required: true,
                    message: '密码是必填项！',
                  },
                  {
                    min: 8,
                    type: 'string',
                    message: '长度不能小于8',
                  }
                ]}
              />

              <ProFormText.Password
                name="checkPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder={'请再次输入密码*'}
                rules={[
                  {
                    required: true,
                    message: '确认密码是必填项！',
                  },
                  {
                    min: 8,
                    type: 'string',
                    message: '长度不能小于8',
                  }
                ]}
              />

              <ProFormText
                name="vipCode"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                placeholder={'请输入您的vip编号'}
                rules={[
                  {
                    required: true,
                    message: 'vip编号是必填项！',
                  },
                ]}
              />

              <div style={{ display: 'flex' }}>
                <ProFormText
                  fieldProps={{
                    autoComplete:"off",
                    size: 'large',
                    prefix: <ArrowRightOutlined className={'prefixIcon'} />,
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
                <img
                  src={imageUrl}
                  onClick={getCaptcha}
                  style={{ marginLeft: 18 }}
                  width="100px"
                  height="39px"
                />
              </div>

            </>
          )}
          {status === 'error' && loginType === 'emailRegister' && (
            <LoginMessage content={'验证码错误!'} />
          )}
          {type === 'emailRegister' && (
            <>
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <MobileOutlined   />,
                }}
                name="emailNum"
                placeholder={'请输入QQ邮箱！'}
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
                  size: 'large',
                  prefix: <LockOutlined  />,
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
                  const captchaType:string = 'register';
                  const result = await sendCodeUsingGET({
                    emailNum,
                    captchaType,
                  });
                  if (result.data === false) {
                    return;
                  }
                  message.success(result.message);
                }}
              />
            </>
          )}




          <div
            style={{
              marginBottom: 24,
            }}
          >
            <Link
              style={{
                marginBottom: 24,
                float: 'right'
              }}
              to={'/user/login'}
            >
              已有帐号，去登陆！
            </Link>
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};
export default Register;
