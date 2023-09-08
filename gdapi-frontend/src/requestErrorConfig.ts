import type { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';
import {message} from "antd";

// 错误处理方案： 错误类型
enum ErrorShowType {
  SILENT = 0,
  WARN_MESSAGE = 1,
  ERROR_MESSAGE = 2,
  NOTIFICATION = 3,
  REDIRECT = 9,
}
// 与后端约定的响应数据格式
interface ResponseStructure {
  success: boolean;
  data: any;
  errorCode?: number;
  errorMessage?: string;
  showType?: ErrorShowType;
}

/**
 * @name 错误处理
 * pro 自带的错误处理， 可以在这里做自己的改动
 * @doc https://umijs.org/docs/max/request#配置
 */
export const errorConfig: RequestConfig = {

  baseURL: 'http://localhost:8090',
  withCredentials: true,



  // 请求拦截器
  requestInterceptors: [
    (config: RequestOptions) => {
    console.log(config)
      // 拦截请求配置，进行个性化处理。
      const url = config?.url?.concat('?token = gaoda');

      const token = sessionStorage.getItem("token");


      const headers = {
        ...config.headers,
        'x-auth-token' : token
      };
      return { ...config, url , headers: headers};
      // const url = config?.url;
      // return { ...config, url };
    },
  ],
//
//   // 响应拦截器
//   responseInterceptors: [
//     (response) => {
//       // 拦截响应数据，进行个性化处理
//       const { data } = response as unknown as ResponseStructure;
//       console.log("data", data);
//       if (data.code !== 0) {
//         throw new Error('响应错误, '+data.message);
//       }
//       return response;
//     },
//   ],
// };

  // 请求拦截器
  // requestInterceptors: [
  //   (config: RequestOptions) => {
  //     // 拦截请求配置，进行个性化处理。
  //     const url = config?.url;
  //     return { ...config, url };
  //   },
  // ],

  // 响应拦截器
  responseInterceptors: [
    (response) => {
      // 拦截响应数据，进行个性化处理
      const { data } = response as unknown as ResponseStructure;
      if (
        data.code !== 0 &&
        response.headers['content-type'] !== 'image/jpeg' &&
        response.headers['content-type'] !== 'text/html;charset=utf-8' &&
        response.headers['content-type'] !== 'application/octet-stream'&&
        response.headers['content-type'] !== 'application/vnd.ms-excel;charset=utf-8'
      ) {
          if (
          data.code === 40100 ||
          data.code === 40301 ||
          data.code === 40300 ||
          data.code === 40101
        )
        {
          history.push('/user/login');
          //删除 cookie
          // document.cookie = 'authorization=;expires=Thu, 01 Jan 1970 00:00:00 GMT';
          // localStorage.removeItem("api-open-platform-user")
          if (data.code ===40100){
            //未登录直接重定向到登录页面即可，不用输出页面错误信息，避免用户首次访问页面时也抛出错误信息，给用户带来不好的体验
            return response;
          }
        }
        message.error(data.message);
      }
      return response;
    },
  ],
};
