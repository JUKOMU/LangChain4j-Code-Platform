<template>
  <div class="no-auth-page">
    <h1>暂无权限</h1>
    <p>当前账号没有访问该页面的权限。</p>
    <RouterLink to="/">返回首页</RouterLink>
  </div>
</template>

<style scoped>
.no-auth-page {
  text-align: center;
  padding: 80px 20px;
}

.no-auth-page h1 {
  margin-bottom: 12px;
  color: #10263a;
}

.no-auth-page p {
  color: #6f8094;
  margin-bottom: 16px;
}
</style>
