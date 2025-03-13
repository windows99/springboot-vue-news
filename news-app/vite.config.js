import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// 引入resolve
import { resolve } from 'path'
/** 路径查找 */
const pathResolve = (dir) => {
  return resolve(__dirname, ".", dir);
};

// 文档: https://vitejs.dev/config/ 
export default defineConfig({
  plugins: [vue()],

  resolve: {
    // 设置路径别名
    alias: {
      "@": pathResolve("src"),
      "@build": pathResolve("build")
    },
  }
})