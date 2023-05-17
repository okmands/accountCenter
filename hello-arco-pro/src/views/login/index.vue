<template>
  <div class="container">
    <div class="content">
      <div class="content-inner" style="z-index: 100">
        <LoginForm />
      </div>
      <div class="footer">
        <Footer />
      </div>
    </div>
    <!--    <div class="logo">-->
    <!--      <img-->
    <!--        alt="logo"-->
    <!--        src="/src/assets/images/XiYaLogo.png"-->
    <!--        style="width: 40px"-->
    <!--      />-->
    <!--      <div class="logo-text">西亚和美</div>-->
    <!--    </div>-->
    <LoginBanner />
  </div>
</template>

<script lang="ts" setup>
  import Footer from '@/components/footer/index.vue';
  import LoginBanner from './components/banner.vue';
  import LoginForm from './components/login-form.vue';
</script>

<style lang="less" scoped>
  .container {
    display: flex;
    height: 100vh;
    background: linear-gradient(163.85deg, #1d2129 0%, #00308f 100%);

    .banner {
      position: absolute;
      bottom: 0;
      left: 0;
      width: 100%;
      height: 30%;
    }

    .content {
      position: relative;
      display: flex;
      flex: 1;
      align-items: center;
      justify-content: center;
      padding-bottom: 40px;
    }

    .footer {
      position: absolute;
      right: 0;
      bottom: 0;
      width: 100%;
    }
  }

  .logo {
    position: fixed;
    top: 24px;
    left: 22px;
    z-index: 1;
    display: inline-flex;
    align-items: center;

    &-text {
      margin-right: 4px;
      margin-left: 4px;
      color: var(--color-fill-1);
      font-size: 20px;
    }
  }
</style>

<style lang="less" scoped>
  // responsive
  @media (max-width: @screen-lg) {
    .container {
      .banner {
        width: 25%;
      }
    }
  }
</style>
