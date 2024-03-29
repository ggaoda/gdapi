import { PageContainer } from '@ant-design/pro-components';

import React, {useEffect, useState} from 'react';
import {Button, Card, Descriptions, Divider, Form, Input, InputNumber, message, Modal} from "antd";
import {
  getInterfaceInfoByIdUsingGET, invokeInterfaceInfoUsingPOST
} from "@/services/gdapi-backend/interfaceInfoController";
import {useModel, useParams} from "@@/exports";
import {values} from "lodash";
import {addOrderUsingPOST} from "@/api/api-order/orderController";
import {getFreeInterfaceCountUsingPOST} from "@/services/gdapi-backend/userInterfaceInfoController";


/**
 * 主页
 * @constructor
 */
const Index: React.FC = () => {

    const [loading, setLoading] = useState(false);
    const [data, setData] = useState<API.InterfaceInfoVO>();
    const params = useParams();
    const [invokeres, setInvokeres] = useState<any>();
    const [invokeLoading,setinvokeLoading] = useState(false);
    const [totalAmount, setTotalAmount] = useState(1.0);
    const [orderCount, setOrderCount] = useState(1);
    const [orderModalOpen, setAddOrderModalOpen] = useState(false);
    const { initialState, setInitialState } = useModel('@@initialState');
    const { loginUser } = initialState;



  const showAddOrderModal = () => {
    setTotalAmount(parseFloat((orderCount * parseFloat(data?.charging)).toFixed(2)));
    setAddOrderModalOpen(true);
  };

  const handleAddOrderOk = async () => {
    setLoading(true);
    try {
      const res = await addOrderUsingPOST({
        interfaceId: data.id,
        count: orderCount,
        userId: loginUser.id,
        totalAmount: totalAmount,
        charging: data.charging,
        chargingId: data.chargingId,
      });
      if (res.code === 0) {
        message.success('订单创建成功');
      }
    } catch (e: any) {
      message.error('请求失败，' + e.message);
    }
    setLoading(false);
    loadData();
    setAddOrderModalOpen(false);
  };

  const handleAddOrderCancel = () => {
    setAddOrderModalOpen(false);
  };

  const onChangeOrderCount = (value: number) => {
    setOrderCount(value);
    setTotalAmount(parseFloat((value * parseFloat(data?.charging)).toFixed(2)));
    console.log('changed', value);
  };


    const loadData = async () =>{
      if (!params.id){
        message.error('参数不存在');
        return;
      }
      setLoading(true);
      try {
        const res = await getInterfaceInfoByIdUsingGET({
          id: Number(params.id)
        });
        console.log(res.data)
        setData(res.data);
      } catch (error: any){
        message.error('请求失败, '+error.message);
        return false;
      }
      setLoading(false);
      return;
    }
    useEffect(() => {
      loadData();
    }, [])

    const onFinish = async (values: any) => {
      if (!params.id){
        message.error('接口不存在!');
        return;
      }

      if(data?.availablePieces === null){
        console.log(data?.availablePieces)
        message.error('接口调用次数不足，请先获取或者购买');
        return true;
      }


      try {
        setinvokeLoading(true);
        const res = await invokeInterfaceInfoUsingPOST({
          id: params.id,
          ...values
        });
        setInvokeres(res.data);
        message.success('请求成功!');
      } catch (error: any){
        message.error('操作失败, '+error.message);
      }
      setinvokeLoading(false);
      return;
    }


  const getFreeInterface = async () => {
    setinvokeLoading(true);
    try {
      const res = await getFreeInterfaceCountUsingPOST({
        userId: loginUser.id,
        interfaceId: data?.id,
        lockNum: 100,
      });
      if (res.data) {
        message.success('获取调用次数成功');
      } else {
        message.error('获取失败请重试');
      }
    } catch (e:any) {
      message.error('请求失败，' + e.message);
    }
    setinvokeLoading(false);
    loadData();
    return
  };



  return (
    <>
    <PageContainer title="查看接口文档">
      <Card loading={loading}>
        {data? (<Descriptions title={data?.name} column={1} extra={data.charging?(<Button onClick={showAddOrderModal}>购买</Button>)
          :(<Button onClick={getFreeInterface}>获取</Button>) }>
          <Descriptions.Item label="描述">{data.description}</Descriptions.Item>
          <Descriptions.Item label="接口状态">{data.status? '正常': '关闭'}</Descriptions.Item>
          {data.charging ? (
              <>
                <Descriptions.Item label="计费">{data.charging} 元 / 条</Descriptions.Item>
                <Descriptions.Item label="接口剩余调用次数">
                  {data.availablePieces === null ? '0':data.availablePieces}次
                </Descriptions.Item>
              </>
            ) :
            <Descriptions.Item label="接口剩余调用次数">
              {data.availablePieces === null ? '0':data.availablePieces}次
            </Descriptions.Item>
          }
          <Descriptions.Item label="请求地址">{data.url}</Descriptions.Item>
          <Descriptions.Item label="请求方法">{data.method}</Descriptions.Item>
          <Descriptions.Item label="请求参数">{data.requestParams===null ?'无':data.requestParams}</Descriptions.Item>
          <Descriptions.Item label="请求头">{data.requestHeader===null ?'无':data.requestHeader}</Descriptions.Item>
          <Descriptions.Item label="响应头">{data.responseHeader===null ?'无':data.responseHeader}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{data.createTime}</Descriptions.Item>
          <Descriptions.Item label="更新时间">{data.updateTime}</Descriptions.Item>
        </Descriptions>):(<>接口不存在!</>)
        }
      </Card>
      <Divider />
      <Card title="在线测试">
        <Form name="invoke" layout="vertical"
              onFinish={onFinish}>
          <Form.Item label="请求参数" name="userRequestParams">
            <Input.TextArea />
          </Form.Item>
          <Form.Item wrapperCol={{span: 16}}>
            <Button type="primary" htmlType="submit">
              调用
            </Button>
          </Form.Item>
        </Form>
      </Card>
      <Divider />
      <Card title="返回结果" loading={invokeLoading}>
        {invokeres}
      </Card>
    </PageContainer>

  <Modal
    title="购买接口"
    open={orderModalOpen}
    onOk={handleAddOrderOk}
    onCancel={handleAddOrderCancel}
  >
    <Descriptions title={data?.name} size="small" layout="vertical" bordered>
      <Descriptions.Item label="接口状态">{data?.status ? '正常' : '关闭'}</Descriptions.Item>
      <Descriptions.Item label="计费">{data?.charging} 元 / 次</Descriptions.Item>
      <Descriptions.Item label="接口剩余调用次数">{data?.availablePieces}次</Descriptions.Item>
      <Descriptions.Item label="请求方法">{data?.method}</Descriptions.Item>
      <Descriptions.Item label="请求地址">{data?.url}</Descriptions.Item>
      <Descriptions.Item label="购买次数">
        <InputNumber
          min={1}
          max={data?.availablePieces}
          defaultValue={1}
          onChange={onChangeOrderCount}
        />
      </Descriptions.Item>
    </Descriptions>
    <Divider />
    <b>总计：{totalAmount}元</b>
  </Modal>
    </>
  );
};

export default Index;
