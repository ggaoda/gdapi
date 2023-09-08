declare namespace API {
  type BaseResponse = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseInterfaceInfoVO_ = {
    code?: number;
    data?: InterfaceInfoVO;
    message?: string;
  };

  type BaseResponseListInterfaceInfo_ = {
    code?: number;
    data?: InterfaceInfo[];
    message?: string;
  };

  type BaseResponseListInterfaceInfoVO_ = {
    code?: number;
    data?: InterfaceInfoVO[];
    message?: string;
  };

  type BaseResponseListOrderVO_ = {
    code?: number;
    data?: OrderVO[];
    message?: string;
  };

  type BaseResponseListUserInterfaceInfo_ = {
    code?: number;
    data?: UserInterfaceInfo[];
    message?: string;
  };

  type BaseResponseListUserInterfaceInfoVO_ = {
    code?: number;
    data?: UserInterfaceInfoVO[];
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseObject_ = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponsePageInterfaceInfo_ = {
    code?: number;
    data?: PageInterfaceInfo_;
    message?: string;
  };

  type BaseResponsePageUserInterfaceInfo_ = {
    code?: number;
    data?: PageUserInterfaceInfo_;
    message?: string;
  };

  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserDevKeyVO_ = {
    code?: number;
    data?: UserDevKeyVO;
    message?: string;
  };

  type BaseResponseUserInterfaceInfo_ = {
    code?: number;
    data?: UserInterfaceInfo;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type DeleteRequest = {
    id?: number;
  };

  type getInterfaceInfoByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getInterfaceInfoByUserIdUsingGETParams = {
    /** userId */
    userId: number;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserInterfaceInfoByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type IdRequest = {
    id?: number;
  };

  type InterfaceInfo = {
    createTime?: string;
    description?: string;
    id?: number;
    isDeleted?: number;
    method?: string;
    name?: string;
    parameterExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    sdk?: string;
    status?: number;
    updateTime?: string;
    url?: string;
    userId?: number;
  };

  type InterfaceInfoAddRequest = {
    description?: string;
    id?: number;
    method?: string;
    name?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    url?: string;
    userId?: number;
  };

  type InterfaceInfoInvokeRequest = {
    id?: number;
    userRequestParams?: string;
  };

  type InterfaceInfoUpdateRequest = {
    description?: string;
    id?: number;
    isDeleted?: number;
    method?: string;
    name?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    status?: number;
    url?: string;
    userId?: number;
  };

  type InterfaceInfoVO = {
    availablePieces?: string;
    charging?: number;
    chargingId?: number;
    createTime?: string;
    description?: string;
    id?: number;
    isDeleted?: number;
    method?: string;
    name?: string;
    parameterExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    sdk?: string;
    status?: number;
    totalNum?: number;
    updateTime?: string;
    url?: string;
    userId?: number;
  };

  type listInterfaceInfoByPageUsingGETParams = {
    current?: number;
    description?: string;
    id?: number;
    method?: string;
    name?: string;
    pageSize?: number;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    url?: string;
    userId?: number;
  };

  type listInterfaceInfoUsingGETParams = {
    current?: number;
    description?: string;
    id?: number;
    method?: string;
    name?: string;
    pageSize?: number;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    url?: string;
    userId?: number;
  };

  type listUserByPageUsingGETParams = {
    createTime?: string;
    current?: number;
    id?: number;
    pageSize?: number;
    phoneNum?: string;
    role?: number;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userName?: string;
  };

  type listUserInterfaceInfoByPageUsingGETParams = {
    current?: number;
    id?: number;
    interfaceInfoId?: number;
    leftNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalNum?: number;
    userId?: number;
  };

  type listUserInterfaceInfoUsingGETParams = {
    current?: number;
    id?: number;
    interfaceInfoId?: number;
    leftNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalNum?: number;
    userId?: number;
  };

  type LoginUserVO = {
    avatarUrl?: string;
    createTime?: string;
    gender?: number;
    id?: number;
    role?: number;
    updateTime?: string;
    userAccount?: string;
    userToken?: string;
    username?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type OrderVO = {
    charging?: number;
    createTime?: string;
    expirationTime?: string;
    interfaceDesc?: string;
    interfaceId?: number;
    interfaceName?: string;
    orderNumber?: string;
    status?: number;
    total?: number;
    totalAmount?: number;
    userId?: number;
  };

  type PageInterfaceInfo_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: InterfaceInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserInterfaceInfo_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserInterfaceInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type sendCodeUsingGETParams = {
    /** captchaType */
    captchaType: string;
    /** emailNum */
    emailNum: string;
  };

  type UpdateUserInterfaceInfoDTO = {
    interfaceId?: number;
    lockNum?: number;
    userId?: number;
  };

  type User = {
    accessKey?: string;
    avatarUrl?: string;
    createTime?: string;
    email?: string;
    gender?: number;
    id?: number;
    isDelete?: number;
    phone?: string;
    role?: number;
    secretKey?: string;
    updateTime?: string;
    userAccount?: string;
    userPassword?: string;
    userStatus?: number;
    username?: string;
    vipCode?: string;
  };

  type UserAddRequest = {
    role?: number;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
  };

  type UserDevKeyVO = {
    accessKey?: string;
    secretKey?: string;
  };

  type UserInterfaceInfo = {
    createTime?: string;
    id?: number;
    interfaceInfoId?: number;
    isDelete?: number;
    leftNum?: number;
    status?: number;
    totalNum?: number;
    updateTime?: string;
    userId?: number;
  };

  type UserInterfaceInfoAddRequest = {
    interfaceInfoId?: number;
    leftNum?: number;
    totalNum?: number;
    userId?: number;
  };

  type UserInterfaceInfoUpdateRequest = {
    id?: number;
    leftNum?: number;
    status?: number;
    totalNum?: number;
  };

  type UserInterfaceInfoVO = {
    description?: string;
    id?: number;
    interfaceInfoId?: number;
    interfaceStatus?: number;
    leftNum?: number;
    method?: string;
    name?: string;
    status?: number;
    totalNum?: number;
    url?: string;
  };

  type UserLoginRequest = {
    emailCaptcha?: string;
    emailNum?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    createTime?: string;
    current?: number;
    id?: number;
    pageSize?: number;
    phoneNum?: string;
    role?: number;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userName?: string;
  };

  type UserRegisterRequest = {
    captcha?: string;
    checkPassword?: string;
    emailCaptcha?: string;
    emailNum?: string;
    phone?: string;
    userAccount?: string;
    userPassword?: string;
    vipCode?: string;
  };

  type UserUpdateMyRequest = {
    userAvatar?: string;
    userName?: string;
  };

  type UserUpdateRequest = {
    gender?: number;
    id?: number;
    role?: number;
    userAvatar?: string;
    userName?: string;
  };

  type UserVO = {
    avatarUrl?: string;
    createTime?: string;
    id?: number;
    role?: number;
    userToken?: string;
    username?: string;
  };
}
