import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import {
  FooterToolbar,
  PageContainer,
  ProDescriptions,

  ProTable,
} from '@ant-design/pro-components';
import '@umijs/max';
import { Button, Drawer, message } from 'antd';
import React, { useRef, useState } from 'react';
import CreateModal from './components/CreateModal';
import {SortOrder} from "antd/es/table/interface";
import {
  addInterfaceInfoUsingPOST,
  deleteInterfaceInfoUsingPOST,
  listInterfaceInfoByPageUsingGET,
  offlineInterfaceInfoUsingPOST,
  onlineInterfaceInfoUsingPOST,
  updateInterfaceInfoUsingPOST
} from "@/services/gdapi-backend/interfaceInfoController";
import UpdateModal from "@/pages/Admin/InterfaceInfo/components/UpdateModal";


const TableList: React.FC = () => {
  /**
   * @en-US Pop-up window of new window
   * @zh-CN 新建窗口的弹窗
   *  */
  const [createModalOpen, handleModalOpen] = useState<boolean>(false);
  /**
   * @en-US The pop-up window of the distribution update window
   * @zh-CN 分布更新窗口的弹窗
   * */
  const [updateModalOpen, handleUpdateModalOpen] = useState<boolean>(false);
  const [showDetail, setShowDetail] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.InterfaceInfo>();
  const [selectedRowsState, setSelectedRows] = useState<API.InterfaceInfo[]>([]);

  /**
   * @en-US Add node
   * @zh-CN 添加节点
   * @param fields
   */
  const handleAdd = async (fields: API.InterfaceInfo) => {
    const hide = message.loading('正在创建...');
    try {
      await addInterfaceInfoUsingPOST({
        ...fields,
      });
      hide();
      message.success('创建成功!');
      handleModalOpen(false);
      return true;
    } catch (error: any) {
      hide();
      message.error('创建失败,'+error.message);
      return false;
    }
  };

  /**
   *  Delete node
   * @zh-CN 删除节点
   *
   * @param selectedRows
   */
  const handleRemove = async (record: API.InterfaceInfo) => {
    const hide = message.loading('正在删除...');
    if (!record) return true;
    try {
      await deleteInterfaceInfoUsingPOST({
       id: record.id
      });
      hide();
      message.success('删除成功!');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('删除失败, '+error.message);
      return false;
    }
  };

  /**
   * @zh-CN 发布接口
   *
   * @param record
   */
  const handleOnline = async (record: API.IdRequest) => {
    const hide = message.loading('发布中...');
    if (!record) return true;
    try {
      await onlineInterfaceInfoUsingPOST({
        id: record.id
      });
      hide();
      message.success('上线成功!');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('上线失败, '+error.message);
      return false;
    }
  };

  /**
   * @zh-CN 下线接口
   *
   * @param record
   */
  const handleOffline = async (record: API.IdRequest) => {
    const hide = message.loading('下线中...');
    if (!record) return true;
    try {
      await offlineInterfaceInfoUsingPOST({
        id: record.id
      });
      hide();
      message.success('下线成功!');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('下线失败, '+error.message);
      return false;
    }
  };

  /**
   * @en-US Update node
   * @zh-CN 更新节点
   *
   * @param fields
   */
  const handleUpdate = async (fields: API.InterfaceInfo) => {
    if(!currentRow){
      return;
    }
      const hide = message.loading('修改中...');
    try {
      await updateInterfaceInfoUsingPOST({
        id: currentRow.id,
        ...fields
      });
      hide();
      message.success('操作成功!');
      return true;
    } catch (error: any) {
      hide();
      message.error('操作失败, '+error.message);
      return false;
    }
  };



  const columns: ProColumns<API.InterfaceInfo>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'index',
    },
    {
      title: '接口名称',
      dataIndex: 'name',
      valueType: 'text',
      formItemProps: {
        rules: [{
          required: true,
          message: '接口名称是必填项!',
        }]
      },
    },
    {
      title: '描述',
      dataIndex: 'description',
      valueType: 'textarea',
    },
    {
      title: '请求方法',
      dataIndex: 'method',
      valueType: 'text',
    },
    {
      title: 'url',
      dataIndex: 'url',
      valueType: 'text',
    },
    {
      title: '请求参数',
      dataIndex: 'requestParams',
      valueType: 'textarea',
    },
    {
      title: '请求头',
      dataIndex: 'requestHeader',
      valueType: 'textarea',
    },
    {
      title: '响应头',
      dataIndex: 'responseHeader',
      valueType: 'textarea',
    },
    {
      title: '状态',
      dataIndex: 'status',
      hideInForm: true,
      valueEnum: {
        0: {
          text: '关闭',
          status: 'Default',
        },
        1: {
          text: '开启',
          status: 'Processing',
        },
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          key="config1"
          onClick={() => {
            handleUpdateModalOpen(true);
            setCurrentRow(record);
          }}
        >
          修改
        </a>,

        record.status === 0? <a
          key="config2"
          onClick={() => {
            handleOnline(record);
          }}
        >
          发布
        </a> : null,
        record.status === 1? <Button
          type="text"
          danger
          key="config3"
          onClick={() => {
            handleOffline(record);
          }}
        >
          下线
        </Button>: null,

        <Button
          type="text"
          danger
          key="config4"
          onClick={() => {
            handleRemove(record);
          }}
        >
          删除
        </Button>,

      ],
    },
  ];
  return (
    <PageContainer>
      <ProTable<API.RuleListItem, API.PageParams>
        headerTitle={'查询表格'}
        actionRef={actionRef}
        rowKey="key"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              handleModalOpen(true);
            }}
          >
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={ async (params: U & {
          pageSize?: number;
          current?: number;
          keyword?: string;
        }, sort: Record<string, SortOrder>, filter: Record<string, (string | number)[] | null>) => {
          const res = await listInterfaceInfoByPageUsingGET({
            ...params
          })
          if (res?.data){
            return {
              data: res?.data.records || [],
              success: true,
              total: res.data.total,
            }
          } else {
            return {
              data: [],
              success: false,
              total: 0,
            }
          }

        }

        }
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => {
            setSelectedRows(selectedRows);
          },
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              项 &nbsp;&nbsp;
              <span>
                服务调用次数总计 {selectedRowsState.reduce((pre, item) => pre + item.callNo!, 0)} 万
              </span>
            </div>
          }
        >
          <Button
            onClick={async () => {
              await handleRemove(selectedRowsState);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            批量删除
          </Button>
          <Button type="primary">批量审批</Button>
        </FooterToolbar>
      )}

      <UpdateModal
        columns={columns}
        onSubmit={async (value) => {
          const success = await handleUpdate(value);
          if (success) {
            handleUpdateModalOpen(false);
            setCurrentRow(undefined);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        onCancel={() => {
          handleUpdateModalOpen(false);
          if (!showDetail) {
            setCurrentRow(undefined);
          }
        }}
        visible={updateModalOpen}
        values={currentRow || {}}
      />

      <Drawer
        width={600}
        open={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.name && (
          <ProDescriptions<API.RuleListItem>
            column={2}
            title={currentRow?.name}
            request={async () => ({
              data: currentRow || {},
            })}
            params={{
              id: currentRow?.name,
            }}
            columns={columns as ProDescriptionsItemProps<API.RuleListItem>[]}
          />
        )}
      </Drawer>
      <CreateModal columns={columns} onCancel={()=>{handleModalOpen(false)}} onSubmit={(values: API.InterfaceInfo)=>{handleAdd(values)}} visible={createModalOpen} />
    </PageContainer>
  );
};
export default TableList;
