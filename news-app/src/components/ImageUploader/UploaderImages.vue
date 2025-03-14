<template>
  <el-upload
    v-model:file-list="props.fileList"
    list-type="picture-card"
    :on-preview="handlePictureCardPreview"
    :on-remove="handleRemove"
    :before-upload="beforeAvatarUpload"
    :http-request="updateAvatar"
  >
    <el-icon>
      <Plus />
    </el-icon>
  </el-upload>
  <el-dialog v-model="dialogVisible" width="30%">
    <img :src="dialogImageUrl" alt="Preview Image" />
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage, UploadProps } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { uploadFileUsingPost } from "@/api/fileController";

const dialogImageUrl = ref("");
const dialogVisible = ref(false);
const props = defineProps(["fileList", "biz"]);
const $emit = defineEmits(["update:fileList"]);

const handleRemove: UploadProps["onRemove"] = (uploadFile, uploadFiles) => {
  $emit("update:fileList", props.fileList);
};

const handlePictureCardPreview: UploadProps["onPreview"] = uploadFile => {
  dialogImageUrl.value = uploadFile.url!;
  dialogVisible.value = true;
};

// 校验文件格式
const fileType = ["image/jpeg", "image/png"];
const beforeAvatarUpload = rawFile => {
  if (!fileType.includes(rawFile.type)) {
    ElMessage.error("头像必须为JPG/PNG格式!");
    return false;
  } else if (rawFile.size / 1024 / 1024 > 3) {
    ElMessage.error("文件大小不能超过3MB!");
    return false;
  }
  return true;
};

const updateAvatar = rawFile => {
  return new Promise((resolve, reject) => {
    uploadFileUsingPost(
      {
        biz: props.biz
      },
      {},
      rawFile?.file
    ).then(res => {
        if (res.code === 0) {
          const existingFile = props.fileList.find(file =>
            file.url.includes("blob:")
          );
          if (existingFile) {
            // 如果存在，删除旧的文件对象
            props.fileList.splice(props.fileList.indexOf(existingFile), 1);
          }
          props.fileList.push({
            name: props.biz,
            url: res.data
          });
          console.log(props.fileList);
          $emit("update:fileList", props.fileList);
          ElMessage.success("上传成功");
          resolve(true); // 成功时将Promise对象状态设为fulfilled
        } else {
          ElMessage.error(res.message);
          reject(res.message); // 失败时将Promise对象状态设为rejected，并返回错误信息
        }
      })
      .catch(error => {
        ElMessage.error("上传失败");
        reject(error); // 报错时将Promise对象状态设为rejected，并返回错误对象
      });
  });
};
</script>

<style scoped></style>
