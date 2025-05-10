<template>
  <el-footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h4>关于我们</h4>
        <p>新闻网站致力于提供最新、最全面的新闻资讯</p>
      </div>
      <div class="footer-section">
        <h4>联系我们</h4>
        <p>电话: 176-3176-6054</p>
        <p>邮箱: wsk_web@163.com</p>
      </div>
      <div class="footer-section">
        <h4>关注我们</h4>
        <div class="social-links">
          <el-link :underline="false" href="#">
            <i class="el-icon-weibo"></i>
          </el-link>
          <el-link :underline="false" href="#">
            <i class="el-icon-wechat"></i>
          </el-link>
          <el-link :underline="false" href="#">
            <i class="el-icon-qq"></i>
          </el-link>
        </div>
      </div>
    </div>
    <div class="copyright">
      &copy; 2025 新闻网站. 保留所有权利
    </div>
  </el-footer>
</template>

<script setup>

</script>

<style scoped>
.footer {
  background-color: #2c3e50;
  color: #fff;
  padding: 40px 20px;
  text-align: center;
  height: 100%;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 20px;
}

.footer-section {
  flex: 1;
  min-width: 200px;
  margin-bottom: 20px;
}

.footer-section h4 {
  margin-bottom: 15px;
  font-size: 18px;
}

.footer-section p {
  margin: 5px 0;
  color: #bdc3c7;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.social-links .el-link {
  color: #fff;
  font-size: 24px;
}

.social-links .el-link:hover {
  color: #42b983;
}

.copyright {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #34495e;
  color: #bdc3c7;
  font-size: 14px;
}
</style>
