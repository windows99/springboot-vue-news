<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="queryRequest">
        <el-form-item label="用户名">
          <el-input v-model="queryRequest.userName" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryRequest.userRole" placeholder="请选择用户角色" clearable>
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序条件">
          <el-select v-model="queryRequest.sortField" placeholder="请选择排序条件" clearable>
            <el-option label="创建时间" value="createTime" />
            <el-option label="更新时间" value="updateTime" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序顺序">
          <el-select v-model="queryRequest.sortOrder" placeholder="请选择排序顺序" clearable>
            <el-option label="升序" value="asc" />
            <el-option label="降序" value="desc" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="primary" @click="listUserByPage">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <div style="margin-bottom: 15px" />
    <el-card class="box-card">
      <div class="header">
        <h3 class="title">用户列表</h3>
        <div class="config">
          <el-button :icon="Plus" type="primary" @click="showAdd">新建
          </el-button>
        </div>
      </div>
      <!--  展示表格-->
      <el-table :data="pageUserList?.records" style="width: 100%" table-layout="auto">
        <el-table-column fixed type="index" :index="1" />
        <el-table-column fixed prop="userName" label="用户名" />
        <!--    头像-->
        <el-table-column label="头像">
          <template #default="scope">
            <div style="display: flex; align-items: center">
              <el-avatar shape="square" :src="scope.row.userAvatar" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="电子邮箱" />
        <el-table-column prop="userProfile" label="个人简介" />
        <!--    性别-->
        <el-table-column label="性别">
          <template #default="scope">
            {{ scope.row.gender === 1 ? "男" : "女" }}
          </template>
        </el-table-column>
        <!--    用户权限-->
        <el-table-column prop="userRole" label="用户角色" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column fixed="right" label="操作">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="editUser(scope.row)">编辑
            </el-button>
            <el-button link type="danger" size="small" @click="deleteUser(scope.row.id)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!--  底部分页选择-->
      <el-pagination style="margin-top: 15px" v-model:current-page="pageUserList.current"
        v-model:page-size="pageUserList.size" :page-sizes="[5, 10, 15, 20]" :small="false" :disabled="false"
        :background="false" layout="total, sizes, prev, pager, next, jumper" :page-count="parseInt(pageUserList.pages)"
        :total="parseInt(pageUserList.total)" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
    <!--  编辑信息对话框-->
    <el-dialog v-model="editFormVisible" title="编辑用户信息" style="width: 580px">
      <el-form :model="currentUser">
        <el-form-item label="用户名" :label-width="formLabelWidth">
          <el-input v-model="currentUser.userName" autocomplete="off" />
        </el-form-item>
        <!--      头像修改-->
        <el-form-item label="头像" :label-width="formLabelWidth">
          <!--          自定义组件-->
          <upload-avatar v-model:img-url="currentUser.userAvatar" biz="user_avatar" />
        </el-form-item>
        <el-form-item label="简介" :label-width="formLabelWidth">
          <el-input v-model="currentUser.userProfile" autocomplete="off" />
        </el-form-item>
        <el-form-item label="个人邮箱" :label-width="formLabelWidth">
          <el-input v-model="currentUser.email" autocomplete="off" />
        </el-form-item>
        <!--      性别选项-->
        <el-form-item label="性别" :label-width="formLabelWidth">
          <el-select v-model="currentUser.gender" placeholder="Select">
            <el-option v-for="gender in genderOptions" :key="gender.value" :label="gender.label"
              :value="gender.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" @click="confirmEdit"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>

    <!--    新增用户-->
    <el-dialog v-model="showAddDialog" title="新增用户" width="30%" center draggable>
      <el-form label-position="right" label-width="80px" :model="addUserInfo" style="max-width: 460px">
        <el-form-item label="用户名">
          <el-input v-model="addUserInfo.userName" />
        </el-form-item>
        <!--      头像修改-->
        <el-form-item label="头像">
          <!--          自定义组件-->
          <upload-avatar v-model:img-url="addUserInfo.userAvatar" biz="user_avatar" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="addUserInfo.userPassword" />
        </el-form-item>
        <el-form-item label="个人邮箱">
          <el-input v-model="addUserInfo.email" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input type="textarea" v-model="addUserInfo.userProfile" />
        </el-form-item>
        <el-form-item label="性别">
          <el-switch v-model="addUserInfo.gender" style="
              --el-switch-on-color: #99daf8;
              --el-switch-off-color: #f077ac;
            " inline-prompt active-text="男" inactive-text="女" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="用户角色">
          <el-select v-model="addUserInfo.userRole" class="m-2">
            <el-option key="管理员" label="管理员" value="admin" />
            <el-option key="普通用户" label="普通用户" value="user" />
            <el-option key="禁用" label="禁用" value="ban" />
            <el-option key="编辑" label="编辑" value="editor" />
            <el-option key="审核" label="审核" value="manage" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeAdd">取消</el-button>
          <el-button type="primary" @click="confirmAdd"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { message } from "@/utils/message";
