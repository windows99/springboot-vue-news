<template>
    <NavBar />

    <el-row justify="center">
        <el-col :span="18">
            <el-card class="box-card">
                <template #header>
                    <div class="card-header">
                        <span>我的评论</span>

                    </div>
                </template>

                <el-table :data="comments" style="width: 100%">
                    <el-table-column prop="content" label="评论内容" />
                    <el-table-column prop="newsTitle" label="新闻标题" />
                    <el-table-column prop="createTime" label="评论时间" />
                    <el-table-column label="操作">
                        <template #default="scope">
                            <el-button size="small" @click="handleView(scope.row)">
                                查看
                            </el-button>
                            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-card>
        </el-col>
    </el-row>



    <FooterBar />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCommentListUsingGet, deleteCommentByIdUsingDelete } from '@/api/commentController'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { ElMessage } from 'element-plus'

import { useRouter } from 'vue-router'
import { useUserStoreHook } from '@/stores/modules/user'

const router = useRouter()
const useUserStore = useUserStoreHook()
const comments = ref([])

const fetchComments = async () => {
    try {
        const res = await getCommentListUsingGet({ userId: useUserStore.user.id })
        comments.value = res.data
    } catch (error) {
        console.error('获取评论失败', error)
    }
}


/**
 * 查看新闻详情
 * @param row 当前浏览记录项
 */
const handleView = (row) => {
    router.push(`/news/` + row.newsId.toString())
}

const handleDelete = async (id: number) => {
    try {
        await deleteCommentByIdUsingDelete(id)
        ElMessage.success('删除成功')
        fetchComments()
    } catch (error) {
        console.error('删除失败', error)
    }
}

onMounted(() => {
    fetchComments()
})
</script>

<style scoped>
.box-card {
    margin: 20px;
    margin-top: 80px;
    min-height: 600px;
}
</style>
