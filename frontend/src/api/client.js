import axios from 'axios';

export default axios.create({
  baseURL: '/'  // Nginx en producción hará proxy /api → python-service:5000
});
