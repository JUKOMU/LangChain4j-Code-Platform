<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { deleteApp, getApps, updateApp } from '@/api/appController'
import { formatDateTime, getCodeGenTypeLabel } from '@/utils/app'

const router = useRouter()

const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '应用名称', dataIndex: 'appName' },
  { title: '封面', dataIndex: 'cover' },
  { title: '生成类型', dataIndex: 'codeGenType' },
  { title: '部署 Key', dataIndex: 'deployKey' },
  { title: '优先级', dataIndex: 'priority' },
  { title: '用户 ID', dataIndex: 'userId' },
  { title: '创建时间', dataIndex: 'createTime' },
  { title: '操作', key: 'action' },
]

const data = ref<API.AppVo[]>([])
const total = ref(0)
const searchParams = reactive<API.AppAdminQueryDto>({
  pageNum: 1,
  pageSize: 10,
})

const pagination = computed(() => ({
  current: searchParams.pageNum ?? 1,
  pageSize: searchParams.pageSize ?? 10,
  total: total.value,
  showSizeChanger: true,
  showTotal: (value: number) => `共 ${value} 条`,
}))

const fetchData = async () => {
  const res = await getApps({ ...searchParams })
  if (res.data.code === 200 && res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
    return
  }
  message.error(res.data.message || '获取应用列表失败')
}

const doSearch = () => {
  searchParams.pageNum = 1
  fetchData()
}

const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

const doDelete = async (id?: number) => {
  if (!id) {
    return
  }
  const res = await deleteApp({ id })
  if (res.data.code === 200) {
    message.success('删除成功')
    fetchData()
  } else {
    message.error(res.data.message || '删除失败')
  }
}

const doFeature = async (record: API.AppVo) => {
  if (!record.id) {
    return
  }
  const res = await updateApp({
    id: record.id,
    appName: record.appName,
    cover: record.cover,
    priority: 99,
  })
  if (res.data.code === 200) {
    message.success('已设为精选')
    fetchData()
  } else {
    message.error(res.data.message || '设置精选失败')
  }
}

const goEdit = (id?: number) => {
  if (!id) {
    return
  }
  router.push(`/app/admin/edit/${id}`)
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="app-manage-page">
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="ID">
        <a-input-number v-model:value="searchParams.id" style="width: 180px" placeholder="应用 ID" />
      </a-form-item>
      <a-form-item label="应用名称">
        <a-input v-model:value="searchParams.appName" placeholder="应用名称" />
      </a-form-item>
      <a-form-item label="生成类型">
        <a-select v-model:value="searchParams.codeGenType" allow-clear placeholder="生成类型" style="width: 180px">
          <a-select-option value="html">单页面</a-select-option>
          <a-select-option value="multi_file">多文件结构</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="部署 Key">
        <a-input v-model:value="searchParams.deployKey" placeholder="部署 Key" />
      </a-form-item>
      <a-form-item label="优先级">
        <a-input-number v-model:value="searchParams.priority" style="width: 160px" placeholder="优先级" />
      </a-form-item>
      <a-form-item label="用户 ID">
        <a-input-number v-model:value="searchParams.userId" style="width: 160px" placeholder="用户 ID" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>

    <a-divider />

    <a-table :columns="columns" :data-source="data" :pagination="pagination" row-key="id" @change="doTableChange">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'cover'">
          <a-image v-if="record.cover" :src="record.cover" :width="96" />
          <span v-else>-</span>
        </template>
        <template v-else-if="column.dataIndex === 'codeGenType'">
          {{ getCodeGenTypeLabel(record.codeGenType) }}
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ formatDateTime(record.createTime) }}
        </template>
        <template v-else-if="column.key === 'action'">
          <div class="table-actions">
            <a-button type="link" @click="goEdit(record.id)">编辑</a-button>
            <a-popconfirm title="确认删除该应用？" ok-text="删除" cancel-text="取消" @confirm="doDelete(record.id)">
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
            <a-button type="link" @click="doFeature(record)">精选</a-button>
          </div>
        </template>
      </template>
    </a-table>
  </div>
</template>

<style scoped>
.app-manage-page {
  padding: 24px;
  background: white;
  margin-top: 16px;
  border-radius: 24px;
}

.table-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