import { markRaw, onMounted, ref } from "vue";
import { ElInput, ElMessage, ElMessageBox } from "element-plus";
import { Delete, Plus } from "@element-plus/icons-vue";
import {
  addUserUsingPost,
  deleteUserUsingPost,
  listUserByPageUsingPost,
  updateUserUsingPost
} from "@/api/userController";
import UploadAvatar from "@/components/ImageUploader/UploaderAvatar.vue";

defineOptions({
  name: "UserManage"
});

const queryRequest = ref<API.UserQueryRequest>({
  /**
   * 当前页号
   */
  current: 1,
  /**
   * 页面大小
   */
  pageSize: 10
});

const pageUserList = ref<API.PageUser_>({
  current: 0,
  pages: 0,
  records: undefined,
  size: 0,
  total: 0
});

// 初始化用户数据
const listUserByPage = async () => {
  const { sortField, sortOrder } = queryRequest.value;
  if (Boolean(sortField) !== Boolean(sortOrder)) {
    return ElMessage.error("请同时设置排序条件和排序字段");
  }
  const res = await listUserByPageUsingPost(queryRequest.value);
  if (res.code === 0) {
    pageUserList.value = res.data;
  } else {
    message(res.message, { type: "error" });
  }
};

/**
 * 充值查询
 */
const resetQuery = () => {
  queryRequest.value = {};
  listUserByPage();
};

// 删除确认框
const deleteUser = (id) => {
  ElMessageBox.confirm("注意，此操作不可撤销，是否继续？", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
    icon: markRaw(Delete),
    draggable: true
  })
    .then(async () => {
      console.log(id)
      // 删除用户
      const res = await deleteUserUsingPost({ id: id });
      if (res.code === 0) {
        message("删除成功", { type: "success" });
        await listUserByPage();
      } else {
        message(res.message, { type: "error" });
      }
    })
    .catch(() => {
      ElMessage({
        type: "info",
        message: "取消删除"
      });
    });
};

// 编辑用户信息
const editFormVisible = ref(false);
const formLabelWidth = "100px";
// 当前修改用户
const currentUser = ref<API.UserUpdateRequest>({});
// 性别选项
const genderOptions = [
  {
    value: 1,
    label: "男"
  },
  {
    value: 0,
    label: "女"
  }
];
// 修改用户对话框
const editUser = (user: API.LoginUserVO) => {
  editFormVisible.value = true;
  currentUser.value = Object.assign({}, user);
};
// 取消修改
const cancelEdit = () => {
  editFormVisible.value = false;
  currentUser.value = {};
};
// 确认修改
const confirmEdit = async () => {
  const res = await updateUserUsingPost(currentUser.value);
  if (res.code === 0) {
    message("修改成功", { type: "success" });
    await listUserByPage();
    cancelEdit();
  } else {
    message(res.message, { type: "error" });
  }
};
/**
 * 分页
 */
const handleSizeChange = (val: number) => {
  queryRequest.value.pageSize = val;
  queryRequest.value.current = 1;
  listUserByPage();
};
const handleCurrentChange = (val: number) => {
  queryRequest.value.current = val;
  listUserByPage();
};

onMounted(() => {
  listUserByPage();
});

/**
 * 新增用户
 */
const showAddDialog = ref(false);
const addUserInfo = ref<API.UserAddRequest>({});

const showAdd = () => {
  showAddDialog.value = true;
};

const closeAdd = () => {
  showAddDialog.value = false;
  addUserInfo.value = {};
};

const confirmAdd = async () => {
  showAddDialog.value = false;
  const res = await addUserUsingPost(addUserInfo.value);
  if (res.code !== 0) {
    return ElMessage.error(res.message);
  }
  addUserInfo.value = {};
  ElMessage.success("添加成功");
  await listUserByPage();
};
</script>

<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}

.header {
  display: flex;
  justify-content: space-between;
}
</style>
