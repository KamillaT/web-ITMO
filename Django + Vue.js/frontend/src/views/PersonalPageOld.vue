<template>
  <div class="personal-page">
    <h2>Your Personal Page</h2>
    <div v-if="user">
      <p><strong>Name:</strong> {{ user.username }}</p>
      <p><strong>Email:</strong> {{ user.email }}</p>
      <button @click="logout">Log Out</button>
    </div>
    <div v-else>
      <p>Loading your personal information...</p>
    </div>
  </div>
</template>

<script>
import { api } from '../api';

export default {
  name: 'PersonalPage',
  props: {
    email: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      user: null,
    };
  },
  async created() {
    await this.fetchPersonalPage();
  },
  methods: {
    async fetchPersonalPage() {
      try {
        const response = await api.getPersonalPage(this.email);
        this.user = response.data;
      } catch (error) {
        console.error('Error fetching personal page:', error);
      }
    },
    logout() {
      // Emit logout event to parent
      this.$emit('user-logged-out');
    },
  },
};
</script>

<style scoped>
.personal-page {
  max-width: 400px;
  margin: 0 auto;
}

h2 {
  text-align: center;
}

p {
  font-size: 16px;
  margin: 10px 0;
}

button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 10px;
  width: 100%;
  cursor: pointer;
}

button:hover {
  background-color: #c82333;
}
</style>
