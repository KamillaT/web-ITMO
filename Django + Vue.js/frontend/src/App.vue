<template>
  <div id="app">
    <h1>Welcome to the User App</h1>
    <RegistrationForm v-if="!isLoggedIn" @user-logged-in="onUserLoggedIn" />
    <PersonalPage
        v-else
        :email="userEmail"
        @user-logged-out="onUserLoggedOut"
    />
  </div>
</template>

<script>
import RegistrationForm from './components/RegistrationForm.vue';
import PersonalPage from './views/PersonalPage.vue';

export default {
  name: 'App',
  components: {
    RegistrationForm,
    PersonalPage,
  },
  data() {
    return {
      isLoggedIn: false,
      userEmail: null,
    };
  },
  methods: {
    onUserLoggedIn(userData) {
      this.isLoggedIn = true;
      this.userEmail = userData.email;
    },
    onUserLoggedOut() {
      this.isLoggedIn = false;
      this.userEmail = null;
    },
  },
};
</script>

<style src="./assets/style/screenSize.css"></style>
<style>
body {
  font-family: Arial, sans-serif;
  margin: 20px;
}

h1 {
  color: #333;
}
</style>
