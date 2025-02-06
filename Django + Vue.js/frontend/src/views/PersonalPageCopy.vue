<template>
  <div class="personal-page">
    <h2>Your Personal Page</h2>
    <div v-if="user">
      <p><strong>Email:</strong> {{ user.email }}</p>
      <p><strong>Username:</strong> {{ user.username }}</p>
    </div>

    <h3>Graph</h3>
    <canvas id="graphCanvas" width="800" height="800" @click="handleCanvasClick"></canvas>

    <h3>Add Points</h3>
    <form @submit.prevent="submitFormPoint">
      <div>
        <input
            type="number"
            v-model="point.x"
            placeholder="X"
            value="1.0"
            required
        />
      </div>
      <div>
        <input
            type="number"
            v-model="point.y"
            placeholder="Y"
            value="1.0"
            required
        />
      </div>
      <div>
        <input
            type="number"
            v-model="point.r"
            placeholder="R"
            value="1.0"
            required
        />
      </div>
      <div>
        <button type="submit">Check!</button>
      </div>
    </form>

    <h3>Your Points</h3>
    <table v-if="points.length">
      <thead>
      <tr>
        <th>X</th>
        <th>Y</th>
        <th>R</th>
        <th>Is Inside</th>
        <th>Execution Time</th>
        <th>Created At</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="point in points" :key="point.id">
        <td>{{ point.x }}</td>
        <td>{{ point.y }}</td>
        <td>{{ point.r }}</td>
        <td>{{ point.is_inside ? 'Yes' : 'No' }}</td>
        <td>{{ point.execution_time }} ms</td>
        <td>{{ new Date(point.created_at).toLocaleString() }}</td>
      </tr>
      </tbody>
    </table>
    <p v-else>No points found.</p>

    <button @click="logout">Logout</button>
  </div>
</template>

<script>
import { api } from '../api';

export default {
  name: 'PersonalPage',
  data() {
    return {
      user: null,
      points: [],
      point: {
        x: 1.0,
        y: 1.0,
        r: 1.0,
      },
    };
  },
  async created() {
    await this.fetchPersonalPage();
    await this.fetchPoints();
    // this.drawGraph();
  },
  methods: {
    async fetchPersonalPage() {
      try {
        const response = await api.getPersonalPage();
        this.user = response.data;
      } catch (error) {
        console.error('Error fetching personal page:', error);
      }
    },
    async fetchPoints() {
      try {
        const authToken = localStorage.getItem('authToken'); // Fetch the token
        const response = await api.getPoints(authToken); // Pass the token to the API call
        if (Array.isArray(response.data) && response.data.length > 0) {
          this.points = response.data;
          this.drawGraph();
          this.points.forEach((point) => this.drawPoint(point));
        }
      } catch (error) {
        console.error('Error fetching points:', error);
      }
    },
    handleCanvasClick(event) {
      const canvas = document.getElementById('graphCanvas');
      const rect = canvas.getBoundingClientRect();
      const x = (event.clientX - rect.left - 400) / 50; // Adjust for canvas center and scale
      const y = (400 - (event.clientY - rect.top)) / 50;

      const pointData = {x, y, r: 1}; // Default R value

      this.submitPoint(pointData);
    },
    async submitPoint(pointData) {
      try {
        const authToken = localStorage.getItem('authToken'); // Fetch the token
        const response = await api.submitPoint(authToken, pointData); // Pass the token to the API call
        if (Array.isArray(this.points)) {
          this.points.push(response.data);
          this.drawPoint(response.data);
        }
      } catch (error) {
        console.error('Error submitting point:', error);
      }
    },
    async submitFormPoint() {
      if (this.point.x < -5 || this.point.y > 5 || this.point.y < -5 || this.point.x > 5 ||
          this.point.r < 0 || this.point.r > 5) {
        console.log("Not correct coordinates");
      } else {
        const x = this.point.x;
        const y = this.point.y;
        const r = this.point.r;
        const pointData = {x, y, r};

        await this.submitPoint(pointData);
      }
    },
    drawGraph() {
      const canvas = document.getElementById('graphCanvas');
      const context = canvas.getContext('2d');

      // Clear canvas
      context.clearRect(0, 0, canvas.width, canvas.height);

      // Draw axes
      context.strokeStyle = '#000';
      context.beginPath();
      context.moveTo(canvas.width / 2, 0);
      context.lineTo(canvas.width / 2, canvas.height);
      context.stroke();
      context.beginPath();
      context.moveTo(0, canvas.height / 2);
      context.lineTo(canvas.width, canvas.height / 2);
      context.stroke();

      // Draw grid
      for (let i = 0; i <= canvas.width; i += 50) {
        context.strokeStyle = '#ddd';
        context.beginPath();
        context.moveTo(i, 0);
        context.lineTo(i, canvas.height);
        context.stroke();

        context.beginPath();
        context.moveTo(0, i);
        context.lineTo(canvas.width, i);
        context.stroke();
      }
    },
    drawPoint(point) {
      const canvas = document.getElementById('graphCanvas');
      const context = canvas.getContext('2d');
      const x = canvas.width / 2 + point.x * 50;
      const y = canvas.height / 2 - point.y * 50;

      context.fillStyle = point.is_inside ? 'green' : 'red';
      context.beginPath();
      context.arc(x, y, 5, 0, 2 * Math.PI);
      context.fill();
    },
    logout() {
      localStorage.removeItem('authToken');
      this.$emit('user-logged-out');
    },
  },
};
</script>

<style scoped>
.personal-page {
  max-width: 800px;
  margin: 0 auto;
}

canvas {
  border: 1px solid #ddd;
  margin: 20px auto;
  display: block;
}
</style>
